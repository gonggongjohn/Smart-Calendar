package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArrangeViewModel extends ViewModel {
    private MutableLiveData<String>info;

    public MutableLiveData<String> getInfo() {
        if(info==null){
            info=new MutableLiveData<>();
            info.setValue("");
        }
        return info;
    }
}
