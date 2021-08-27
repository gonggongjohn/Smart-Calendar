package team.time.smartcalendar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;
import team.time.smartcalendar.adapters.MainViewPagerAdapter;
import team.time.smartcalendar.databinding.FragmentMainBinding;
import team.time.smartcalendar.utils.UserUtils;

import javax.inject.Inject;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    private FragmentMainBinding binding;

    @Inject
    SharedPreferences sp;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserUtils.USER_LATITUDE=Double.parseDouble(sp.getString("latitude","39.908671"));
        UserUtils.USER_LONGITUDE=Double.parseDouble(sp.getString("longitude","116.397454"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewPagerAdapter adapter=new MainViewPagerAdapter(
                getParentFragmentManager(),
                getLifecycle(),
                binding.bottomNavigationView);
        binding.mainViewPager.setAdapter(adapter);

        // 不允许用户滑动
        binding.mainViewPager.setUserInputEnabled(false);

        // bottomNavigationView关联mainViewPager
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.mainFirstFragment:
                    binding.mainViewPager.setCurrentItem(0,false);
                    break;
                case R.id.mainSecondFragment:
                    binding.mainViewPager.setCurrentItem(1,false);
                    break;
                case R.id.mainThirdFragment:
                    binding.mainViewPager.setCurrentItem(2,false);
                    break;
            }
            return true;
        });
        binding.bottomNavigationView.setOnNavigationItemReselectedListener(item -> {});
    }
}