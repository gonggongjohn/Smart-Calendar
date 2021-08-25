package team.time.smartcalendar.requests;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/login")
    Call<ResponseBody> login(@Body RequestBody body);

    @POST("/user/register")
    Call<ResponseBody> register(@Body RequestBody body);

    @GET("/calendar/fetch")
    Call<ResponseBody> fetch();

    @POST("/calendar/remove")
    Call<ResponseBody> remove(@Body RequestBody body);

    @GET("/calendar/category")
    Call<ResponseBody> getCategory();

    @POST("/calendar/add")
    Call<ResponseBody> add(@Body RequestBody body);
}
