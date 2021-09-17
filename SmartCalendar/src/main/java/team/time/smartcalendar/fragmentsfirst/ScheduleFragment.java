package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import dagger.hilt.android.AndroidEntryPoint;
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

        SystemUtils.checkLocatePermission(parentActivity);

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
        SystemUtils.setAction(binding.action,"常规日程",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        if(isFirst){
            isFirst=false;

            // 工作线程发起网络请求，同步方法
            if(categories.isEmpty() || categories.size()==1){
                categories.clear();
                requestCategories();
            }
            ViewUtils.setSpinnerAdapter(binding.spinnerCategory,categories);

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
            ViewUtils.setSpinnerAdapter(binding.spinnerCategory,categories);
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
                .setSubCalSize(16)
                .setContentTextSize(16)
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
        ViewGroup container=view.getDialogContainerLayout();
        ViewGroup.LayoutParams params=container.getLayoutParams();
        params.width=900;
        container.setLayoutParams(params);
        view.show();
    }

    private List<View> getEditTextList(){
        List<View> viewList=new ArrayList<>();
        viewList.add(binding.editTextTittle);
        return viewList;
    }

    private void setViewModel() {
        viewModel.getInfo().setValue(item.info);

        viewModel.getPosition().setValue(item.position);
        viewModel.latitude=item.latitude;
        viewModel.longitude=item.longitude;

        viewModel.getStartTime().setValue(new Date(item.startTime));
        viewModel.getEndTime().setValue(new Date(item.endTime));
    }

    private void doItem(){
        item.info=viewModel.getInfo().getValue();
        item.startTime=viewModel.getStartTime().getValue().getTime();
        item.endTime=viewModel.getEndTime().getValue().getTime();

        item.position=viewModel.getPosition().getValue();
        item.latitude= viewModel.latitude;
        item.longitude= viewModel.longitude;

        item.categoryId=getCategoryId(binding.spinnerCategory.getSelectedItemPosition());
        item.categoryName=binding.spinnerCategory.getSelectedItem().toString();
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
        item.type=0;
        item.listId=0L;
        item.username= UserUtils.USERNAME;
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
        // 添加到数据库
        addLocalItems();
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
        return (index+2)>categories.size() ? (index+2)%categories.size() : (index+2);
    }

    private void requestCategories() {
        Thread thread=new Thread(() -> {
            RequestUtils.requestCategories(apiService,categories);
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
            RequestUtils.requestAddItems(apiService,isSuccess,item);
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
            RequestUtils.requestUpdateItems(apiService,isSuccess,item);
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