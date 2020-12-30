package com.jsb.widget;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class FloatingWindow extends Service {

    WindowManager wm;
    LinearLayout window_root;
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

        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT
        ,LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        layoutParams.gravity =Gravity.TOP|Gravity.RIGHT;
        layoutParams.x=0;
        layoutParams.y=100;

        final WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_close_white);
        imageClose.setVisibility(View.INVISIBLE);

        wm.addView(imageClose,imageParams);
        wm.addView(mFloatingView,layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);
        height = wm.getDefaultDisplay().getHeight();
        width = wm.getDefaultDisplay().getWidth();

        window_root = mFloatingView.findViewById(R.id.window_root);

        window_root.setOnTouchListener(new View.OnTouchListener() {
//            WindowManager.LayoutParams updatepar = layoutParams;
            int initialX, initialY;
            float initialTouchX,initialTouchY;
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

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        imageClose.setVisibility(View.GONE);
                        layoutParams.x = initialX + (int)(initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialY + (int)(motionEvent.getRawY() - initialTouchY);
                        if (clickDuration < MAX_CLICK_DURATION) {
                            Toast.makeText(FloatingWindow.this, "Hi", Toast.LENGTH_SHORT).show();
                        }else {
                            if (layoutParams.y > (height * 0.6))
                                stopSelf();
                        }

                        return  true;

                    case MotionEvent.ACTION_MOVE:

                        layoutParams.x = initialX+ (int)(initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialY+ (int)(motionEvent.getRawY()-initialTouchY);

                        wm.updateViewLayout(window_root, layoutParams);

                        if (layoutParams.y > (height * 0.6))
                            imageClose.setImageResource(R.drawable.ic_close_red);
                        else
                            imageClose.setImageResource(R.drawable.ic_close_white);

                        return true;

                    default:
                        break;
                }

                return false;

            }
        });

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();


      /*  window_root = new LinearLayout(this);
        window_root.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        window_root.setLayoutParams(layoutParams);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;*/

       /* WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;*/


//        wm.addView(mFloatingView,layoutParams);
//        mFloatingView.setVisibility(View.VISIBLE);

      /*  ImageView openapp = new ImageView(this);
        openapp.setImageResource(R.mipmap.ic_launcher_round);
        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
                150, 150);
        openapp.setLayoutParams(butnparams);*/
/*
        window_root.addView(openapp);
        wm.addView(window_root, params);
        wm.addView(imageClose, imageParams);*/




    /*    window_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(FloatingWindow.this, MainActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
            }
        });*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        if (window_root!=null){
            wm.removeView(window_root);
        }
        if (imageClose!=null){
            wm.removeView(imageClose);
        }
    }
}
