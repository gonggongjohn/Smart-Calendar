package team.time.smartcalendar.fragmentsfirst;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CalendarRecyclerViewAdapter;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentCalendarBinding;
import team.time.smartcalendar.viewmodels.CalendarViewModel;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;
    private boolean isFirst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
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

        if(isFirst){
           isFirst=false;

            for (int i = 1; i <= 10; i++) {
                CalendarItem item1=new CalendarItem();
                item1.infoAll="吃饭";item1.allDay="全天";
                viewModel.getItems().getValue().add(item1);
                CalendarItem item2=new CalendarItem();
                item2.startTime="00:00";item2.endTime="00:30";item2.infoAll="睡觉";
                viewModel.getItems().getValue().add(item2);
                CalendarItem item3=new CalendarItem();
                item3.startTime="01:00";item3.startTime="02:00";item3.info="玩游戏";item3.position="游戏厅";
                viewModel.getItems().getValue().add(item3);
            }

            viewModel.getMonthDay().setValue(binding.calendarView.getCurMonth() + "月" + binding.calendarView.getCurDay() + "日");
            viewModel.getYear().setValue(binding.calendarView.getCurYear());
            viewModel.getLunar().setValue(binding.calendarView.getSelectedCalendar().getLunar());
            viewModel.getCurrentDay().setValue(binding.calendarView.getCurDay());
        }

        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        binding.calendarRecyclerView.setLayoutManager(manager);
        CalendarRecyclerViewAdapter adapter=new CalendarRecyclerViewAdapter(viewModel.getItems().getValue());
        binding.calendarRecyclerView.setAdapter(adapter);

        binding.calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                viewModel.getMonthDay().setValue(calendar.getMonth() + "月" + calendar.getDay() + "日");
                viewModel.getYear().setValue(calendar.getYear());
                viewModel.getLunar().setValue(calendar.getLunar());
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
            NavController controller= Navigation.findNavController(v);
            controller.navigate(R.id.action_calendarFragment_to_scheduleFragment);
        });
    }
}