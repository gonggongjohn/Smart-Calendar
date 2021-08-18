package team.time.smartcalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.databinding.FragmentLoginBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private NavController controller;
    private Activity parentActivity;
    private SharedPreferences sp;
    private String USERNAME;
    private String PASSWORD;
    private OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity=getActivity();
        client=((MainApplication)getActivity().getApplication()).getClient();

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        sp= getActivity().getSharedPreferences(
                getString(R.string.USER_INFO),
                Context.MODE_PRIVATE
        );

        USERNAME = sp.getString("username","");
        PASSWORD = sp.getString("password","");

        // 自动登录
        if(!USERNAME.equals("") && !PASSWORD.equals("")){
            binding.btnLogin.setVisibility(View.GONE);
            binding.btnRegister.setVisibility(View.GONE);
            login();
        }

        binding.btnLogin.setOnClickListener(v -> {
            controller.navigate(R.id.action_loginFragment_to_loginDialog);
        });

        binding.btnRegister.setOnClickListener(v -> {
            controller.navigate(R.id.action_loginFragment_to_registerDialog);
        });
    }

    private void login() {
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
                    binding.btnLogin.setVisibility(View.VISIBLE);
                    binding.btnRegister.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
                });
            }

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        parentActivity.runOnUiThread(() -> {
                            controller.navigate(R.id.action_loginFragment_to_mainFragment);
                        });
                    }else if(status==2){
                        parentActivity.runOnUiThread(() -> {
                            binding.btnLogin.setVisibility(View.VISIBLE);
                            binding.btnRegister.setVisibility(View.VISIBLE);
                            Toast.makeText(parentActivity, "密码错误", Toast.LENGTH_SHORT).show();
                        });
                    }else if(status==3){
                        parentActivity.runOnUiThread(() -> {
                            binding.btnLogin.setVisibility(View.VISIBLE);
                            binding.btnRegister.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "用户名不存在", Toast.LENGTH_SHORT).show();
                        });
                    }else{
                        parentActivity.runOnUiThread(() -> {
                            binding.btnLogin.setVisibility(View.VISIBLE);
                            binding.btnRegister.setVisibility(View.VISIBLE);
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