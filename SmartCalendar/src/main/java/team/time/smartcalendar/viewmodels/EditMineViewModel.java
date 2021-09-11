package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import team.time.smartcalendar.utils.UserUtils;

public class EditMineViewModel extends ViewModel {
    private MutableLiveData<String>nickname;

    public MutableLiveData<String> getNickname() {
        if(nickname==null){
            nickname=new MutableLiveData<>();
            nickname.setValue(UserUtils.USERNAME);
        }
        return nickname;
    }
}
