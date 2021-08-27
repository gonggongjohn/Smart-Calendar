package team.time.smartcalendar.dataBeans;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.utils.UserUtils;

import java.io.Serializable;

/**
 * schedule item data structure locally
 */
@Entity(tableName = "calendar_item")
public class CalendarItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id",typeAffinity = ColumnInfo.INTEGER)
    public int id;
    @ColumnInfo(name = "dirty",typeAffinity = ColumnInfo.INTEGER)
    public int dirty; // 0 已同步，1 添加后未同步，2 修改后未同步
    @ColumnInfo(name = "username",typeAffinity = ColumnInfo.TEXT)
    public String username;

    @ColumnInfo(name = "uuid",typeAffinity = ColumnInfo.TEXT)
    public String uuid="";
    @ColumnInfo(name = "info",typeAffinity = ColumnInfo.TEXT)
    public String info="";

    @ColumnInfo(name = "position",typeAffinity = ColumnInfo.TEXT)
    public String position="";
    @ColumnInfo(name = "latitude",typeAffinity = ColumnInfo.REAL)
    public double latitude=0.0;
    @ColumnInfo(name = "longitude",typeAffinity = ColumnInfo.REAL)
    public double longitude=0.0;

    @ColumnInfo(name = "details",typeAffinity = ColumnInfo.TEXT)
    public String details="";
    @ColumnInfo(name = "category_id",typeAffinity = ColumnInfo.INTEGER)
    public int categoryId=1;
    @ColumnInfo(name = "category_name",typeAffinity = ColumnInfo.TEXT)
    public String categoryName="其他";
    @ColumnInfo(name = "start_time",typeAffinity = ColumnInfo.INTEGER)
    public long startTime=0;
    @ColumnInfo(name = "end_time",typeAffinity = ColumnInfo.INTEGER)
    public long endTime=0;

    public CalendarItem(int id,int dirty,String uuid,String info,String position,double latitude,double longitude,String details,int categoryId,String categoryName,long startTime,long endTime) {
        this.id = id;
        this.dirty = dirty;
        this.username = UserUtils.USERNAME;
        this.uuid = uuid;
        this.info = info;

        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;

        this.details = details;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Ignore
    public CalendarItem(){}

    @Ignore
    public CalendarItem(@NotNull ScheduleItem scheduleItem){
        this.dirty=0;
        this.username=UserUtils.USERNAME;
        this.uuid=scheduleItem.uuid;
        this.info=scheduleItem.name;

        this.position=scheduleItem.position;
        this.latitude=scheduleItem.latitude;
        this.longitude=scheduleItem.longitude;

        this.details="";
        this.categoryId=scheduleItem.categoryId;
        this.categoryName=scheduleItem.categoryName;
        this.startTime=(long) scheduleItem.start * 1000;
        this.endTime=(long) scheduleItem.end * 1000;
    }

    @NotNull
    @Override
    public String toString() {
        return "CalendarItem{" +
                "id=" + id +
                ", dirty=" + dirty +
                ", username='" + username + '\'' +
                ", uuid='" + uuid + '\'' +
                ", info='" + info + '\'' +
                ", position='" + position + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", details='" + details + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
