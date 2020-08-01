package com.ashishdev.notitracker.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ashishdev.notitracker.Database.Notification;
import com.ashishdev.notitracker.Repository.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {

    NotificationRepository notificationRepository;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notificationRepository = new NotificationRepository(application);
    }

    public LiveData<List<Notification>> getAllNotifications() {
        return notificationRepository.getMutableLiveData();
    }

    public void createNotification(String notificationTitle, String notificationText){
        notificationRepository.addNotification(notificationTitle, notificationText);
    }

    public void deleteSingleNotification(String notificationTitle, String notificationText, String timeStamp, long id, String phoneNo){
        notificationRepository.deleteSingleNotification(notificationTitle, notificationText, timeStamp, id, phoneNo);
    }

    public void clearAllNotifications(){
        notificationRepository.deleteNotifications();
    }

    public void clear(){
        notificationRepository.clear();
    }
}
