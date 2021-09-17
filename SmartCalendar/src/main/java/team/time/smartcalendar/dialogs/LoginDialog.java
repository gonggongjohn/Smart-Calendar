package team.time.smartcalendar.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.DialogLoginBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.RequestUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;
import team.time.smartcalendar.viewmodels.LoginViewModel;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class LoginDialog extends DialogFragment {
    private NavController controller;
    private DialogLoginBinding binding;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LoginViewModel viewModel;
    private Activity parentActivity;
    private String USERNAME;
    private String PASSWORD;
    private SharedPreferences.Editor editor;

    @Inject
    ApiService apiService;

    @Inject
    SharedPreferences sp;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        parentActivity=getActivity();

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.dialog_login,
                null,
                false);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        controller = Navigation.findNavController(getActivity(),R.id.loginNavHostFragment);
        binding.btnLogin.setOnClickListener(v -> {
            // 隐藏键盘
            List<View>viewList=new ArrayList<>();
            viewList.add(binding.editTextUserName);
            viewList.add(binding.editTextPassWord);
            SystemUtils.hideKeyBoard(parentActivity,viewList);
            // 检测合法性
            if(viewModel.getUserName().getValue().equals("")){
                Toast.makeText(parentActivity, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }else if(viewModel.getPassWord().getValue().equals("")){
                Toast.makeText(parentActivity, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            // 储存USERNAME
            UserUtils.USERNAME=viewModel.getUserName().getValue();
            // 登录
            login();
        });
        binding.btnCancel.setOnClickListener(v -> {
            controller.popBackStack();
        });

        builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());
//        builder.setCancelable(false);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @SuppressLint("CommitPrefEdits")
    private void login() {
        editor = sp.edit();

        USERNAME=viewModel.getUserName().getValue();
        PASSWORD=viewModel.getPassWord().getValue();

        JSONObject body=new JSONObject();
        try {
            body.put("username",USERNAME);
            body.put("password",PASSWORD);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody=RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );

        apiService.login(requestBody).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        editor.putString("username",USERNAME);
                        editor.putString("password",PASSWORD);
                        editor.commit();

                        // 更新用户nickname
                        updateUser();

                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(parentActivity, "登录成功", Toast.LENGTH_SHORT).show();
                            controller.navigate(R.id.action_loginDialog_to_mainFragment);
                        });
                    }else if(status==2){
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(parentActivity, "密码错误", Toast.LENGTH_SHORT).show();
                        });
                    }else if(status==3){
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "用户名不存在", Toast.LENGTH_SHORT).show();
                        });
                    }else{
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException | IOException e) {
                    parentActivity.runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                parentActivity.runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateUser() {
        String nickname=sp.getString(UserUtils.USERNAME+"nickname",UserUtils.USERNAME);
        JSONObject body=new JSONObject();
        try {
            body.put("nickname",nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestUtils.requestUpdateUser(apiService,body);
    }
}
