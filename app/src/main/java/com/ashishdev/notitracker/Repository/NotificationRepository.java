package com.ashishdev.notitracker.Repository;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.ashishdev.notitracker.Database.Notification;
import com.ashishdev.notitracker.Database.NotificationAppDatabase;
import com.ashishdev.notitracker.R;
import com.ashishdev.notitracker.Utils.TimeUtils;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationRepository {

    private Application application;
    private NotificationAppDatabase appDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Notification>> mutableLiveData = new MutableLiveData<>();

    public void setMutableLiveData(MutableLiveData<List<Notification>> mutableLiveData) {
        this.mutableLiveData = mutableLiveData;
    }

    public NotificationRepository(Application application) {
        this.application = application;
        appDatabase = Room.databaseBuilder(application.getApplicationContext(),
                NotificationAppDatabase.class, "Notifications")
                .build();
        compositeDisposable.add(
                appDatabase.getNotificationDAO().getAllNotifications().
                        subscribeOn(Schedulers.computation()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<List<Notification>>() {
                            @Override
                            public void accept(List<Notification> notifications) throws Exception {
                                mutableLiveData.postValue(notifications);
                            }
                        })
        );
    }

    public void addNotification(final String notificationTitle, final String notificationText) {
        compositeDisposable.add(
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        appDatabase.getNotificationDAO().addNotification(
                                new Notification(notificationTitle, notificationText,
                                        new TimeUtils().getCurrentTimeStamp(), 0,
                                        getPhoneNumber(notificationTitle, application.getApplicationContext())));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

    public void deleteSingleNotification(final String notificationTitle, final String notificationText,
                                         final String timeStamp, final long id, final String phoneNo){
        compositeDisposable.add(
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        appDatabase.getNotificationDAO().deleteNotification(
                                new Notification(notificationTitle, notificationText, timeStamp, id, phoneNo));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        })
        );
    }

    public void deleteNotifications(){
        appDatabase.getNotificationDAO().deleteAllNoficiations();
    }

    public MutableLiveData<List<Notification>> getMutableLiveData() {
        return mutableLiveData;
    }

    public void clear(){
        compositeDisposable.clear();
    }

    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection, selection, null, null);
            if (c.moveToFirst()) {
                ret = c.getString(0);
            }
            c.close();
            if (ret == null)
                ret = context.getString(R.string.unsaved);
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

