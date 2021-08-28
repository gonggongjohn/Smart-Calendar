package team.time.smartcalendar.fragmentsthird;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentAccountBinding;
import team.time.smartcalendar.utils.SystemUtils;

public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false);

        ConstraintLayout.LayoutParams params= (ConstraintLayout.LayoutParams) binding.statusImage.getLayoutParams();
        params.height= SystemUtils.STATUS_BAR_HEIGHT;
        binding.statusImage.setLayoutParams(params);

        return binding.getRoot();
    }
}