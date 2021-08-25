package team.time.smartcalendar.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import team.time.smartcalendar.dataBeans.CalendarItem;

import java.util.List;

@Dao
public interface CalendarItemDao {

    // 增
    @Insert(entity = CalendarItem.class)
    void insertCalendarItem(CalendarItem... calendarItems);

    // 删: 根据uuid
    @Query("DELETE FROM calendar_item WHERE uuid=:uuid")
    void deleteCalendarItemByUUID(String uuid);

    // 查: 获取当前用户的所有数据
    @Query("SELECT * FROM calendar_item WHERE username=:username")
    List<CalendarItem>getAllCalendarItems(String username);

    // 查: 获取当前用户的所有uuid
    @Query("SELECT uuid FROM calendar_item WHERE username=:username")
    List<String>getAllUUID(String username);

    // 查: 获取当前用户的未同步数据
    @Query("SELECT * FROM calendar_item WHERE username=:username AND dirty!=0")
    List<CalendarItem>getSycCalendarItems(String username);

    // 查: 根据uuid获取id
    @Query("SELECT id FROM calendar_item WHERE uuid=:uuid")
    int getIdByUUID(String uuid);

    // 改
    @Update(entity = CalendarItem.class)
    void updateCalendarItem(CalendarItem... calendarItems);
}
