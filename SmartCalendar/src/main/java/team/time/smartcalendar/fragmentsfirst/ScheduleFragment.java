package team.time.smartcalendar.fragmentsfirst;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentScheduleBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.service.MyLocationService;
import team.time.smartcalendar.utils.*;
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
    private boolean isFirst;

    @Inject
    ApiService apiService;
    @Named("all")
    @Inject
    List<CalendarItem> calendarItems;
    @Inject
    CalendarItemDao dao;
    @Named("category")
    @Inject
    List<String> categories;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = getActivity();

        isFirst = true;

        checkPermission();

        // 启动定位服务
        Intent intent=new Intent(parentActivity, MyLocationService.class);
        parentActivity.startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_schedule,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"日程",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        if(isFirst){
            isFirst=false;

            // 工作线程发起网络请求，同步方法
            if(categories.isEmpty() || categories.size()==1){
                categories.clear();
                requestCategories();
            }
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
        }else {
            setSpinnerAdapter();
        }

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.action.imageLeft.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
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

        binding.imagePosition.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());

            Bundle bundle=new Bundle();
            bundle.putSerializable("viewModel",viewModel);
            controller.navigate(R.id.action_scheduleFragment_to_positionFragment,bundle);
        });

        binding.textStartTime.setOnClickListener(v -> {
            showPicker(viewModel.getStartTime().getValue(),true);
        });

        binding.textEndTime.setOnClickListener(v -> {
            showPicker(viewModel.getEndTime().getValue(),false);
        });

        binding.imagePositionIcon.setOnClickListener(v -> {
            if(!viewModel.getPosition().getValue().equals("")){
                viewModel.getPosition().setValue("");
                viewModel.latitude=0.0;
                viewModel.longitude=0.0;
            }
        });
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
        viewList.add(binding.editTextDetail);
        return viewList;
    }

    private void setViewModel() {
        viewModel.getInfo().setValue(item.info);

        viewModel.getPosition().setValue(item.position);
        viewModel.latitude=item.latitude;
        viewModel.longitude=item.longitude;

        viewModel.getStartTime().setValue(new Date(item.startTime));
        viewModel.getEndTime().setValue(new Date(item.endTime));
        viewModel.getDetails().setValue(item.details);
    }

    private void doItem(){
        item.info=viewModel.getInfo().getValue();
        item.startTime=viewModel.getStartTime().getValue().getTime();
        item.endTime=viewModel.getEndTime().getValue().getTime();
        item.details=viewModel.getDetails().getValue();

        item.position=viewModel.getPosition().getValue();
        item.latitude= viewModel.latitude;
        item.longitude= viewModel.longitude;

        item.categoryId=getCategoryId(binding.spinnerCategory.getSelectedItemPosition());
        item.categoryName=binding.spinnerCategory.getSelectedItem().toString();

        item.username= UserUtils.USERNAME;
    }

    private void updateItem(){
        doItem();
        // 通知服务器修改日程
        boolean[] isSuccess=new boolean[1];
        requestUpdateItems(isSuccess);
        // 判断dirty值
        if(isSuccess[0]){
            item.dirty=0;
        }else {
            item.dirty=2;
        }
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
        if(categories.size()==1){
            return 1;
        }
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
                    }
                    categories.add("其他");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // 请求失败
                categories.add("其他");
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
            RequestBody requestBody= RequestUtils.createAddOrUpdateRequestBody(item);
            try {
                Response<ResponseBody> response=apiService.update(requestBody).execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestUpdateItems: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        isSuccess[0]=true;
                    }
                }catch (JSONException e){
                    Log.d("lmx", "requestUpdateItems: "+e);
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

    private void requestUpdateItems(boolean[] isSuccess) {
        Thread thread=new Thread(() -> {
            RequestBody requestBody= RequestUtils.createAddOrUpdateRequestBody(item);
            try {
                Response<ResponseBody> response=apiService.add(requestBody).execute();
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "requestUpdateItems: "+result);
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

    private void checkPermission() {
        if(Build.VERSION.SDK_INT>=23 && parentActivity.getApplicationInfo().targetSdkVersion>=23){
            List<String> permissionList=new ArrayList<>();
            if(ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if(ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (!permissionList.isEmpty()){
                SystemUtils.checkPermission(parentActivity,permissionList);
            }
        }
    }
}