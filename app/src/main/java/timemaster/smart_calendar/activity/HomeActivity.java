package timemaster.smart_calendar.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import timemaster.smart_calendar.R;
import timemaster.smart_calendar.service.MyLocationService;
import timemaster.smart_calendar.sqlite.MyLocationHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    List<String> permissionList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView=findViewById(R.id.view_nav);
        NavController navController=Navigation.findNavController(this,R.id.fragment_nav);

        if(Build.VERSION.SDK_INT>=23 && getApplicationInfo().targetSdkVersion>=23){
            checkPermission();
        }

        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        startService(new Intent(this, MyLocationService.class));

    }

    private void checkPermission() {
        /*
        <!--用于进行网络定位-->
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        <!--用于访问GPS定位-->
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        <!--用于获取运营商信息，用于支持提供运营商信息相关的接口-->
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
        <!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
        <!--用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
        <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
        <!--用于访问网络，网络定位需要上网-->
        <uses-permission android:name="android.permission.INTERNET"/>
        <!--用于写入缓存数据到扩展存储卡-->
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        <!--用于申请调用A-GPS模块-->
        <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"/>
        <!--android 9.0以上需要-->
        <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
        <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
        <!--用于读取手机当前的状态-->
        <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
         */
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.INTERNET);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
        }
        if(Build.VERSION.SDK_INT>28 && getApplicationInfo().targetSdkVersion>28){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.FOREGROUND_SERVICE);
            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this,permissions,0);
        }
    }
}