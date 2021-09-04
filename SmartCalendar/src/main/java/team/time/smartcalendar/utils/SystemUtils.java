package team.time.smartcalendar.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.databinding.ActionCommonBinding;

import java.util.ArrayList;
import java.util.List;

public class SystemUtils {
    public static int WINDOW_WIDTH;
    public static int STATUS_BAR_HEIGHT;

    // 填充statusBar
    public static void setStatusImage(@NotNull ImageView image){
        ViewGroup.LayoutParams params=image.getLayoutParams();
        params.height= SystemUtils.STATUS_BAR_HEIGHT;
        image.setLayoutParams(params);
    }

    // 填充actionBar
    public static void setAction(@NotNull ActionCommonBinding action, String title, int left, int right) {
        action.textTitle.setText(title);
        if(left!=-1){
            action.imageLeft.setImageResource(left);
        }else {
            action.imageLeft.setVisibility(View.INVISIBLE);
        }
        if(right!=-1){
            action.imageRight.setImageResource(right);
        }else {
            action.imageRight.setVisibility(View.INVISIBLE);
        }

    }

    // 隐藏键盘
    public static void hideKeyBoard(Context context,List<View>viewList){
        if (viewList == null) return;

        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 重写回退键
    public static void setBack(@NotNull Fragment fragment) {
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(fragment).popBackStack();
            }
        };
        fragment.requireActivity().getOnBackPressedDispatcher().addCallback(fragment,callback);
    }

    // 检查权限
    public static void checkPermission(Activity activity,List<String> permissionList){
        String[] permissions=permissionList.toArray(new String[0]);
        ActivityCompat.requestPermissions(activity,permissions,0);
    }

    // 检查定位权限
    public static void checkLocatePermission(Activity activity){
        if(Build.VERSION.SDK_INT>=23 && activity.getApplicationInfo().targetSdkVersion>=23){
            List<String> permissionList=new ArrayList<>();
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (!permissionList.isEmpty()){
                checkPermission(activity,permissionList);
            }
        }
    }
}
