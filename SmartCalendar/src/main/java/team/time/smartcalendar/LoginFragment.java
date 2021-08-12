package team.time.smartcalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import team.time.smartcalendar.databinding.DialogLoginBinding;
import team.time.smartcalendar.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogin.setOnClickListener(v -> {
            AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
            DialogLoginBinding loginBinding=DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.dialog_login,
                    null,
                    false);
//            View dialogView=LayoutInflater.from(getContext())
//                    .inflate(R.layout.dialog_login,null);
            loginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vv) {
                    NavController controller= Navigation.findNavController(v);
                    controller.navigate(R.id.action_loginFragment_to_mainFragment);
                }
            });
            dialog.setView(loginBinding.getRoot());
            dialog.setCancelable(false);
            dialog.show();
        });
    }
}