package team.time.smartcalendar.hiltModule;

import com.haibin.calendarview.Calendar;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.requests.ApiService;
import team.time.smartcalendar.requests.CookieManager;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@InstallIn(SingletonComponent.class)
@Module
public class AppModule {

    @Provides
    @Singleton
    Map<HttpUrl, List<Cookie>> provideCookieStore(){
        return new HashMap<>();
    }

    @Named("all")
    @Provides
    @Singleton
    List<CalendarItem> provideCalendarItems(){
        return new ArrayList<>();
    }

    @Named("current")
    @Provides
    @Singleton
    List<CalendarItem> provideCurCalendarItems(){
        return new ArrayList<>();
    }

    @Provides
    @Singleton
    Map<String, Calendar> getMap(){
        return new HashMap<>();
    }

    @Provides
    @Singleton
    CookieManager provideCookieManager(Map<HttpUrl, List<Cookie>> cookieStore){
        return new CookieManager(cookieStore);
    }

    @Provides
    @Singleton
    OkHttpClient provideClient(CookieManager cookieManager){
        return new OkHttpClient.Builder()
                .cookieJar(cookieManager)
//                .addInterceptor(new HttpLoggingInterceptor(s -> {
//                    Log.d("lmx", "HttpLoggingInterceptor: "+s);
//                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(3, TimeUnit.SECONDS)
                .callTimeout(3,TimeUnit.SECONDS)
                .readTimeout(3,TimeUnit.SECONDS)
                .writeTimeout(3,TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl("http://139.196.44.94:3000/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    ApiService provideApiService(@NotNull Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }
}
