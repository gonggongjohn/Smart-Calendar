package team.time.smartcalendar.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import team.time.smartcalendar.utils.UserUtils;

public class MyLocationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            AMapLocationClient client=new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption option=new AMapLocationClientOption();

            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setGpsFirst(false);
            option.setHttpTimeOut(10000);
            option.setOnceLocation(true);

            client.setLocationOption(option);
            client.setLocationListener(aMapLocation -> {
                if(aMapLocation.getLatitude()!=0.0 && aMapLocation.getLongitude()!=0.0){
                    UserUtils.USER_LATITUDE=aMapLocation.getLatitude();
                    UserUtils.USER_LONGITUDE=aMapLocation.getLongitude();
                }
                Log.d("lmx", "onStartCommand: "+UserUtils.USER_LATITUDE+" "+UserUtils.USER_LONGITUDE);
            });
            client.startLocation();
        }).start();

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
