package team.time.smartcalendar.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import team.time.smartcalendar.fragmentsfirst.MainFirstFragment;
import team.time.smartcalendar.Fragmentssecond.MainSecondFragment;
import team.time.smartcalendar.fragmentsthird.MainThirdFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment>fragments=new ArrayList<>();
    private final BottomNavigationView bottomNavigationView;

    public MainViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,BottomNavigationView bottomNavigationView) {
        super(fragmentManager, lifecycle);

        this.bottomNavigationView=bottomNavigationView;

        fragments.add(new MainFirstFragment());
        fragments.add(new MainSecondFragment());
        fragments.add(new MainThirdFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
