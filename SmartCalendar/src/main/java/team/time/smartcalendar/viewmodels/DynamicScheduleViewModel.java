package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

public class DynamicScheduleViewModel extends ScheduleViewModel{
    private MutableLiveData<Date>firstStartTime;
    private MutableLiveData<Date>lastEndTime;
    private MutableLiveData<String>hours;

    public MutableLiveData<Date> getFirstStartTime() {
        if(firstStartTime==null){
            firstStartTime=new MutableLiveData<>();
            firstStartTime.setValue(new Date());
        }
        return firstStartTime;
    }

    public MutableLiveData<Date> getLastEndTime() {
        if(lastEndTime==null){
            lastEndTime=new MutableLiveData<>();
            lastEndTime.setValue(new Date());
        }
        return lastEndTime;
    }

    public MutableLiveData<String>getHours(){
        if(hours==null){
            hours=new MutableLiveData<>();
            hours.setValue("");
        }
        return hours;
    }
}
