package team.time.smartcalendar.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import team.time.smartcalendar.dataBeans.CalendarItem;
import team.time.smartcalendar.dataBeans.ScheduleItem;

public class RequestUtils {

    @NotNull
    public static RequestBody createAddOrUpdateRequestBody(CalendarItem item){
        ScheduleItem scheduleItem=new ScheduleItem(item);
        JSONObject body=new JSONObject();
        try {
            body.put("uuid",scheduleItem.uuid);
            body.put("name",scheduleItem.name);
            body.put("category",scheduleItem.categoryId);
            body.put("start",scheduleItem.start);
            body.put("end",scheduleItem.end);
            // 位置信息
            if(!scheduleItem.position.equals("")){
                JSONObject position=new JSONObject();
                position.put("name",scheduleItem.position);
                position.put("latitude",scheduleItem.latitude);
                position.put("longitude",scheduleItem.longitude);
                body.put("position",position);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(
                body.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );
    }
}
