package team.time.smartcalendar.hiltModule;

import android.app.Application;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.dao.CalendarItemDao;
import team.time.smartcalendar.database.MainDatabase;

import javax.inject.Singleton;

@InstallIn(SingletonComponent.class)
@Module
public class RoomModule {

    @Provides
    @Singleton
    MainDatabase provideMainDatabase(Application application){
        return Room
                .databaseBuilder(application,MainDatabase.class,"calendar.db")
                .build();
    }

    @Provides
    @Singleton
    CalendarItemDao provideCalendarItemDao(@NotNull MainDatabase mainDatabase){
        return mainDatabase.getCalendarItemDao();
    }
}
