package team.time.smartcalendar.dataBeans;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * schedule item data structure receive from server
 */
public class ScheduleItem {
    public String uuid;
    public String name;
    public int categoryId;
    public String categoryName;
    public double start;
    public double end;

    public ScheduleItem(JSONObject jsonObject) throws JSONException {
        this.uuid=jsonObject.getString("uuid");
        this.name=jsonObject.getString("name");
        this.categoryId=jsonObject.getJSONObject("category").getInt("id");
        this.categoryName=jsonObject.getJSONObject("category").getString("name");
        this.start=jsonObject.getDouble("start");
        this.end=jsonObject.getDouble("end");
    }

    public ScheduleItem(CalendarItem calendarItem){
        this.uuid=calendarItem.uuid;
        this.name=calendarItem.info;
        this.categoryId=calendarItem.categoryId;
        this.categoryName=calendarItem.categoryName;
        this.start=calendarItem.startTime/1000.0;
        this.end=calendarItem.endTime/1000.0;
    }

    @NotNull
    @Override
    public String toString() {
        return "ScheduleItem{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
