package team.time.smartcalendar.fragmentsfirst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.MainApplication;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CalendarRecyclerViewAdapter;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.dataBeans.ScheduleItem;
import team.time.smartcalendar.databinding.FragmentCalendarBinding;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.viewmodels.CalendarViewModel;

import java.io.IOException;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;
    private boolean isFirst;
    private boolean isAdded;
    private boolean[] isUpdated;
    private MainApplication app;
    private Activity parentActivity;
    private CalendarRecyclerViewAdapter adapter;
    private Calendar lastCalendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=getActivity();
        app = (MainApplication) getActivity().getApplication();

        // 请求日程列表
        app.getCalendarItems().clear();
        requestCalendarItems();

        isFirst = true;
        isAdded = false;
        isUpdated=new boolean[1];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,R.layout.fragment_calendar,container,false);
        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()))
                .get(CalendarViewModel.class);
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
        adapter = new CalendarRecyclerViewAdapter(parentActivity, binding.calendarView, isUpdated);
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
            bundle.putLong("time",binding.calendarView.getSelectedCalendar().getTimeInMillis());
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
            synchronized (app.getMap()){
                DateUtils.setMonthScheme(app.getMap(),app.getCalendarItems(),year,month);
                binding.calendarView.setSchemeDate(app.getMap());
            }
        }).start();
    }

    private void setCurrentScheduleList(long time) {
        // 清空当前日程列表
        app.getCurCalendarItems().clear();
        // 遍历总日程列表，加入符合条件的日程
        for(CalendarItem item:app.getCalendarItems()){
            if(DateUtils.includeItem(item,time)){
                app.getCurCalendarItems().add(item);
            }
        }
    }

    private void requestCalendarItems() {
        Thread thread=new Thread(() -> {
            String PATH="/calendar/fetch";

            Request request=new Request.Builder()
                    .url(getString(R.string.URL)+PATH)
                    .addHeader("contentType","application/json;charset=UTF-8")
                    .get()
                    .build();

            Call call=app.getClient().newCall(request);

            try {
                Response response=call.execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestCalendarItems: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        JSONArray scheduleItems=result.getJSONArray("schedule");
                        for(int i=0;i<scheduleItems.length();i++){
                            app.getCalendarItems().add(new CalendarItem(new ScheduleItem(scheduleItems.getJSONObject(i))));
                        }
                    }else {
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    Log.d("lmx", "requestCalendarItems: "+e);
                }
            } catch (IOException e) {
                parentActivity.runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
                });
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}