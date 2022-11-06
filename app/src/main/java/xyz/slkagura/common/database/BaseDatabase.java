package xyz.slkagura.common.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

// @Database(version = 1, entities = { UserEntity.class })
public abstract class BaseDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "slkagura.db";
    
    private static volatile BaseDatabase sInstance;
    
    public static synchronized BaseDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BaseDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), BaseDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }
    // public abstract UserDao getUserDao();
}
