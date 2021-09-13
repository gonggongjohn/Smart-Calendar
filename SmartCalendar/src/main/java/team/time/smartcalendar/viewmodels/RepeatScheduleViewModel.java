package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

public class RepeatScheduleViewModel extends ScheduleViewModel {
    private MutableLiveData<Date>firstStartTime;
    private MutableLiveData<Date>lastEndTime;
    private MutableLiveData<Boolean[]>repeat;

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
