package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.Date;

public class ScheduleViewModel extends ViewModel implements Serializable {
    private MutableLiveData<String>info;
    private MutableLiveData<String>position;
    public double latitude;
    public double longitude;
    private MutableLiveData<String>details;
    private MutableLiveData<Date>startTime;
    private MutableLiveData<Date>endTime;

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

    public MutableLiveData<String> getDetails() {
        if(details==null){
            details=new MutableLiveData<>();
            details.setValue("");
        }
        return details;
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
