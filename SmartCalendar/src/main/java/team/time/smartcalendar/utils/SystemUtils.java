package team.time.smartcalendar.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

public class SystemUtils {
    // 隐藏键盘
    public static void hideKeyBoard(Context context,List<View>viewList){
        if (viewList == null) return;

        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
