package team.time.smartcalendar.fragmentssecond;

import android.app.Activity;
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
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentStatBinding;
import team.time.smartcalendar.utils.SystemUtils;

public class StatFragment extends Fragment {
    private Activity parentActivity;
    private FragmentStatBinding binding;
    private NavController controller;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stat,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"统计",-1,-1);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.cardGo.setOnClickListener(v -> {
            controller.navigate(R.id.action_statFragment_to_heatMapFragment);
        });

        binding.cardStat.setOnClickListener(v -> {
            controller.navigate(R.id.action_statFragment_to_statGraphFragment);
        });
    }
}