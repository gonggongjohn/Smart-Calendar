package team.time.smartcalendar.fragmentsfirst;

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
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentDynamicScheduleBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.*;
import team.time.smartcalendar.viewmodels.DynamicScheduleViewModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@AndroidEntryPoint
public class DynamicScheduleFragment extends Fragment {
    private FragmentDynamicScheduleBinding binding;
    private NavController controller;
    private Activity parentActivity;
    private DynamicScheduleViewModel viewModel;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 重写回退键
        SystemUtils.setBack(this);

        viewModel = new ViewModelProvider(this).get(DynamicScheduleViewModel.class);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dynamic_schedule,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"动态日程",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

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
            if(bundle!=null) {
                item = (CalendarItem) bundle.getSerializable("item");
                time = bundle.getLong("time");

                // 修改日程
                if (item != null) {
//                    getItemList();
//                    sortItemList();
//                    setUpdateViewModel();
//                    setSpinnerSelection();
                }

                // 添加日程
                if (time != 0) {
                    // 设置日程链ID
                    listId = new Date().getTime();
                    setCreateViewModel();
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
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.action.imageLeft.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(), getEditTextList());

            if((viewModel.getStartTime().getValue().getTime())>(viewModel.getEndTime().getValue().getTime())){
                Toast.makeText(parentActivity, "开始时间晚于结束时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if((viewModel.getFirstStartTime().getValue().getTime())>(viewModel.getLastEndTime().getValue().getTime())){
                Toast.makeText(parentActivity, "起始时间晚于截止时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if(viewModel.getHours().getValue().equals("")){
                Toast.makeText(parentActivity, "请填写预计花费的时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if(item==null){
                createItems();
            }else {
                updateItems();
            }

            controller.popBackStack();
        });

        binding.textStartTime.setOnClickListener(v -> {
            showTimePicker(viewModel.getStartTime().getValue(),true);
        });

        binding.textEndTime.setOnClickListener(v -> {
            showTimePicker(viewModel.getEndTime().getValue(),false);
        });

        binding.textBegin.setOnClickListener(v -> {
            showDatePicker(viewModel.getFirstStartTime().getValue(),true);
        });

        binding.textDDL.setOnClickListener(v -> {
            showDatePicker(viewModel.getLastEndTime().getValue(),false);
        });
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
                .setItemVisibleCount(5)
                .isCyclic(true) // 循环
                .setLabel("","","",":","","")
                .isCenterLabel(true)
                .setLineSpacingMultiplier(3.0F) // 间距
                .isDialog(true) // 以Dialog形式显示
                .build();
        ViewGroup container=view.getDialogContainerLayout();
        ViewGroup.LayoutParams params=container.getLayoutParams();
        params.width=500;
        container.setLayoutParams(params);
        view.show();
    }

    private void showDatePicker(Date time,boolean isStart) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(time);

        TimePickerView view=new TimePickerBuilder(getContext(), (date, v) -> {
            date=DateUtils.getMinDate(date);
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
        viewList.add(binding.editTextPredict);
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

    private void setCreateViewModel() {
        viewModel.getStartTime();
        viewModel.getEndTime();
        viewModel.getFirstStartTime().setValue(new Date(time));
        viewModel.getLastEndTime().setValue(new Date(time+4 * DateUtils.A_DAY_MILLISECOND));
    }



    private void updateItems() {

    }

    private void createItems() {
        // 请求
        boolean[] isSuccess=new boolean[1];
        requestArrangeItems(isSuccess);
        // 展示返回的日程列表，用户可以进行修改
        showArrangeAdvice();
        // 添加日程
        if(isSuccess[0]){
            for(CalendarItem i:items){
                i.type=3;
                i.listId=listId;
                i.uuid= UUID.randomUUID().toString().toUpperCase();
                createItem(i);
            }
        }
    }

    private void createItem(CalendarItem i) {
        // 通知服务器添加日程
        boolean[] isSuccess=new boolean[1];
        requestAddItems(i,isSuccess);
        // 判断dirty值
        if(isSuccess[0]){
            item.dirty=0;
        }else {
            item.dirty=1;
        }
        // 本地创建日程
        calendarItems.add(i);
        // 添加到数据库
        addLocalItems(i);
    }

    private void requestAddItems(CalendarItem i, boolean[] isSuccess) {
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

    private void addLocalItems(CalendarItem i) {
        Thread thread=new Thread(() -> {
            dao.insertCalendarItem(i);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestArrangeItems(boolean[] isSuccess) {
        Thread thread=new Thread(()->{
            RequestBody requestBody=RequestUtils.createArrangeRequestBody(
                    viewModel.getInfo().getValue(),
                    getCategoryId(binding.spinnerCategory.getSelectedItemPosition()),
                    Integer.parseInt(viewModel.getHours().getValue()),
                    viewModel.getFirstStartTime().getValue().getTime()/1000.0,
                    viewModel.getLastEndTime().getValue().getTime()/1000.0
            );
            RequestUtils.requestArrangeItems(apiService,isSuccess,items,requestBody,parentActivity);
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showArrangeAdvice() {

    }

    // 计算服务器提供的category的ID
    private int getCategoryId(int index){
        if(categories.size()==1){
            return 1;
        }
        return (index+2) % categories.size();
    }
}