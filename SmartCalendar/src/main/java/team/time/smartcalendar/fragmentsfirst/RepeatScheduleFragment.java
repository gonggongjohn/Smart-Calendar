package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
import android.content.Intent;
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
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentRepeatScheduleBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.service.MyLocationService;
import team.time.smartcalendar.utils.*;
import team.time.smartcalendar.viewmodels.RepeatScheduleViewModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@AndroidEntryPoint
public class RepeatScheduleFragment extends Fragment {
    private FragmentRepeatScheduleBinding binding;
    private NavController controller;
    private Activity parentActivity;
    private RepeatScheduleViewModel viewModel;
    private CalendarItem item;
    private List<CalendarItem> items;
    private long time;
    private Bundle bundle;
    private boolean isFirst;
    private long listId;

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
        items=new ArrayList<>();

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

        viewModel = new ViewModelProvider(this).get(RepeatScheduleViewModel.class);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_repeat_schedule,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"定时任务",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        if(isFirst){
            isFirst=false;

            // 工作线程发起网络请求，同步方法
            Log.d("lmx", "categories.size: "+categories.size());
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
                    getItemList();
                    DateUtils.sortItemList(items);
                    setUpdateViewModel();
                    setSpinnerSelection();
                }

                // 添加日程
                if(time!=0){
                    // 设置日程链ID
                    listId=new Date().getTime();
                    setCreateViewModel();
                }
            }
        }else {
            ViewUtils.setSpinnerAdapter(binding.spinnerCategory,categories);
        }

        viewModel.getRepeat().observe(this, booleans -> {
            setRepeatText(booleans);
        });

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
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

            if((viewModel.getFirstStartTime().getValue().getTime())>(viewModel.getLastEndTime().getValue().getTime())){
                Toast.makeText(parentActivity, "起始时间晚于截止时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if((binding.textRepeatTime.getText().equals("未选择"))){
                Toast.makeText(parentActivity, "请选择重复时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if(item==null){
                createItems();
            }else {
                updateItems();
            }

            controller.popBackStack();
        });

        binding.imagePosition.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());

            Bundle bundle=new Bundle();
            bundle.putSerializable("viewModel",viewModel);
            controller.navigate(R.id.action_repeatScheduleFragment_to_positionFragment,bundle);
        });

        binding.textStartTime.setOnClickListener(v -> {
            showTimePicker(viewModel.getStartTime().getValue(),true);
        });

        binding.textEndTime.setOnClickListener(v -> {
            showTimePicker(viewModel.getEndTime().getValue(),false);
        });

        binding.textFirstStartTime.setOnClickListener(v -> {
            showDatePicker(viewModel.getFirstStartTime().getValue(),true);
        });

        binding.textLastEndTime.setOnClickListener(v -> {
            showDatePicker(viewModel.getLastEndTime().getValue(),false);
        });

        binding.imagePositionIcon.setOnClickListener(v -> {
            if(!viewModel.getPosition().getValue().equals("")){
                viewModel.getPosition().setValue("");
                viewModel.latitude=0.0;
                viewModel.longitude=0.0;
            }
        });

        binding.textRepeatTime.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            Bundle bundle=new Bundle();
            bundle.putSerializable("viewModel",viewModel);
            controller.navigate(R.id.action_repeatScheduleFragment_to_weekChooseDialog,bundle);
        });
    }

    private void getItemList() {
        for(CalendarItem i:calendarItems){
            if(i.type==1 && i.listId==item.listId){
                items.add(i);
            }
        }
    }

    private void setRepeatText(@NotNull Boolean[] repeat) {
        String s0=repeat[0]?"周日 ":"";
        String s1=repeat[1]?"周一 ":"";
        String s2=repeat[2]?"周二 ":"";
        String s3=repeat[3]?"周三 ":"";
        String s4=repeat[4]?"周四 ":"";
        String s5=repeat[5]?"周五 ":"";
        String s6=repeat[6]?"周六 ":"";
        String s=s0+s1+s2+s3+s4+s5+s6;
        if(s.length()==0){
            s="未选择";
        }else if(s.length()==21){
            s="每天";
        }else {
            s=s.substring(0,s.length()-1);
        }
        binding.textRepeatTime.setText(s);
    }

    private void showTimePicker(Date time, boolean isStart) {
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
                .setSubCalSize(16)
                .setContentTextSize(16)
                .setSubmitColor(ColorUtils.DoDodgerBlue)
                .setCancelColor(ColorUtils.DoDodgerBlue)
                .setType(new boolean[]{false,false,false,true,true,false}) // 年、月、日、时、分、秒
                .setOutSideCancelable(true) // 点击外围取消
                .setItemVisibleCount(3)
                .isCyclic(true) // 循环
                .setLabel("","","",":","","")
                .isCenterLabel(true)
                .setLineSpacingMultiplier(3.0F) // 间距
                .isDialog(true) // 以Dialog形式显示
                .build();
        ViewGroup container=view.getDialogContainerLayout();
        ViewGroup.LayoutParams params=container.getLayoutParams();
        params.width=600;
        container.setLayoutParams(params);
        view.show();
    }

    private void showDatePicker(Date time, boolean isStart) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(time);

        TimePickerView view=new TimePickerBuilder(getContext(), (date, v) -> {
            date=DateUtils.getDayDate(date);
            if(isStart){
                viewModel.getFirstStartTime().setValue(date);
            }else {
                viewModel.getLastEndTime().setValue(date);
            }
        })
                .setDate(calendar)
                .setSubCalSize(16)
                .setContentTextSize(16)
                .setSubmitColor(ColorUtils.DoDodgerBlue)
                .setCancelColor(ColorUtils.DoDodgerBlue)
                .setType(new boolean[]{true,true,true,false,false,false}) // 年、月、日、时、分、秒
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
        params.width=700;
        container.setLayoutParams(params);
        view.show();
    }

    private void updateItems() {
        for(CalendarItem i:items){
            updateItem(i);
        }
    }

    private void createItems() {
        long start=viewModel.getFirstStartTime().getValue().getTime();
        long end=viewModel.getLastEndTime().getValue().getTime();

        // 加入限制，一次最多添加31个日程
        for(long num=0,i=start;i<=end && num<=30;i+=DateUtils.A_DAY_MILLISECOND){
           if(viewModel.getRepeat().getValue()[new Date(i).getDay()]){
               createItem(i);
               num++;
           }
        }
    }

    private void doItem(long dayTime,CalendarItem i){
        Date day=new Date(dayTime);
        Date start=viewModel.getStartTime().getValue();
        Date end=viewModel.getEndTime().getValue();

        i.info=viewModel.getInfo().getValue();
        i.startTime=new Date(
                day.getYear(),
                day.getMonth(),
                day.getDate(),
                start.getHours(),
                start.getMinutes()
        ).getTime();
        i.endTime=new Date(
                day.getYear(),
                day.getMonth(),
                day.getDate(),
                end.getHours(),
                end.getMinutes()
        ).getTime();

        i.position=viewModel.getPosition().getValue();
        i.latitude= viewModel.latitude;
        i.longitude= viewModel.longitude;

        i.categoryId=getCategoryId(binding.spinnerCategory.getSelectedItemPosition());
        i.categoryName=binding.spinnerCategory.getSelectedItem().toString();
    }

    private void createItem(long dayTime) {
        item=new CalendarItem();
        item.uuid= UUID.randomUUID().toString().toUpperCase();
        item.type=1;
        item.listId=listId;
        item.username= UserUtils.USERNAME;
        doItem(dayTime,item);

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

    private void updateItem(CalendarItem i) {
        doItem(DateUtils.getDayDate(new Date(i.startTime)).getTime(),i);
        // 通知服务器修改日程
        boolean[] isSuccess=new boolean[1];
        requestUpdateItems(isSuccess,i);
        // 判断dirty值
        if(isSuccess[0]){
            i.dirty=0;
        }else {
            i.dirty=2;
        }
        // 更新数据库
        updateLocalItems(i);
    }

    private void updateLocalItems(CalendarItem i) {
        Thread thread=new Thread(() -> {
            if(i.id==0){
                i.id= dao.getIdByUUID(i.uuid);
            }
            dao.updateCalendarItem(i);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestUpdateItems(boolean[] isSuccess,CalendarItem i) {
        Thread thread=new Thread(() -> {
            RequestUtils.requestUpdateItems(apiService,isSuccess,i);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<View> getEditTextList(){
        List<View> viewList=new ArrayList<>();
        viewList.add(binding.editTextTittle);
        return viewList;
    }

    private void requestCategories() {
        Thread thread=new Thread(() -> {
            RequestUtils.requestCategories(apiService,categories);
            Log.d("lmx", "requestCategories: ");
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setUpdateViewModel() {
        viewModel.getInfo().setValue(item.info);

        viewModel.getPosition().setValue(item.position);
        viewModel.latitude=item.latitude;
        viewModel.longitude=item.longitude;

        viewModel.getStartTime().setValue(new Date(item.startTime));
        viewModel.getEndTime().setValue(new Date(item.endTime));

        viewModel.getFirstStartTime().setValue(new Date(items.get(0).startTime));
        viewModel.getLastEndTime().setValue(new Date(items.get(items.size()-1).endTime));

        Boolean[] repeat=new Boolean[7];
        for(int i=0;i<7;i++){
            repeat[i]=false;
        }
        for(CalendarItem i:items){
            repeat[new Date(i.startTime).getDay()]=true;
        }
        viewModel.getRepeat().setValue(repeat);

        // 暂时不支持修改重复、起始和截止时间
        binding.textRepeatTime.setEnabled(false);
        binding.textRepeatTime.setTextColor(ColorUtils.DarkGray);
        binding.layoutFirstStart.setVisibility(View.INVISIBLE);
        binding.layoutLastEnd.setVisibility(View.INVISIBLE);
    }

    private void setCreateViewModel() {
        viewModel.getStartTime().setValue(new Date(time));
        viewModel.getEndTime().setValue(new Date(time));
        viewModel.getFirstStartTime().setValue(DateUtils.getDayDate(new Date(time)));
        viewModel.getLastEndTime().setValue(DateUtils.getDayDate(new Date(time)));
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
}