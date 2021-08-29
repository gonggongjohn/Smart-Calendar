package team.time.smartcalendar.fragmentssecond;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentStatBinding;
import team.time.smartcalendar.utils.SystemUtils;

public class StatFragment extends Fragment {
    private FragmentStatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stat,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"统计",-1,-1);

        return binding.getRoot();
    }
}