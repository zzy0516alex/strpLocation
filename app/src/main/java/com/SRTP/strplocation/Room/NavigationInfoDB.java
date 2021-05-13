package com.SRTP.strplocation.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NavigationMessageForDB.class},version=5,exportSchema = false)
public abstract class NavigationInfoDB extends RoomDatabase {
    private static NavigationInfoDB NAVIGATIONINFO_DATABASE;
    public static synchronized NavigationInfoDB getDataBase(Context context){
        if (NAVIGATIONINFO_DATABASE == null){
            NAVIGATIONINFO_DATABASE = Room.databaseBuilder(context.getApplicationContext(),NavigationInfoDB.class,"Navi_DB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return NAVIGATIONINFO_DATABASE;
    }
    public abstract NavigationInfoDao getNavigationInfoDao();
}
