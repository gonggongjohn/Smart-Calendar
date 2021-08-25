package team.time.smartcalendar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import team.time.smartcalendar.databinding.DialogRegisterBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.viewmodels.LoginViewModel;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class RegisterDialog extends DialogFragment {
    private NavController controller;
    private DialogRegisterBinding binding;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LoginViewModel viewModel;
    private Activity parentActivity;

    @Inject
    ApiService apiService;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        parentActivity=getActivity();

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.dialog_register,
                null,
                false);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        controller = Navigation.findNavController(getActivity(),R.id.loginNavHostFragment);
        binding.btnLogin.setOnClickListener(v -> {
            List<View> viewList=new ArrayList<>();
            viewList.add(binding.editTextUserName);
            viewList.add(binding.editTextPassWord);
            viewList.add(binding.editTextPassWordAgain);
            viewList.add(binding.editTextPhoneNumber);
            SystemUtils.hideKeyBoard(parentActivity,viewList);
            register();
        });
        binding.btnCancel.setOnClickListener(v -> {
            controller.popBackStack();
        });

        builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
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
