package team.time.smartcalendar.dataBeans;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * schedule item data structure locally
 */
public class CalendarItem implements Serializable {
    public String uuid="";
    public String info="";
    public String position="";
    public String details="";
    public int categoryId=1;
    public String categoryName="其他";
    public long startTime=0;
    public long endTime=0;

    public CalendarItem(){}

    public CalendarItem(ScheduleItem scheduleItem){
        this.uuid=scheduleItem.uuid;
        this.info=scheduleItem.name;
        this.position="";
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
                "uuid='" + uuid + '\'' +
                ", info='" + info + '\'' +
                ", position='" + position + '\'' +
                ", details='" + details + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
