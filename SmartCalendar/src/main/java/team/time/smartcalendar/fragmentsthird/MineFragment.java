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
import team.time.smartcalendar.adapters.CommonRecyclerViewAdapter;
import team.time.smartcalendar.databinding.FragmentMineBinding;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment {
    FragmentMineBinding binding;
    private Activity parentActivity;

    private List<String>strings=new ArrayList<>();
    private List<Integer>icons=new ArrayList<>();
    private RecyclerView.LayoutManager manager;
    private CommonRecyclerViewAdapter adapter;
    public NavController controller;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();

        strings.add("我的账户");strings.add("设置");
        strings.add("关于");

        icons.add(R.drawable.ic_baseline_manage_accounts_24);icons.add(R.drawable.ic_baseline_settings_24);
        icons.add(R.drawable.ic_baseline_link_24);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine,container,false);

        ViewGroup.LayoutParams params=binding.imageBack.getLayoutParams();
        params.height= (int) (6/9.0 * binding.imageBack.getContext().getResources().getDisplayMetrics().widthPixels);
        binding.imageBack.setLayoutParams(params);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        manager = new LinearLayoutManager(parentActivity);
        binding.viewMine.setLayoutManager(manager);
        adapter = new CommonRecyclerViewAdapter(parentActivity, strings, icons) {
            @Override
            protected void onItemClick(List<String> strings, List<Integer> icons, int position) {
                switch (position){
                    case 0:
                        controller.navigate(R.id.action_mineFragment_to_accountFragment);
                        break;
                    case 1:
                        controller.navigate(R.id.action_mineFragment_to_settingsFragment);
                        break;
                    case 2:
                        controller.navigate(R.id.action_mineFragment_to_aboutFragment);
                        break;
                }
            }
        };
        binding.viewMine.setAdapter(adapter);

        binding.imageBack.setOnLongClickListener(v -> {
            controller.navigate(R.id.action_mineFragment_to_chooseDialog);
            return true;
        });

        binding.imageHead.setOnLongClickListener(v -> {
            controller.navigate(R.id.action_mineFragment_to_chooseDialog);
            return true;
        });
    }
}