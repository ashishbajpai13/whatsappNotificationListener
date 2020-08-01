package com.ashishdev.notitracker.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ashishdev.notitracker.Activity.MainActivity;
import com.ashishdev.notitracker.R;

public class FloatingBubbleService extends Service implements View.OnClickListener, View.OnTouchListener {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private WindowManager.LayoutParams params;

    public FloatingBubbleService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_bubble_layout, null);
        //Add the view to the window.
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-right corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        try {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);

            //Initialize views and set Click listeners
            final View bubbleView = mFloatingView.findViewById(R.id.bubble_view);
            final ImageView closeBtn = mFloatingView.findViewById(R.id.close_btn);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFloatingView.findViewById(R.id.new_notif_alert_tv).setVisibility(View.GONE);
                }
            }, 1000);
            closeBtn.setOnClickListener(this);
            bubbleView.setOnTouchListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.close_btn:
                stopSelf();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //remember the initial position.
                initialX = params.x;
                initialY = params.y;

                //get the touch location
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_UP:
                int Xdiff = (int) (event.getRawX() - initialTouchX);
                int Ydiff = (int) (event.getRawY() - initialTouchY);


                //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                //So that is click event.
                if (Xdiff < 10 && Ydiff < 10) {
                    Intent intent = new Intent(FloatingBubbleService.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    stopSelf();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                //Calculate the X and Y coordinates of the view.
                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                params.y = initialY + (int) (event.getRawY() - initialTouchY);

                //Update the layout with new X & Y coordinate
                mWindowManager.updateViewLayout(mFloatingView, params);
                return true;
        }
        return false;
    }
}
