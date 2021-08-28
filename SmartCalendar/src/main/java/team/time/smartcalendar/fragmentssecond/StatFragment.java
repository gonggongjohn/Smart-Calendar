package team.time.smartcalendar.fragmentssecond;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentStatBinding;
import team.time.smartcalendar.utils.SystemUtils;

public class StatFragment extends Fragment {
    private FragmentStatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stat,container,false);

        ConstraintLayout.LayoutParams params= (ConstraintLayout.LayoutParams) binding.statusImage.getLayoutParams();
        params.height= SystemUtils.STATUS_BAR_HEIGHT;
        binding.statusImage.setLayoutParams(params);

        return binding.getRoot();
    }
}