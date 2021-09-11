package team.time.smartcalendar.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<String>userName;
    private MutableLiveData<String>passWord;
    private MutableLiveData<String>phone;
    private MutableLiveData<String>nickName;

    public MutableLiveData<String> getUserName() {
        if(userName==null){
            userName=new MutableLiveData<>();
            userName.setValue("");
        }
        return userName;
    }

    public MutableLiveData<String> getPassWord() {
        if(passWord==null){
            passWord=new MutableLiveData<>();
            passWord.setValue("");
        }
        return passWord;
    }

    public MutableLiveData<String> getPhone() {
        if(phone==null){
            phone=new MutableLiveData<>();
            phone.setValue("");
        }
        return phone;
    }

    public MutableLiveData<String> getNickName() {
        if(nickName==null){
            nickName=new MutableLiveData<>();
            nickName.setValue("");
        }
        return nickName;
    }
}
