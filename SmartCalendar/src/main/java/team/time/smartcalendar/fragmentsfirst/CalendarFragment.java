package team.time.smartcalendar.fragmentsfirst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CalendarRecyclerViewAdapter;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.dataBeans.ScheduleItem;
import team.time.smartcalendar.databinding.FragmentCalendarBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.utils.RequestUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;
import team.time.smartcalendar.viewmodels.CalendarViewModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AndroidEntryPoint
public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;
    private boolean isFirst;
    private boolean isAdded;
    private boolean[] isUpdated;
    private Activity parentActivity;
    private CalendarRecyclerViewAdapter adapter;
    private Calendar lastCalendar;
    public List<CalendarItem> requestCalendarItems;

    @Inject
    public ApiService apiService;
    @Named("all")
    @Inject
    public List<CalendarItem> calendarItems;
    @Named("current")
    @Inject
    public List<CalendarItem>curCalendarItems;
    @Inject
    public Map<String, Calendar> map;
    @Inject
    public CalendarItemDao dao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=getActivity();

        requestCalendarItems=new ArrayList<>();

        if(calendarItems.isEmpty()){
            // 读取数据库日程
            readLocalCalendarItems();
            // 发送未同步日程，同步数据库
            postAndSynchronize();
        }

        isFirst = true;
        isAdded = false;
        isUpdated=new boolean[1];

        Log.d("lmx", "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,R.layout.fragment_calendar,container,false);

        SystemUtils.setStatusImage(binding.statusImage);

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* first start */
        if(isFirst){
            isFirst=false;

            lastCalendar=binding.calendarView.getSelectedCalendar();
            setCurrentScheduleList(DateUtils.getTimeStamp(binding.calendarView.getSelectedCalendar()));

            viewModel.getMonthDay().setValue(binding.calendarView.getCurMonth() + "月" + binding.calendarView.getCurDay() + "日");
            viewModel.getYear().setValue(binding.calendarView.getCurYear());
            viewModel.getLunar().setValue(binding.calendarView.getSelectedCalendar().getLunar());
            viewModel.getCurrentDay().setValue(binding.calendarView.getCurDay());
        }
        /* first end */

        setMonthSchedule(
                binding.calendarView.getSelectedCalendar().getYear(),
                binding.calendarView.getSelectedCalendar().getMonth()
        );

        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        binding.calendarRecyclerView.setLayoutManager(manager);
        adapter = new CalendarRecyclerViewAdapter(this, binding.calendarView, isUpdated);
        binding.calendarRecyclerView.setAdapter(adapter);

        binding.calendarView.setOnMonthChangeListener((year, month) -> {
            setMonthSchedule(year,month);
        });
        binding.calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if(!lastCalendar.equals(calendar) || isAdded || isUpdated[0]){
                    // 更新页面
                    viewModel.getMonthDay().setValue(calendar.getMonth() + "月" + calendar.getDay() + "日");
                    viewModel.getYear().setValue(calendar.getYear());
                    viewModel.getLunar().setValue(calendar.getLunar());
                    // 刷新当前列表
                    setCurrentScheduleList(DateUtils.getTimeStamp(calendar));
                    adapter.notifyDataSetChanged();
                    // 修改标记
                    isAdded=false;
                    isUpdated[0]=false;
                }
                lastCalendar=calendar;
            }
        });

        binding.textMonthDay.setOnClickListener(v -> {
            // 展开日历
            if(!binding.calendarLayout.isExpand()){
                binding.calendarLayout.expand();
            }else {
                binding.calendarLayout.shrink();
            }
        });

        binding.calendarFrameLayout.setOnClickListener(v -> {
            binding.calendarView.scrollToCurrent();
        });

        binding.btnSchedule.setOnClickListener(v -> {
            // 传参
            Bundle bundle=new Bundle();
            bundle.putLong("time",DateUtils.getMinDate(new Date(binding.calendarView.getSelectedCalendar().getTimeInMillis())).getTime());
            // 跳转
            NavController controller= Navigation.findNavController(v);
            controller.navigate(R.id.action_calendarFragment_to_scheduleFragment,bundle);
            // 标记
            isAdded=true;
        });
    }

    // 在日历上显示日程标签
    private void setMonthSchedule(int year,int month) {
        new Thread(() -> {
            synchronized (map){
                DateUtils.setMonthScheme(map,calendarItems,year,month);
                binding.calendarView.setSchemeDate(map);
            }
        }).start();
    }

    private void setCurrentScheduleList(long time) {
        Log.d("lmx", "setCurrentScheduleList: "+calendarItems);
        // 清空当前日程列表
        curCalendarItems.clear();
        // 遍历总日程列表，加入符合条件的日程
        for(CalendarItem item:calendarItems){
            if(DateUtils.includeItem(item,time)){
                curCalendarItems.add(item);
            }
        }
    }

    private void readLocalCalendarItems() {
        Thread thread=new Thread(() -> {
            List<CalendarItem>items=dao.getAllCalendarItems(UserUtils.USERNAME);
            // 不可改变引用！！！
            calendarItems.addAll(items);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("lmx", "readLocalCalendarItems: "+calendarItems);
    }

    private void postAndSynchronize() {
        // 发送未同步日程并更新数据库
        Thread thread1=new Thread(() -> {
            List<CalendarItem>calendarItems=dao.getSycCalendarItems(UserUtils.USERNAME);
            for(CalendarItem item:calendarItems){
                boolean isSuccess=false;

                RequestBody requestBody= RequestUtils.createAddOrUpdateRequestBody(item);

                if (item.dirty==1){
                    try {
                        Response<ResponseBody> response=apiService.add(requestBody).execute();
                        try {
                            JSONObject result=new JSONObject(response.body().string());
                            Log.d("lmx", "requestAddItems: "+result);
                            int status=result.getInt("status");
                            if(status==1){
                                isSuccess=true;
                            }
                        }catch (JSONException e){
                            Log.d("lmx", "requestAddItems: "+e);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(item.dirty==2){
                    try {
                        Response<ResponseBody> response=apiService.update(requestBody).execute();
                        try {
                            JSONObject result=new JSONObject(response.body().string());
                            Log.d("lmx", "requestUpdateItems: "+result);
                            int status=result.getInt("status");
                            if(status==1){
                                isSuccess=true;
                            }
                        }catch (JSONException e){
                            Log.d("lmx", "requestUpdateItems: "+e);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(isSuccess){
                    item.dirty=0;
                    dao.updateCalendarItem(item);
                }
            }
        });
        thread1.start();
        // 请求所有日程
        Thread thread2=new Thread(() -> {
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Response<ResponseBody> response=apiService.fetch().execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestCalendarItems: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        JSONArray scheduleItems=result.getJSONArray("schedule");
                        for(int i=0;i<scheduleItems.length();i++){
                            requestCalendarItems.add(new CalendarItem(new ScheduleItem(scheduleItems.getJSONObject(i))));
                        }
                    }
                } catch (Exception e) {
                    Log.d("lmx", "requestCalendarItemsException: "+e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        thread2.start();
        // 根据uuid更新数据库
        Thread thread3=new Thread(() -> {
            try {
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 确保请求是成功的
            if(!requestCalendarItems.isEmpty()){
                for(CalendarItem requestItem:requestCalendarItems){
                    List<String>uuids=dao.getAllUUID(UserUtils.USERNAME);
                    if(!uuids.contains(requestItem.uuid)){
                        Log.d("lmx", "insert: "+requestItem);
                        dao.insertCalendarItem(requestItem);
                    }
                }
            }

            List<CalendarItem>items=dao.getAllCalendarItems(UserUtils.USERNAME);
            calendarItems.clear();
            calendarItems.addAll(items);

            Log.d("lmx", "postAndSynchronize: "+calendarItems);
        });
        thread3.start();
    }
}