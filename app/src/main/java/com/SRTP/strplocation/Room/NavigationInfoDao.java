package com.SRTP.strplocation.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NavigationInfoDao {
    @Insert
    void InsertNavigationInfos(NavigationMessageForDB... navigationMessageForDBS);

    @Update
    void UpdateNavigationInfos(NavigationMessageForDB... navigationMessageForDBS);

    @Delete
    void DeleteNavigationInfos(NavigationMessageForDB... navigationMessageForDBS);

    @Query("DELETE FROM NavigationMessageForDB")
    void DeleteAll();

    @Query("SELECT * FROM NavigationMessageForDB ORDER BY ID")
    LiveData<List<NavigationMessageForDB>> getAllNavigationInfos();

    @Query("SELECT * FROM NavigationMessageForDB Where prn = :prn")
    List<NavigationMessageForDB> getMatchedNavigationInfos(int prn);
}
