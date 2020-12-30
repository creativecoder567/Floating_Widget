package com.jsb.youtubetest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class FloatingWindow extends Service {

    WindowManager wm;
    LinearLayout ll;
    int LAYOUT_FLAG;
    View mFloatingView;
    ImageView imageClose;
    double width, height;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.window, null);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        ll = new LinearLayout(this);
        ll.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(layoutParams);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_close);
        imageClose.setVisibility(View.INVISIBLE);

//        wm.addView(mFloatingView,layoutParams);
//        mFloatingView.setVisibility(View.VISIBLE);

        ImageView openapp = new ImageView(this);
        openapp.setImageResource(R.mipmap.ic_launcher_round);
        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
                150, 150);
        openapp.setLayoutParams(butnparams);

        ll.addView(openapp);
        wm.addView(ll, params);
        wm.addView(imageClose, imageParams);
        height = wm.getDefaultDisplay().getHeight();
        width = wm.getDefaultDisplay().getWidth();

        openapp.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatepar = params;
            double x;
            double y;
            double px;
            double py;
            long startClickTime;
            int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);

                        x = updatepar.x;
                        y = updatepar.y;

                        px = motionEvent.getRawX();
                        py = motionEvent.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                        updatepar.x = (int) (x + px - motionEvent.getRawX());
                        updatepar.y = (int) (y + motionEvent.getRawY() - y);
                        if (clickDuration > MAX_CLICK_DURATION) {
                            if (updatepar.x > (height * 0.6))
                                stopSelf();
                        }

                        break;

                    case MotionEvent.ACTION_MOVE:

                        updatepar.x = (int) (x + (motionEvent.getRawX() - px));
                        updatepar.y = (int) (y + (motionEvent.getRawY() - py));

                        wm.updateViewLayout(ll, updatepar);

                    default:
                        break;
                }

                return false;

            }
        });

        openapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(FloatingWindow.this, MainActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        if (ll!=null){
            wm.removeView(ll);
        }
        if (imageClose!=null){
            wm.removeView(imageClose);
        }
    }
}
