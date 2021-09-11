package team.time.smartcalendar.fragmentsthird;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.gzuliyujiang.imagepicker.ImagePicker;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.CommonRecyclerViewAdapter;
import team.time.smartcalendar.databinding.FragmentMineBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.GlideUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;
import team.time.smartcalendar.viewmodels.MineViewModel;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class MineFragment extends Fragment implements Serializable {
    FragmentMineBinding binding;
    private Activity parentActivity;

    private List<String>strings=new ArrayList<>();
    private List<Integer>icons=new ArrayList<>();
    private RecyclerView.LayoutManager manager;
    private CommonRecyclerViewAdapter adapter;
    private NavController controller;
    public MineViewModel viewModel;
    private boolean isFirst;

    @Inject
    SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String nickname;

    @Inject
    ApiService apiService;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = requireActivity();

        isFirst=true;

        editor=sp.edit();

        strings.add("我的账户");strings.add("设置");
        strings.add("关于");

        icons.add(R.drawable.ic_baseline_manage_accounts_24);icons.add(R.drawable.ic_baseline_settings_24);
        icons.add(R.drawable.ic_baseline_link_24);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MineViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine,container,false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        nickname=sp.getString(UserUtils.USERNAME+"nickname",UserUtils.USERNAME);

        if(isFirst){
            isFirst=false;
            requestUserInfo();
        }else {
            viewModel.getNickName().setValue(nickname);
        }

        viewModel.getHead().observe(this, s -> {
            GlideUtils.loadLocalImage(s,binding.imageHead,R.drawable.head);
        });

        viewModel.getBackground().observe(this,s -> {
            GlideUtils.loadLocalImage(s,binding.imageBack,R.drawable.background);
        });

        ViewGroup.LayoutParams params=binding.imageBack.getLayoutParams();
        params.height= (int) (6/9.0 * SystemUtils.WINDOW_WIDTH);
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
            Bundle bundle=new Bundle();
            bundle.putSerializable("fragment",this);
            bundle.putInt("ratioX",3);
            bundle.putInt("ratioY",2);
            bundle.putString("name","background");
            controller.navigate(R.id.action_mineFragment_to_chooseDialog,bundle);
            return true;
        });

        binding.imageHead.setOnLongClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putSerializable("fragment",this);
            bundle.putInt("ratioX",1);
            bundle.putInt("ratioY",1);
            bundle.putString("name","head");
            controller.navigate(R.id.action_mineFragment_to_chooseDialog,bundle);
            return true;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImagePicker.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void requestUserInfo() {
        apiService.getInfo().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        String reNickname=result.getJSONObject("info").getString("nickname");
                        Log.d("lmx", "nickname: "+reNickname);
                        if(!reNickname.equals(UserUtils.USERNAME) && !reNickname.equals(nickname)){
                            editor.putString(UserUtils.USERNAME+"nickname",reNickname);
                            editor.commit();
                            nickname=reNickname;
                        }
                    }
                    viewModel.getNickName().setValue(nickname);
                }catch (JSONException | IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                viewModel.getNickName().setValue(nickname);
            }
        });
    }
}