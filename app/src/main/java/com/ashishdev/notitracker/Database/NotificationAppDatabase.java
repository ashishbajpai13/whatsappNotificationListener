package com.ashishdev.notitracker.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Notification.class}, version = 1)
public abstract class NotificationAppDatabase extends RoomDatabase {

    public abstract NotificationDAO getNotificationDAO();

}
