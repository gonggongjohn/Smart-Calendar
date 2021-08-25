package team.time.smartcalendar.requests;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CookieManager implements CookieJar {
    private Map<HttpUrl, List<Cookie>> cookieStore;

    public CookieManager(Map<HttpUrl, List<Cookie>>cookieStore) {
        this.cookieStore=cookieStore;
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> cookies=cookieStore.get(HttpUrl.parse(httpUrl.host()));
        if(cookies==null){
            cookies=new ArrayList<>();
        }

        return cookies;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        cookieStore.put(HttpUrl.parse(httpUrl.host()),list);
    }
}