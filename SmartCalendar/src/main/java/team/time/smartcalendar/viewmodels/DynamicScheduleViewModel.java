package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

public class DynamicScheduleViewModel extends ScheduleViewModel{
    private MutableLiveData<String> info;
    private MutableLiveData<Date>startTime;
    private MutableLiveData<Date>endTime;
    private MutableLiveData<Date>firstStartTime;
    private MutableLiveData<Date>lastEndTime;
    private MutableLiveData<String>hours;

    public MutableLiveData<String> getInfo() {
        if(info==null){
            info=new MutableLiveData<>();
            info.setValue("");
        }
        return info;
    }

    public MutableLiveData<Date> getStartTime() {
        if(startTime==null){
            startTime=new MutableLiveData<>();
            startTime.setValue(new Date());
        }
        return startTime;
    }

    public MutableLiveData<Date> getEndTime() {
        if(endTime==null){
            endTime=new MutableLiveData<>();
            endTime.setValue(new Date());
        }
        return endTime;
    }

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
