package timemaster.smart_calendar.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import timemaster.smart_calendar.sqlite.MyLocationHelper;

public class MyLocationService extends Service {
    public MyLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyLocationHelper helper=new MyLocationHelper(getApplicationContext(),"data.db",null,1);
                final SQLiteDatabase db=helper.getWritableDatabase();

                AMapLocationClient aMapLocationClient=new AMapLocationClient(getApplicationContext());
                AMapLocationClientOption locationClientOption=new AMapLocationClientOption();

                locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                locationClientOption.setGpsFirst(false);
                locationClientOption.setNeedAddress(true);
                locationClientOption.setHttpTimeOut(1000*30);
                locationClientOption.setOnceLocation(true);
                locationClientOption.setWifiScan(true);

                aMapLocationClient.setLocationOption(locationClientOption);
                aMapLocationClient.setLocationListener(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                        if(aMapLocation!=null){
                            String sql="insert into locations(latitude,longitude,describe,ts)" +
                                    "values('"+aMapLocation.getLatitude()+"'," +
                                    "'"+aMapLocation.getLongitude()+"'," +
                                    "'"+aMapLocation.getDescription()+"'," +
                                    "'"+System.currentTimeMillis()+"')";
                            db.execSQL(sql);
                            Log.e("OnLocationChanged","("+aMapLocation.getLatitude()+", "+aMapLocation.getLongitude()+")");
                        }else {
                            Log.e("OnLocationChanged","定位失败");
                        }
                    }
                });
                aMapLocationClient.startLocation();
            }
        }).start();

        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+1000*10;
        Intent i=new Intent(this,MyLocationService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }
}
