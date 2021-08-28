package team.time.smartcalendar.fragmentsthird;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.MineRecyclerViewAdapter;
import team.time.smartcalendar.databinding.FragmentMineBinding;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment {
    FragmentMineBinding binding;
    private Activity parentActivity;

    private List<String>strings=new ArrayList<>();
    private List<Integer>icons=new ArrayList<>();
    private RecyclerView.LayoutManager manager;
    private MineRecyclerViewAdapter adapter;
    public NavController controller;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();

        strings.add("我的账户");strings.add("设置");strings.add("关于");

        icons.add(R.drawable.ic_baseline_manage_accounts_24);icons.add(R.drawable.ic_baseline_settings_24);
        icons.add(R.drawable.ic_baseline_link_24);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = new LinearLayoutManager(parentActivity);
        binding.viewMine.setLayoutManager(manager);
        adapter = new MineRecyclerViewAdapter(this,strings,icons);
        binding.viewMine.setAdapter(adapter);

        controller = Navigation.findNavController(view);
    }
}