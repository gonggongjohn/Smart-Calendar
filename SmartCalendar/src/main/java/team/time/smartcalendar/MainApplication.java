package team.time.smartcalendar;

import android.app.Application;
import com.haibin.calendarview.Calendar;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.requests.CookieManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
    private Map<HttpUrl, List<Cookie>> cookieStore;
    private List<CalendarItem> calendarItems;
    private List<CalendarItem> curCalendarItems;
    private Map<String, Calendar> map;
    private OkHttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        cookieStore=new HashMap<>();
        calendarItems=new ArrayList<>();
        curCalendarItems=new ArrayList<>();
        map=new HashMap<>();
        client=new OkHttpClient.Builder()
                .cookieJar(new CookieManager(this))
                .connectTimeout(3, TimeUnit.SECONDS)
                .callTimeout(3,TimeUnit.SECONDS)
                .readTimeout(3,TimeUnit.SECONDS)
                .writeTimeout(3,TimeUnit.SECONDS)
                .build();
    }

    public Map<HttpUrl, List<Cookie>> getCookieStore() {
        return cookieStore;
    }

    public List<CalendarItem> getCalendarItems() {
        return calendarItems;
    }

    public List<CalendarItem>getCurCalendarItems(){
        return curCalendarItems;
    }

    public Map<String, Calendar> getMap() {
        return map;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
