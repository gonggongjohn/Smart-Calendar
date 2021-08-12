package team.time.smartcalendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import team.time.smartcalendar.adapters.MainViewPagerAdapter;
import team.time.smartcalendar.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

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