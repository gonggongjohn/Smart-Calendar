package team.time.smartcalendar.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.dataBeans.ScheduleItem;
import team.time.smartcalendar.requests.ApiService;

import java.io.IOException;
import java.util.List;

public class RequestUtils {

    @NotNull
    public static RequestBody createAddOrUpdateRequestBody(CalendarItem item) {
        ScheduleItem scheduleItem = new ScheduleItem(item);
        JSONObject body = new JSONObject();
        try {
            body.put("uuid", scheduleItem.uuid);
            body.put("name", scheduleItem.name);
            body.put("category", scheduleItem.categoryId);
            body.put("start", scheduleItem.start);
            body.put("end", scheduleItem.end);
            // 位置信息
            if (!scheduleItem.position.equals("")) {
                JSONObject position = new JSONObject();
                position.put("name", scheduleItem.position);
                position.put("latitude", scheduleItem.latitude);
                position.put("longitude", scheduleItem.longitude);
                body.put("position", position);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );
    }

    public static RequestBody createArrangeRequestBody(
            String name,int categoryId,int duration,double from,double to
    ) {
        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("category", categoryId);
            body.put("duration", duration);
            body.put("from", from);
            body.put("to", to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );
    }

    public static void requestCategories(ApiService apiService, List<String> categories) {
        try {
            retrofit2.Response<ResponseBody> response = apiService.getCategory().execute();
            try {
                JSONObject result = new JSONObject(response.body().string());
                int status = result.getInt("status");
                Log.d("lmx", "status: " + status);
                if (status == 1) {
                    JSONArray categoryArray = result.getJSONArray("category");

                    for (int i = 1; i < categoryArray.length(); i++) {
                        categories.add(categoryArray.getJSONObject(i).getString("name"));
                    }
                }
                categories.add("其他");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // 请求失败
            categories.add("其他");
        }
    }

    public static void requestUpdateItems(ApiService apiService, boolean[] isSuccess, CalendarItem item) {
        RequestBody requestBody = RequestUtils.createAddOrUpdateRequestBody(item);
        try {
            Response<ResponseBody> response = apiService.update(requestBody).execute();
            try {
                JSONObject result = new JSONObject(response.body().string());
                Log.d("lmx", "requestUpdateItems: " + result);
                int status = result.getInt("status");
                if (status == 1) {
                    isSuccess[0] = true;
                }
            } catch (JSONException e) {
                Log.d("lmx", "requestUpdateItems: " + e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void requestAddItems(ApiService apiService, boolean[] isSuccess, CalendarItem item) {
        RequestBody requestBody= RequestUtils.createAddOrUpdateRequestBody(item);
        try {
            Response<ResponseBody> response=apiService.add(requestBody).execute();
            try {
                JSONObject result=new JSONObject(response.body().string());
                Log.d("lmx", "requestAddItems: "+result);
                int status=result.getInt("status");
                if(status==1){
                    isSuccess[0]=true;
                }
            }catch (JSONException e){
                Log.d("lmx", "requestAddItems: "+e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void requestDeleteItems(ApiService apiService, boolean[] isSuccess, String uuid){
        JSONObject body=new JSONObject();
        try {
            body.put("uuid",uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody=RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );

        try {
            Response<ResponseBody> response=apiService.remove(requestBody).execute();
            try {
                JSONObject result=new JSONObject(response.body().string());
                Log.d("lmx", "requestDeleteItems: "+result);
                int status=result.getInt("status");
                if(status==1){
                    isSuccess[0]=true;
                }
            }catch (JSONException e){
                Log.d("lmx", "requestDeleteItems: "+e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void requestArrangeItems(
            ApiService apiService, boolean[] isSuccess, List<CalendarItem> items, RequestBody requestBody, Activity activity) {
        try {
            Response<ResponseBody> response=apiService.arrange(requestBody).execute();
            try {
                JSONObject result=new JSONObject(response.body().string());
                Log.d("lmx", "requestArrangeItems: "+result);
                int status=result.getInt("status");
                if(status==1){
                    // 请求成功
                    isSuccess[0]=true;
                    // 获取日程列表
                    JSONArray array=result.getJSONArray("schedule");
                    for(int i=0;i<array.length();i++){
                        Log.d("lmx", "requestArrangeItems: ");
                        array.getJSONObject(i).put("uuid","0");
                        CalendarItem item=new CalendarItem(new ScheduleItem(array.getJSONObject(i)));
                        items.add(item);
                    }
                }else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                    });
                }
            }catch (JSONException e){
                Log.d("lmx", "requestArrangeItems: "+e);
            }
        } catch (IOException e) {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "网络未连接", Toast.LENGTH_SHORT).show();
            });
        }
    }

    public static void requestUpdateUser(ApiService apiService,JSONObject body) {
        RequestBody requestBody=RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );

        apiService.updateUser(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) { }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }
}
