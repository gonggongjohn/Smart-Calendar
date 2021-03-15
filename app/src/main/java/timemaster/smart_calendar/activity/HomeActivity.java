package timemaster.smart_calendar.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import timemaster.smart_calendar.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView=findViewById(R.id.view_nav);
        NavController navController=Navigation.findNavController(this,R.id.fragment_nav);

        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }
}