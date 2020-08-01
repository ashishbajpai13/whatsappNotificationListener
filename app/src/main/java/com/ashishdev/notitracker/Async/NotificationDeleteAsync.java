package com.ashishdev.notitracker.Async;

import android.os.AsyncTask;
import android.widget.Toast;

import com.ashishdev.notitracker.ViewModel.NotificationViewModel;

public class NotificationDeleteAsync extends AsyncTask<Void, Void, Void> {

    private NotificationViewModel notificationViewModel;
    private boolean clearAll;
    private String notificationTitle, notificationText, timeStamp;
    private long id;
    private String phoneNo;

    public NotificationDeleteAsync(NotificationViewModel notificationViewModel,
                                   boolean clearAll, String notificationTitle,
                                   String notificationText, String timeStamp, long id, String phoneNo) {
        this.notificationViewModel = notificationViewModel;
        this.clearAll = clearAll;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.timeStamp = timeStamp;
        this.id = id;
        this.phoneNo = phoneNo;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (clearAll)
            notificationViewModel.clearAllNotifications();
        else
            notificationViewModel.deleteSingleNotification(notificationTitle, notificationText, timeStamp, id, phoneNo);
            return null;
    }
}
