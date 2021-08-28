package team.time.smartcalendar.fragmentssecond;

import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentMainSecondBinding;

public class MainSecondFragment extends Fragment {
    private FragmentMainSecondBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_second,container,false);

        return binding.getRoot();
    }
}