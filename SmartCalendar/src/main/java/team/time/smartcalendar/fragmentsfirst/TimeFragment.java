package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
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
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentTimeBinding;
import team.time.smartcalendar.utils.ColorUtils;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.viewmodels.TimeViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeFragment extends Fragment {


    private Activity parentActivity;
    private FragmentTimeBinding binding;
    private TimeViewModel viewModel;
    private CalendarItem item;
    private List<CalendarItem>items;
    private boolean isCreate;
    private NavController controller;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();

        viewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        Bundle bundle=getArguments();
        if(bundle!=null){
            item = (CalendarItem) bundle.getSerializable("item");
            items= (List<CalendarItem>) bundle.getSerializable("items");
            isCreate=bundle.getBoolean("isCreate");
            setViewModel();
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parentActivity),
                R.layout.fragment_time,
                null,
                false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"日程时间",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        binding.setViewModel(viewModel);

        viewModel.getStart().observe(this, date -> {
            binding.textDate.setText(DateUtils.getDayTime(date));
            binding.textStart.setText(DateUtils.getClockTime(date));
        });

        viewModel.getEnd().observe(this,date -> {
            binding.textDate.setText(DateUtils.getDayTime(date));
            binding.textEnd.setText(DateUtils.getClockTime(date));
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.textDate.setOnClickListener(v -> {
            showDatePicker(viewModel.getStart().getValue());
        });

        binding.textStart.setOnClickListener(v -> {
            showTimePicker(viewModel.getStart().getValue(),true);
        });

        binding.textEnd.setOnClickListener(v -> {
            showTimePicker(viewModel.getEnd().getValue(),false);
        });

        binding.action.imageLeft.setOnClickListener(v -> {
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            if(viewModel.getStart().getValue().getTime()>viewModel.getEnd().getValue().getTime()){
                Toast.makeText(parentActivity, "开始时间晚于结束时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isCreate){
                create();
            }else {
                update();
            }
            DateUtils.sortItemList(items);
            controller.popBackStack();
        });
    }

    private void doItem() {
        item.startTime=viewModel.getStart().getValue().getTime();
        item.endTime=viewModel.getEnd().getValue().getTime();
    }

    private void create() {
        doItem();
        items.add(item);
    }

    private void update() {
        doItem();
    }

    private void setViewModel() {
        viewModel.getStart().setValue(new Date(item.startTime));
        viewModel.getEnd().setValue(new Date(item.endTime));
    }

    private void showTimePicker(Date time, boolean isStart) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(time);

        TimePickerView view=new TimePickerBuilder(getContext(), (date, v) -> {
            date=new Date(
                    time.getYear(),
                    time.getMonth(),
                    time.getDate(),
                    date.getHours(),
                    date.getMinutes()
            );
            if(isStart){
                viewModel.getStart().setValue(date);
            }else {
                viewModel.getEnd().setValue(date);
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

    private void showDatePicker(Date time) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(time);

        TimePickerView view=new TimePickerBuilder(getContext(), (date, v) -> {
            Date start=viewModel.getStart().getValue();
            Date end=viewModel.getEnd().getValue();
            start=new Date(
                    date.getYear(),
                    date.getMonth(),
                    date.getDate(),
                    start.getHours(),
                    start.getMinutes()
            );
            end=new Date(
                    date.getYear(),
                    date.getMonth(),
                    date.getDate(),
                    end.getHours(),
                    end.getMinutes()
            );
            viewModel.getStart().setValue(start);
            viewModel.getEnd().setValue(end);
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
}