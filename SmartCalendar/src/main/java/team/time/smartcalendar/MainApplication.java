package team.time.smartcalendar;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 检测内存泄露
//        LeakCanary.install(this);
    }
}
