package team.time.smartcalendar.hiltModule;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import org.jetbrains.annotations.NotNull;
import team.time.smartcalendar.R;

@InstallIn(ActivityComponent.class)
@Module
public class ActivityModule {

    @Provides
    @ActivityScoped
    SharedPreferences provideSharedPreferences(@NotNull Activity activity){
        return activity.getSharedPreferences(
                activity.getString(R.string.USER_INFO),
                Context.MODE_PRIVATE);
    }
}
