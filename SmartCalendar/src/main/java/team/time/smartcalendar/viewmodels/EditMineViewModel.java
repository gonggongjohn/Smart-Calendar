package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import team.time.smartcalendar.utils.UserUtils;

public class EditMineViewModel extends ViewModel {
    private MutableLiveData<String>nickname;
    private MutableLiveData<Integer>meq;

    public MutableLiveData<String> getNickname() {
        if(nickname==null){
            nickname=new MutableLiveData<>();
            nickname.setValue(UserUtils.USERNAME);
        }
        return nickname;
    }

    public MutableLiveData<Integer> getMeq() {
        if(meq==null){
            meq=new MutableLiveData<>();
            meq.setValue(43);
        }
        return meq;
    }
}
