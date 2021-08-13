package team.time.smartcalendar;

import android.app.Application;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainApplication extends Application {
    private Map<HttpUrl, List<Cookie>> cookieStore;

    @Override
    public void onCreate() {
        super.onCreate();

        cookieStore=new HashMap<>();
    }

    public Map<HttpUrl, List<Cookie>> getCookieStore() {
        return cookieStore;
    }
}
