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
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.MainApplication;
import team.time.smartcalendar.R;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.databinding.DialogRegisterBinding;
import team.time.smartcalendar.viewmodels.LoginViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterDialog extends DialogFragment {
    private NavController controller;
    private DialogRegisterBinding binding;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LoginViewModel viewModel;
    private Activity parentActivity;
    private OkHttpClient client;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        parentActivity=getActivity();
        client=((MainApplication)getActivity().getApplication()).getClient();

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.dialog_register,
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
        String PATH="/user/register";

        Map<String,String> map=new HashMap<>();
        map.put("username",viewModel.getUserName().getValue());
        map.put("password",viewModel.getPassWord().getValue());
        map.put("phone",viewModel.getPhone().getValue());

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

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
