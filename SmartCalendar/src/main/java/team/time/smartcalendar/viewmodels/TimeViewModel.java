package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import team.time.smartcalendar.utils.DateUtils;

import java.util.Date;

public class TimeViewModel extends ViewModel {
    MutableLiveData<Date>start;
    MutableLiveData<Date>end;

    public MutableLiveData<Date> getStart() {
        if(start==null){
            start=new MutableLiveData<>();
            start.setValue(DateUtils.getMinDate(new Date()));
        }
        return start;
    }

    public MutableLiveData<Date> getEnd() {
        if(end==null){
            end=new MutableLiveData<>();
            end.setValue(DateUtils.getMinDate(new Date()));
        }
        return end;
    }
}
