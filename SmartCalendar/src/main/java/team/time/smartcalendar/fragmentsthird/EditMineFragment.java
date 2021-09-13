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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.R;
import team.time.smartcalendar.databinding.FragmentEditMineBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.RequestUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;
import team.time.smartcalendar.viewmodels.EditMineViewModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class EditMineFragment extends Fragment {
    private Activity parentActivity;
    private EditMineViewModel viewModel;
    private FragmentEditMineBinding binding;
    private NavController controller;

    @Inject
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Inject
    ApiService apiService;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = getActivity();

        editor=sp.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        viewModel = new ViewModelProvider(this).get(EditMineViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_mine,container,false);

        setViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"编辑",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.action.imageLeft.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            updateMineInfo();
            controller.popBackStack();
        });

        binding.imageMeq.setOnClickListener(v -> {
            SystemUtils.hideKeyBoard(getContext(),getEditTextList());
            controller.navigate(R.id.action_editMineFragment_to_meqFragment);
        });
    }

    private void setViewModel() {
        String nickname=sp.getString(UserUtils.USERNAME+"nickname",UserUtils.USERNAME);
        int meq=sp.getInt(UserUtils.USERNAME+"meq",43);
        viewModel.getNickname().setValue(nickname);
        viewModel.getMeq().setValue(meq);
    }

    private void updateMineInfo() {
        // 同步数据
        requestUpdate();
        // 本地更新
        editor.putString(UserUtils.USERNAME+"nickname",viewModel.getNickname().getValue());
        editor.commit();
    }

    private void requestUpdate() {
        JSONObject body=new JSONObject();
        try {
            body.put("nickname",viewModel.getNickname().getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestUtils.requestUpdateUser(apiService,body);
    }

    private List<View> getEditTextList(){
        List<View> viewList=new ArrayList<>();
        viewList.add(binding.editTextNickname);
        return viewList;
    }
}