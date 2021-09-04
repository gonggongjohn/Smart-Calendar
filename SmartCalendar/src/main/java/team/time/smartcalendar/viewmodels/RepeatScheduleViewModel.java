package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

public class RepeatScheduleViewModel extends ScheduleViewModel {
    private MutableLiveData<String> info;
    private MutableLiveData<String>position;
    public double latitude;
    public double longitude;
    private MutableLiveData<Date>startTime;
    private MutableLiveData<Date>endTime;
    private MutableLiveData<Date>firstStartTime;
    private MutableLiveData<Date>lastEndTime;
    private MutableLiveData<Boolean[]>repeat;

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

    public MutableLiveData<Boolean[]>getRepeat() {
        if(repeat==null){
            repeat=new MutableLiveData<>();
            Boolean[] booleans=new Boolean[7];
            for(int i=0;i<7;i++){
                booleans[i]=false;
            }
            repeat.setValue(booleans);
        }
        return repeat;
    }
}
