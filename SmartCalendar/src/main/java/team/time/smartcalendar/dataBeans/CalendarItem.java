package team.time.smartcalendar.dataBeans;

import androidx.annotation.NonNull;
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
public class CalendarItem implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id",typeAffinity = ColumnInfo.INTEGER)
    public int id;
    @ColumnInfo(name = "dirty",typeAffinity = ColumnInfo.INTEGER)
    public int dirty; // 0 已同步，1 添加后未同步，2 修改后未同步
    @ColumnInfo(name = "type",typeAffinity = ColumnInfo.INTEGER)
    public int type=0; // 0 常规，1 定时，2 动态
    @ColumnInfo(name = "list_id",typeAffinity = ColumnInfo.INTEGER)
    public long listId=0L; // 用时间戳表示日程链的ID
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

    @ColumnInfo(name = "category_id",typeAffinity = ColumnInfo.INTEGER)
    public int categoryId=1;
    @ColumnInfo(name = "category_name",typeAffinity = ColumnInfo.TEXT)
    public String categoryName="其他";
    @ColumnInfo(name = "start_time",typeAffinity = ColumnInfo.INTEGER)
    public long startTime=0;
    @ColumnInfo(name = "end_time",typeAffinity = ColumnInfo.INTEGER)
    public long endTime=0;

    public CalendarItem(int id,int dirty,int type,long listId,String uuid,String info,String position,double latitude,double longitude,int categoryId,String categoryName,long startTime,long endTime) {
        this.id = id;
        this.dirty = dirty;
        this.type = type;
        this.listId = listId;
        this.username = UserUtils.USERNAME;
        this.uuid = uuid;
        this.info = info;

        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;

        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Ignore
    public CalendarItem(CalendarItem item){
        this(item.id,item.dirty,item.type,item.listId,item.uuid,item.info,item.position,item.latitude,item.longitude,item.categoryId,item.categoryName,item.startTime,item.endTime);
    }

    @Ignore
    public CalendarItem(){}

    @Ignore
    public CalendarItem(@NotNull ScheduleItem scheduleItem){
        this.dirty=0;
        this.type=0;
        this.listId=0;
        this.username=UserUtils.USERNAME;
        this.uuid=scheduleItem.uuid;
        this.info=scheduleItem.name;

        this.position=scheduleItem.position;
        this.latitude=scheduleItem.latitude;
        this.longitude=scheduleItem.longitude;

        this.categoryId=scheduleItem.categoryId;
        this.categoryName=scheduleItem.categoryName;
        this.startTime=(long) scheduleItem.start * 1000;
        this.endTime=(long) scheduleItem.end * 1000;
    }

    @Override
    public String toString() {
        return "CalendarItem{" +
                "id=" + id +
                ", dirty=" + dirty +
                ", type=" + type +
                ", listId=" + listId +
                ", username='" + username + '\'' +
                ", uuid='" + uuid + '\'' +
                ", info='" + info + '\'' +
                ", position='" + position + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public String getTypeName(){
        String typeName;
        switch (type){
            case 1:
                typeName="定时";
                break;
            case 2:
                typeName="动态";
                break;
            default:
                typeName="常规";
                break;
        }
        return typeName;
    }

    public void copy(CalendarItem item){
        this.id = item.id;
        this.dirty = item.dirty;
        this.type = item.type;
        this.listId = item.listId;
        this.username = item.username;
        this.uuid = item.uuid;
        this.info = item.info;

        this.position = item.position;
        this.latitude = item.latitude;
        this.longitude = item.longitude;

        this.categoryId = item.categoryId;
        this.categoryName = item.categoryName;
        this.startTime = item.startTime;
        this.endTime = item.endTime;
    }

    @NonNull
    @NotNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
