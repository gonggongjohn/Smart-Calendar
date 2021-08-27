package team.time.smartcalendar.utils;

import android.location.Location;
import org.jetbrains.annotations.NotNull;

public class UserUtils {
    public static String USERNAME="";
    public static Double USER_LATITUDE=40D;
    public static Double USER_LONGITUDE=116D;

    @NotNull
    public static Location getMyLocation(){
        Location location=new Location("");
        location.setLatitude(USER_LATITUDE);
        location.setLongitude(USER_LONGITUDE);
        return location;
    }
}
