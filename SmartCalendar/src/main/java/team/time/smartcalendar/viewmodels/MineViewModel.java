package team.time.smartcalendar.viewmodels;

import android.app.Application;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.utils.UserUtils;

public class MineViewModel extends AndroidViewModel {
    MutableLiveData<String>nickName;
    MutableLiveData<String>userName;
    MutableLiveData<String>head;
    MutableLiveData<String>background;

    Application application;

    public MineViewModel(@NonNull @NotNull Application application) {
        super(application);
        this.application=application;
    }

    public MutableLiveData<String> getNickName() {
        if(nickName==null){
            nickName=new MutableLiveData<>();
            nickName.setValue(UserUtils.USERNAME);
        }
        return nickName;
    }

    public MutableLiveData<String> getUserName() {
        if(userName==null){
            userName=new MutableLiveData<>();
            userName.setValue(UserUtils.USERNAME);
        }
        return userName;
    }

    public MutableLiveData<String> getHead() {
        if(head==null){
            head=new MutableLiveData<>();
            head.setValue(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+UserUtils.USERNAME+"head.jpg");
        }
        return head;
    }

    public MutableLiveData<String> getBackground() {
        if(background==null){
            background=new MutableLiveData<>();
            background.setValue(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+UserUtils.USERNAME+"background.jpg");
        }
        return background;
    }
}
