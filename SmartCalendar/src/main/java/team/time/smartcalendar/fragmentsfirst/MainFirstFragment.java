package team.time.smartcalendar.fragmentsfirst;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentMainFirstBinding;

public class MainFirstFragment extends Fragment {
    private FragmentMainFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_first,container,false);
        return binding.getRoot();
    }
}