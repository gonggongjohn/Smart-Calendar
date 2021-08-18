package team.time.smartcalendar.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.MainApplication;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.DialogLoginBinding;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.viewmodels.LoginViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginDialog extends DialogFragment {
    private NavController controller;
    private DialogLoginBinding binding;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LoginViewModel viewModel;
    private Activity parentActivity;
    private SharedPreferences sp;
    private String USERNAME;
    private String PASSWORD;
    private SharedPreferences.Editor editor;
    private OkHttpClient client;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        parentActivity=getActivity();
        client=((MainApplication)getActivity().getApplication()).getClient();

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.dialog_login,
                null,
                false);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())
        ).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        controller = Navigation.findNavController(getActivity(),R.id.loginNavHostFragment);
        binding.btnLogin.setOnClickListener(v -> {
            List<View>viewList=new ArrayList<>();
            viewList.add(binding.editTextUserName);
            viewList.add(binding.editTextPassWord);
            SystemUtils.hideKeyBoard(parentActivity,viewList);
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
        sp = getActivity().getSharedPreferences(
                getString(R.string.USER_INFO),
                Context.MODE_PRIVATE);
        editor = sp.edit();

        USERNAME=viewModel.getUserName().getValue();
        PASSWORD=viewModel.getPassWord().getValue();

        String PATH="/user/login";

        Map<String,String> map=new HashMap<>();
        map.put("username",USERNAME);
        map.put("password",PASSWORD);

        String body=new JSONObject(map).toString();
        RequestBody requestBody=RequestBody.create(
                body,
                MediaType.parse("application/json;charset=utf-8")
        );

        Request request=new Request.Builder()
                .url(getString(R.string.URL)+PATH)
                .addHeader("contentType","application/json;charset=UTF-8")
                .post(requestBody)
                .build();

        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                parentActivity.runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
                });
            }

            @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        editor.putString("username",USERNAME);
                        editor.putString("password",PASSWORD);
                        editor.commit();

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
