package team.time.smartcalendar.fragmentsthird;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentMainThirdBinding;


public class MainThirdFragment extends Fragment {
    private FragmentMainThirdBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_third,container,false);

        return binding.getRoot();
    }
}