package team.time.smartcalendar.requests;

import android.app.Application;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.MainApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CookieManager implements CookieJar {
    private Map<HttpUrl, List<Cookie>> cookieStore;


    public CookieManager(Application app) {
        cookieStore = ((MainApplication)app).getCookieStore();
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> cookies=cookieStore.get(httpUrl);
        if(cookies==null){
            cookies=new ArrayList<>();
        }

        return cookies;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        cookieStore.put(httpUrl,list);
    }
}
