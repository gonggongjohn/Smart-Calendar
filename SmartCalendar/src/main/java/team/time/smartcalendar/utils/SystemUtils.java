package team.time.smartcalendar.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;

public class SystemUtils {
    public static int STATUS_BAR_HEIGHT;

    // 隐藏键盘
    public static void hideKeyBoard(Context context,List<View>viewList){
        if (viewList == null) return;

        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 重写回退键
    public static void setBack(Fragment fragment) {
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

}
