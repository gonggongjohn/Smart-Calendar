package timemaster.smart_calendar.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyLocationHelper extends SQLiteOpenHelper {
    public MyLocationHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE locations (\n" +
                "    id        INTEGER   PRIMARY KEY AUTOINCREMENT\n" +
                "                        NOT NULL,\n" +
                "    latitude  DOUBLE    NOT NULL,\n" +
                "    longitude DOUBLE    NOT NULL,\n" +
                "    describe  TEXT,\n" +
                "    ts        TIMESTAMP NOT NULL\n" +
                ")\n";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
