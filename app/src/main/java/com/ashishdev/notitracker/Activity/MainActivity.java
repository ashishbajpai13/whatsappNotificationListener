package com.ashishdev.notitracker.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishdev.notitracker.Adapter.NotificationsAdapter;
import com.ashishdev.notitracker.Async.NotificationDeleteAsync;
import com.ashishdev.notitracker.Database.Notification;
import com.ashishdev.notitracker.R;
import com.ashishdev.notitracker.Service.FloatingBubbleService;
import com.ashishdev.notitracker.ViewModel.NotificationViewModel;
import com.ashishdev.notitracker.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private androidx.appcompat.app.AlertDialog enableNotificationListenerAlertDialog;
    private NotificationViewModel notificationViewModel;
    private NotificationsAdapter notificationsAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 20;
    private static final int REQUEST_READ_CONTACTS = 79;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);

        //check if notifications is allowed by the user
        if (!isNotificationServiceEnabled()) {
            buildNotificationServiceAlertDialog
                    (getString(R.string.allow_notif_permission_text));
        }

        //check if overlay request is allowed by the user
        if (!isDrawOverLayPermissionGranted()) {
            buildNotificationServiceAlertDialog
                    (getString(R.string.allow_overlay_text));
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestPermission();
        }

        // Finally we register a receiver to tell the MainActivity when a notification has been received
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.notification_intent));
        registerReceiver(notificationBroadcastReceiver, intentFilter);

        //setting the data
        notificationsAdapter = new NotificationsAdapter(this, notificationList, notificationViewModel);
        binding.notificationsRecycler.setAdapter(notificationsAdapter);
        notificationViewModel.getAllNotifications().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationsAdapter.notifyDataSetChanged();
            }
        });

        //Click listeners
        binding.clearTv.setOnClickListener(this);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_tv:
                new NotificationDeleteAsync(notificationViewModel,
                        true, null, null, null, 0, null)
                        .execute();
                break;
        }
    }

    public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String notificationTitle = intent.getStringExtra(getString(R.string.notification_title));
            String notificationText = intent.getStringExtra(getString(R.string.notification_text));
            if (notificationTitle != null || notificationText != null)
                notificationViewModel.createNotification(notificationTitle, notificationText);
        }
    }

    private boolean isDrawOverLayPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return false;
        }
        return true;
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void buildNotificationServiceAlertDialog(final String text) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView dialogText = dialogView.findViewById(R.id.top_tv);
        dialogText.setText(text);
        final TextView yesTv = dialogView.findViewById(R.id.yes_tv);
        final TextView cancelTv = dialogView.findViewById(R.id.cancel_tv);
        final androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();

        yesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.equals(getString(R.string.allow_notif_permission_text)))
                    startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                }
                alertDialog.dismiss();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(getString(R.string.add_permissions_required));
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationViewModel.clear();
    }
}
