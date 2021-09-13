package team.time.smartcalendar.fragmentsthird;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.time.smartcalendar.R;
import team.time.smartcalendar.adapters.MeqRecyclerViewAdapter;
import team.time.smartcalendar.dataBeans.MeqItem;
import team.time.smartcalendar.databinding.FragmentMeqBinding;
import team.time.smartcalendar.databinding.ItemMeqBinding;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.utils.RequestUtils;
import team.time.smartcalendar.utils.SystemUtils;
import team.time.smartcalendar.utils.UserUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class MeqFragment extends Fragment {
    private Activity parentActivity;
    private FragmentMeqBinding binding;
    private NavController controller;
    private List<MeqItem>items;
    private MeqRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager manager;

    int[] choices=new int[20];

    @Inject
    ApiService apiService;

    @Inject
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity=requireActivity();
        editor=sp.edit();
        requestMeq();
        items=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 重写回退键
        SystemUtils.setBack(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meq,container,false);

        SystemUtils.setStatusImage(binding.statusImage);
        SystemUtils.setAction(binding.action,"MEQ-SA",R.drawable.ic_baseline_close_24,R.drawable.ic_baseline_check_24);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        adapter = new MeqRecyclerViewAdapter(items){
            @Override
            protected void changeView(ItemMeqBinding binding, List<MeqItem> is, int position) {
                // 监听
                binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    choices[position]=checkedId;
                });
                // 设置选项
                binding.radioGroup.check(choices[position]);
            }
        };
        manager = new LinearLayoutManager(parentActivity);
        binding.viewMeq.setLayoutManager(manager);
        binding.viewMeq.setAdapter(adapter);

        binding.action.imageLeft.setOnClickListener(v -> {
            controller.popBackStack();
        });

        binding.action.imageRight.setOnClickListener(v -> {
            calculateAndBack();
        });
    }

    private void calculateAndBack() {
        int isFinished=0;
        int score=0;
        for(int i=0;i<items.size();i++){
            // 使用switch会有warning
            if(choices[i]==R.id.radio1){
                score+=items.get(i).options.get(0);
            }else if(choices[i]==R.id.radio2){
                score+=items.get(i).options.get(1);
            }else if(choices[i]==R.id.radio3){
                score+=items.get(i).options.get(2);
            }else if(choices[i]==R.id.radio4){
                score+=items.get(i).options.get(3);
            }else if(choices[i]==R.id.radio5){
                score+=items.get(i).options.get(4);
            }else {
                isFinished=i+1;
                break;
            }
        }
        if(isFinished==0){
            Log.d("lmx", "calculateAndBack: "+score);
            // 同步
            JSONObject body=new JSONObject();
            try {
                body.put("meq_feature",score);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestUtils.requestUpdateUser(apiService,body);
            // 本地更新
            editor.putInt(UserUtils.USERNAME+"meq",score);
            editor.commit();
            controller.popBackStack();
        }else {
            Toast.makeText(parentActivity, "第"+isFinished+"个问题未作答", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestMeq() {
        apiService.getMeq().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject result=new JSONObject(response.body().string());
                    int status=result.getInt("status");
                    if(status==1){
                        JSONArray array=result.getJSONArray("question");
                        for(int i=0;i<array.length();i++){
                            items.add(new MeqItem(array.getJSONObject(i)));
                        }
                        parentActivity.runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                        });
                    }
                }catch (JSONException | IOException e){
                    Log.d("lmx", "requestMeq: "+e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                parentActivity.runOnUiThread(() -> {
                    Toast.makeText(parentActivity, "网络未连接", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}