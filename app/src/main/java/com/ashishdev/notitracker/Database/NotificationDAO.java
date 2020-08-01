package com.ashishdev.notitracker.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface NotificationDAO {

    @Insert
    public long addNotification(Notification notification);

    @Update
    public void updateNotification(Notification notification);

    @Delete
    public void deleteNotification(Notification notification);

    @Query("select * from Notifications")
    Flowable<List<Notification>> getAllNotifications();

    @Query("delete from Notifications")
    public void deleteAllNoficiations();
}
