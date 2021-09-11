package team.time.smartcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.databinding.FragmentRegisterBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.viewmodels.LoginViewModel;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    private Activity parentActivity;
    private NavController controller;
    private FragmentRegisterBinding binding;
    private LoginViewModel viewModel;

    @Inject
    ApiService apiService;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=getActivity();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_register,
                container,
                false);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"注册",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(getActivity(),R.id.loginNavHostFragment);

        binding.action.imageRight.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(parentActivity,getEditTextList());
            register();
        });

        binding.action.imageLeft.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(parentActivity,getEditTextList());
            controller.popBackStack();
        });
    }

    private List<View> getEditTextList() {
        List<View> viewList=new ArrayList<>();
        viewList.add(binding.editTextUserName);
        viewList.add(binding.editTextPassWord);
        viewList.add(binding.editTextPassWordAgain);
        viewList.add(binding.editTextPhoneNumber);
        return viewList;
    }

    private void register() {
        JSONObject body=new JSONObject();
        try {
            body.put("username",viewModel.getUserName().getValue());
            body.put("password",viewModel.getPassWord().getValue());
            body.put("phone",viewModel.getPhone().getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody=RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );

        apiService.register(requestBody).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(parentActivity, "注册成功", Toast.LENGTH_SHORT).show();
                            controller.popBackStack();
                        });
                    }else if(status==2){
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(parentActivity, "用户名已存在", Toast.LENGTH_SHORT).show();
                        });
                    }else {
                        parentActivity.runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
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
}
