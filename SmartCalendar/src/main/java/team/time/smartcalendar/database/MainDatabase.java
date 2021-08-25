package team.time.smartcalendar.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.dataBeans.CalendarItem;

@Database(entities = {CalendarItem.class},version = 1)
public abstract class MainDatabase extends RoomDatabase {
    public abstract CalendarItemDao getCalendarItemDao();
}
