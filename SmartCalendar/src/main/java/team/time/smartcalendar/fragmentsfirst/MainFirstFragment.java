package team.time.smartcalendar.fragmentsfirst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentMainFirstBinding;
import team.time.smartcalendar.service.MyLocationService;

public class MainFirstFragment extends Fragment {
    private FragmentMainFirstBinding binding;
    private Activity parentActivity;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();

        Intent intent=new Intent(parentActivity, MyLocationService.class);
        parentActivity.startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_first,container,false);
        return binding.getRoot();
    }
}