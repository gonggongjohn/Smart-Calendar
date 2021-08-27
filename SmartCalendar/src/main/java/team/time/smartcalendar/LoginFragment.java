package team.time.smartcalendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.time.smartcalendar.databinding.FragmentLoginBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.UserUtils;

import javax.inject.Inject;
import java.io.IOException;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private NavController controller;
    private Activity parentActivity;
    private String USERNAME;
    private String PASSWORD;

    @Inject
    ApiService apiService;

    @Inject
    SharedPreferences sp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity=getActivity();

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        USERNAME = sp.getString("username","");
        PASSWORD = sp.getString("password","");

        // 储存USERNAME
        UserUtils.USERNAME=USERNAME;

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

    private void showLoginAndRegister(){
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.btnRegister.setVisibility(View.VISIBLE);
    }

    private void login() {
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

        apiService.login(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    Log.d("lmx", "onResponse: "+result);
                    int status=result.getInt("status");
                    if(status==1){
                        parentActivity.runOnUiThread(() -> {
                            controller.navigate(R.id.action_loginFragment_to_mainFragment);
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
                    // 显示登录与注册按钮
                    if(status!=1){
                        showLoginAndRegister();
                    }
                } catch (JSONException | IOException e) {
                    parentActivity.runOnUiThread(() -> {
                        Log.d("lmx", "onResponse: "+e);
                        showLoginAndRegister();
                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("lmx", "onFailure: "+t);
                parentActivity.runOnUiThread(() -> {
                    showLoginAndRegister();
                    Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}