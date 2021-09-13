package team.time.smartcalendar.fragmentssecond;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.databinding.FragmentStatGraphBinding;
import team.time.smartcalendar.utils.DateUtils;
import team.time.smartcalendar.utils.SystemUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AndroidEntryPoint
public class StatGraphFragment extends Fragment {
    private Activity parentActivity;
    private FragmentStatGraphBinding binding;
    private NavController controller;
    private long today;

    private String[] weekday=new String[7];
    private long[] weekTime=new long[7];
    private double[] yStudy=new double[7];
    private double[] yWork=new double[7];
    private double[] ySport=new double[7];
    private double[] yTravel=new double[7];
    private double[] yHabit=new double[7];
    private double[] yOther=new double[7];

    private double[][] y=new double[6][];

    private List<CalendarItem> weekItems;

    @Named("all")
    @Inject
    public List<CalendarItem> calendarItems;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();
        weekItems=new ArrayList<>();
        today= DateUtils.getDayDate(new Date()).getTime();

        y[0]=yStudy; y[1]=yWork; y[2]=ySport;
        y[3]=yTravel;y[4]=yHabit;y[5]=yOther;

        Log.d("lmx", "y: "+ Arrays.deepToString(y));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stat_graph,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"日程分布",R.drawable.ic_baseline_arrow_back_ios_new_24,-1);

        binding.webBar.getSettings().setJavaScriptEnabled(true);
        binding.webBar.loadUrl("file:///android_asset/BarChart.html");
        setDate();
        setWeekItems();
        setY();
        binding.webBar.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.webBar.loadUrl("javascript:setOptionAndDraw("
                        +getJSString(weekday)+","
                        +Arrays.toString(y[0])+","
                        +Arrays.toString(y[1])+","
                        +Arrays.toString(y[2])+","
                        +Arrays.toString(y[3])+","
                        +Arrays.toString(y[4])+","
                        +Arrays.toString(y[5])+","
                        + ");");
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.action.imageLeft.setOnClickListener(v -> {
            controller.popBackStack();
        });
    }

    private void setDate() {
        for(int i=6,j=1;i>=0;i--,j++){
            weekTime[i]=today-j*DateUtils.A_DAY_MILLISECOND;
            weekday[i]=DateUtils.getOnlyDayTime(weekTime[i]);
        }
        Log.d("lmx", "setDate: "+ Arrays.toString(weekday));
        Log.d("lmx", "setDate: "+ Arrays.toString(weekTime));
    }

    private void setWeekItems() {
        long start=weekTime[0];
        long end=today;
        for(CalendarItem item:calendarItems){
            if(DateUtils.includeItem(item,start,end)){
                weekItems.add(item);
            }
        }
    }

    private void setY() {
        for(CalendarItem item:weekItems){
            int i=getIndexById(item.categoryId);
            for(int j=0; j<7;j++){
                if(DateUtils.includeItem(item,weekTime[j])){
                    long start=Math.max(item.startTime,weekTime[j]);
                    long end=Math.min(item.endTime,weekTime[j]+DateUtils.A_DAY_MILLISECOND);
                    y[i][j]= Double.parseDouble(String.format("%.1f",(end-start)/(double)(60*DateUtils.A_MIN_MILLISECOND)));
                }
            }
        }
    }

    private int getIndexById(int categoryId) {
        if(categoryId==1){
            return 5;
        }else {
            return categoryId-2;
        }
    }

    private String getJSString(String[] weekday) {
        StringBuilder builder=new StringBuilder();
        builder.append("[");
        for(String day:weekday){
            builder.append("'").append(day).append("',");
        }
        builder.append("]");
        return builder.toString();
    }
}