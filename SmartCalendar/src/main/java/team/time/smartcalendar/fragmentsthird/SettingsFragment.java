package team.time.smartcalendar.fragmentsthird;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CommonRecyclerViewAdapter;
import team.time.smartcalendar.databinding.FragmentSettingsBinding;
import team.time.smartcalendar.databinding.ItemCommonBinding;
import team.time.smartcalendar.utils.SystemUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;
    Activity parentActivity;
    private RecyclerView.LayoutManager manager;
    private CommonRecyclerViewAdapter adapter;

    List<String> strings=new ArrayList<>();
    List<Integer> icons=new ArrayList<>();

    @Inject
    SharedPreferences sp;
    private NavController controller;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();

        strings.add("退出登录");

        icons.add(R.drawable.ic_baseline_person_remove_24);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"设置",R.drawable.ic_baseline_arrow_back_ios_new_24,-1);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        manager = new LinearLayoutManager(parentActivity);
        adapter = new CommonRecyclerViewAdapter(parentActivity,strings,icons) {
            boolean[] loggOffFlag=new boolean[1];

            @Override
            protected void setItemView(ItemCommonBinding binding, int position) {
                super.setItemView(binding, position);
                if(position==0){
                    binding.imageArrow.setVisibility(View.INVISIBLE);
                }else {
                    binding.imageArrow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onItemClick(List<String> strings, List<Integer> icons, int position) {
                switch (position){
                    case 0:
                        logOff(loggOffFlag);
                        break;
                }
            }
        };
        binding.viewSettings.setLayoutManager(manager);
        binding.viewSettings.setAdapter(adapter);

        binding.action.imageLeft.setOnClickListener(v -> {
            controller.popBackStack();
        });
    }

    private void logOff(boolean[] flag) {
        if(flag[0]){
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("username","");
            editor.putString("password","");
            editor.commit();
            System.exit(0);
//            controller.navigate(R.id.action_settingsFragment_to_loginFragment2);
        }else {
            Toast.makeText(parentActivity, "再点一次退出登录", Toast.LENGTH_SHORT).show();
        }
        flag[0]=!flag[0];
    }
}