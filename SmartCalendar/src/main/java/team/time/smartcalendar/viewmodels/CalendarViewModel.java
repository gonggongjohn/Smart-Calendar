package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel {
    private MutableLiveData<String>monthDay;
    private MutableLiveData<Integer>year;
    private MutableLiveData<String>lunar;
    private MutableLiveData<Integer>currentDay;

    public MutableLiveData<String> getMonthDay() {
        if(monthDay==null){
            monthDay=new MutableLiveData<>();
            monthDay.setValue("1月1日");
        }
        return monthDay;
    }

    public MutableLiveData<Integer> getYear() {
        if(year==null){
            year=new MutableLiveData<>();
            year.setValue(2000);
        }
        return year;
    }

    public MutableLiveData<String> getLunar() {
        if(lunar==null){
            lunar=new MutableLiveData<>();
            lunar.setValue("初一");
        }
        return lunar;
    }

    public MutableLiveData<Integer> getCurrentDay() {
        if(currentDay==null){
            currentDay=new MutableLiveData<>();
            currentDay.setValue(1);
        }
        return currentDay;
    }
}
