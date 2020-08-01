package com.ashishdev.notitracker.Service;


import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.ashishdev.notitracker.R;

public class WhatsappNotificationListener extends NotificationListenerService {

    private static final String WHATSAPP_PACK_NAME = "com.whatsapp";
    public static final int WHATSAPP_NOTIFICATION_CODE = 0;
    public static final int OTHER_NOTIFICATIONS_CODE = 1;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        int notificationCode = matchNotificationCode(sbn);
        if (notificationCode != OTHER_NOTIFICATIONS_CODE) {
            Log.d("*1234*", sbn.getNotification().extras.toString() + " --- " + String.valueOf(sbn.getNotification().tickerText));
            Intent intent = new Intent(getString(R.string.notification_intent));
            intent.putExtra(getString(R.string.notification_code), notificationCode);
            try {
                if (!sbn.getNotification().extras.getString("android.title").toLowerCase().equals("whatsapp")) {
                    sendIntent(sbn, intent);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startService(new Intent(WhatsappNotificationListener.this, FloatingBubbleService.class));
                        }
                    }).start();
                }
            } catch (Exception e) {
                sendIntent(sbn, intent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startService(new Intent(WhatsappNotificationListener.this, FloatingBubbleService.class));
                    }
                }).start();
            }
        }
    }

    private void sendIntent(StatusBarNotification sbn, Intent intent){
        if (sbn.getNotification().extras.getString("android.conversationTitle") != null)
            intent.putExtra(getString(R.string.notification_title), sbn.getNotification().extras.getString("android.conversationTitle"));
        else
            intent.putExtra(getString(R.string.notification_title), sbn.getNotification().extras.getString("android.title"));
        if (sbn.getNotification().extras.getString("android.text") != null)
            intent.putExtra(getString(R.string.notification_text), sbn.getNotification().extras.getString("android.text"));
        sendBroadcast(intent);
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if (packageName.equals(WHATSAPP_PACK_NAME)) {
            return (WHATSAPP_NOTIFICATION_CODE);
        } else {
            return (OTHER_NOTIFICATIONS_CODE);
        }
    }
}
