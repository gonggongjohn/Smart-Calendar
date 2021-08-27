package team.time.smartcalendar.utils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {
    public static String categories=
            "餐饮服务|"
            +"购物服务|"
            +"生活服务|"
            +"体育休闲服务|"
            +"医疗保健服务|"
            +"住宿服务|"
            +"风景名胜|"
            +"商务住宅|"
            +"政府机构及社会团体|"
            +"科教文化服务|"
            +"金融保险服务|"
            +"公司企业";

    public static void moveCamera(@NotNull AMap map, double latitude, double longitude) {
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(
                        latitude,
                        longitude
                ), 16, 0, 0)
        ));
    }

    public static void animateCamera(@NotNull AMap map, double latitude, double longitude) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(
                        latitude,
                        longitude
                ), 16, 0, 0)
        ));
    }


    public static Marker addMarker(@NotNull AMap map, double latitude, double longitude) {
        MarkerOptions options=new MarkerOptions();
        options.position(new LatLng(latitude,longitude));
        options.setFlat(true);
        return map.addMarker(options);
    }
}
