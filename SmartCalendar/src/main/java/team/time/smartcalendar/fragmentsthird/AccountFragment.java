package team.time.smartcalendar.fragmentsthird;

import android.app.Activity;
import android.content.SharedPreferences;
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
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CommonRecyclerViewAdapter;
import team.time.smartcalendar.databinding.FragmentAccountBinding;
import team.time.smartcalendar.databinding.ItemCommonBinding;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;
    private Activity parentActivity;
    private NavController controller;
    private RecyclerView.LayoutManager manager;
    private CommonRecyclerViewAdapter adapter;

    List<String> strings=new ArrayList<>();
    List<Integer> icons=new ArrayList<>();
    List<String> values=new ArrayList<>();

    @Inject
    SharedPreferences sp;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();


        strings.add("昵称:");strings.add("MEQ-SA:");

        icons.add(R.drawable.ic_baseline_face_24);icons.add(R.drawable.ic_baseline_credit_score_24);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String nickname=sp.getString(UserUtils.USERNAME+"nickname",UserUtils.USERNAME);
        int meqScore=sp.getInt(UserUtils.USERNAME+"meqScore",43);
        values.clear();values.add(nickname);values.add(String.valueOf(meqScore));

        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"我的账户",R.drawable.ic_baseline_arrow_back_ios_new_24,R.drawable.ic_baseline_mode_edit_24);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        manager = new LinearLayoutManager(parentActivity);
        adapter = new CommonRecyclerViewAdapter(parentActivity,strings,icons,values) {
            @Override
            protected void setItemView(ItemCommonBinding binding, int position) {
                super.setItemView(binding, position);
                binding.imageArrow.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onItemClick(List<String> strings, List<Integer> icons, int position) {}
        };
        binding.viewAccount.setLayoutManager(manager);
        binding.viewAccount.setAdapter(adapter);

        binding.action.imageLeft.setOnClickListener(v -> {
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            controller.navigate(R.id.action_accountFragment_to_editMineFragment);
        });
    }
}