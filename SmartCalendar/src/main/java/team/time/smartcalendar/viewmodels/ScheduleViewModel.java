package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.Date;

public class ScheduleViewModel extends ViewModel implements Serializable {
    protected MutableLiveData<String>info;
    protected MutableLiveData<String>position;
    public double latitude;
    public double longitude;
    protected MutableLiveData<Date>startTime;
    protected MutableLiveData<Date>endTime;

    public MutableLiveData<String> getInfo() {
        if(info==null){
            info=new MutableLiveData<>();
            info.setValue("");
        }
        return info;
    }

    public MutableLiveData<String> getPosition() {
        if(position==null){
            position=new MutableLiveData<>();
            position.setValue("");
        }
        return position;
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
}
