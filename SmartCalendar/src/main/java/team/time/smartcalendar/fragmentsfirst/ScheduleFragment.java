package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.dataBeans.ScheduleItem;
import team.time.smartcalendar.databinding.FragmentScheduleBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.ColorUtils;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;
import team.time.smartcalendar.viewmodels.ScheduleViewModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@AndroidEntryPoint
public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private NavController controller;
    private ScheduleViewModel viewModel;
    private Bundle bundle;
    private CalendarItem item;
    private long time;
    private Activity parentActivity;
    private List<String> categories;

    @Inject
    ApiService apiService;
    @Named("all")
    @Inject
    List<CalendarItem> calendarItems;
    @Inject
    CalendarItemDao dao;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = getActivity();
        categories=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        setBack();

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_schedule,container,false);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        // 工作线程发起网络请求，同步方法
        requestCategories();
        setSpinnerAdapter();

        // 接收参数
        bundle = getArguments();
        if(bundle!=null){
            item = (CalendarItem) bundle.getSerializable("item");
            time = bundle.getLong("time");

            // 修改日程
            if(item!=null){
                setViewModel();
                setSpinnerSelection();
            }

            // 添加日程
            if(time!=0){
                viewModel.getStartTime().setValue(new Date(time));
                viewModel.getEndTime().setValue(new Date(time+30 * DateUtils.A_MIN_MILLISECOND));
            }
        }

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.imageDrop.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            controller.popBackStack();
        });

        binding.imageFinish.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());

            if((viewModel.getStartTime().getValue().getTime())>(viewModel.getEndTime().getValue().getTime())){
                Toast.makeText(parentActivity, "开始时间晚于结束时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if(item==null){
                createItem();
            }else {
                updateItem();
            }

            controller.popBackStack();
        });

        binding.textStartTime.setOnClickListener(v -> {
            showPicker(viewModel.getStartTime().getValue(),true);
        });

        binding.textEndTime.setOnClickListener(v -> {
            showPicker(viewModel.getEndTime().getValue(),false);
        });
    }

    private void setBack() {
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(ScheduleFragment.this).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    private void showPicker(Date time,boolean isStart) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(time);

        TimePickerView view=new TimePickerBuilder(getContext(), (date, v) -> {
            date=DateUtils.getMinDate(date);
            if(isStart){
                viewModel.getStartTime().setValue(date);
            }else {
                viewModel.getEndTime().setValue(date);
            }
        })
                .setDate(calendar)
                .setSubmitColor(ColorUtils.DoDodgerBlue)
                .setCancelColor(ColorUtils.DoDodgerBlue)
                .setType(new boolean[]{true,true,true,true,true,false}) // 年、月、日、时、分、秒
                .setOutSideCancelable(true) // 点击外围取消
                .setItemVisibleCount(5)
                .isCyclic(true) // 循环
                .setLabel("","",""," :","","")
                .isCenterLabel(true)
                .setLineSpacingMultiplier(3.0F) // 间距
                .isDialog(true) // 以Dialog形式显示
                .build();
        view.show();
    }

    private List<View> getEditTextList(){
        List<View> viewList=new ArrayList<>();
        viewList.add(binding.editTextTittle);
        viewList.add(binding.editTextPosition);
        viewList.add(binding.editTextDetail);
        return viewList;
    }

    private void setViewModel() {
        viewModel.getInfo().setValue(item.info);
        viewModel.getPosition().setValue(item.position);
        viewModel.getStartTime().setValue(new Date(item.startTime));
        viewModel.getEndTime().setValue(new Date(item.endTime));
        viewModel.getDetails().setValue(item.details);
    }

    private void doItem(){
        item.info=viewModel.getInfo().getValue();
        item.position=viewModel.getPosition().getValue();
        item.startTime=viewModel.getStartTime().getValue().getTime();
        item.endTime=viewModel.getEndTime().getValue().getTime();
        item.details=viewModel.getDetails().getValue();

        item.categoryId=getCategoryId(binding.spinnerCategory.getSelectedItemPosition());
        item.categoryName=binding.spinnerCategory.getSelectedItem().toString();

        item.username= UserUtils.USERNAME;
    }

    private void updateItem(){
        doItem();
        // 通知服务器修改日程
        // 判断dirty值
        item.dirty=2;
        // 更新数据库
        updateLocalItems();
    }

    private void createItem(){
        item=new CalendarItem();
        item.uuid=UUID.randomUUID().toString().toUpperCase();
        doItem();

        // 通知服务器添加日程
        boolean[] isSuccess=new boolean[1];
        requestAddItems(isSuccess);
        // 判断dirty值
        if(isSuccess[0]){
            item.dirty=0;
        }else {
            item.dirty=1;
        }
        // 本地创建日程
        calendarItems.add(item);
        Log.d("lmx", "createItem: "+calendarItems);
        // 添加到数据库
        addLocalItems();
    }

    private void setSpinnerAdapter(){
        ArrayAdapter<String> adapter=new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setSpinnerSelection() {
        int index=categories.indexOf(item.categoryName);
        if(index<0){
            binding.spinnerCategory.setSelection(categories.size()-1);
        }else {
            binding.spinnerCategory.setSelection(index);
        }
    }

    // 计算服务器提供的category的ID
    private int getCategoryId(int index){
        return (index+2) % categories.size();
    }

    private int getCategoryIndex(int id){
        return id==1?categories.size()-1:id-2;
    }

    private void requestCategories() {
        Thread thread=new Thread(() -> {
            try {
                retrofit2.Response<ResponseBody> response=apiService.getCategory().execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        JSONArray categoryArray=result.getJSONArray("category");

                        for(int i=1;i<categoryArray.length();i++){
                            categories.add(categoryArray.getJSONObject(i).getString("name"));
                        }
                    }else {
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                        });
                    }
                    categories.add("其他");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // 请求失败
                categories.add("其他");
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

    private void requestAddItems(boolean[] isSuccess) {
        Thread thread=new Thread(() -> {
            ScheduleItem scheduleItem=new ScheduleItem(item);
            JSONObject body=new JSONObject();
            try {
                body.put("uuid",scheduleItem.uuid);
                body.put("name",scheduleItem.name);
                body.put("category",scheduleItem.categoryId);
                body.put("start",scheduleItem.start);
                body.put("end",scheduleItem.end);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody=RequestBody.create(
                    body.toString(),
                    MediaType.parse("application/json;charset=utf-8")
            );

            try {
                Response<ResponseBody> response=apiService.add(requestBody).execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestAddItems: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        isSuccess[0]=true;
                    }
                }catch (JSONException e){
                    Log.d("lmx", "requestAddItems: "+e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateLocalItems() {
        Thread thread=new Thread(() -> {
            if(item.id==0){
                item.id= dao.getIdByUUID(item.uuid);
            }
            dao.updateCalendarItem(item);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addLocalItems() {
        Thread thread=new Thread(() -> {
            dao.insertCalendarItem(item);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}