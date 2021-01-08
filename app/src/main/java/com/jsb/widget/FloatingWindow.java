package com.jsb.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class FloatingWindow extends Service {

    WindowManager wm;
    RelativeLayout window_root;
    LinearLayout window_header;
    int LAYOUT_FLAG;
    View mFloatingView;
    ImageView imageClose;
    double width, height;
    ImageView openapp;
    EditText content_text;
    ImageButton content_button;
    private boolean wasInFocus = true;
    WindowManager.LayoutParams layoutParams;

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

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int layout_width = (int) (metrics.widthPixels * 0.7f);
        int layout_height = (int) (metrics.heightPixels * 0.45f);

         layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
                , LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);

//         WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        layoutParams.gravity = Gravity.TOP | Gravity.END;
        layoutParams.x = 0;
        layoutParams.y = 100;

        final WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

       /* openapp = new ImageView(this);
        openapp.setImageResource(R.mipmap.ic_launcher_round);
        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        openapp.setLayoutParams(butnparams);*/


//        window_root.addView(openapp);
        wm.addView(imageClose, imageParams);
        wm.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);
        height = wm.getDefaultDisplay().getHeight();
        width = wm.getDefaultDisplay().getWidth();


        window_root.setOnTouchListener(new View.OnTouchListener() {
            //            WindowManager.LayoutParams updatepar = layoutParams;
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            long startClickTime;
            int MAX_CLICK_DURATION = 200;
            long startTime = System.currentTimeMillis();
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (System.currentTimeMillis() - startTime <= 300) {
                    return false;
                }
             /*   if (isViewInBounds(mFloatingView, (int) (motionEvent.getRawX()), (int) (motionEvent.getRawY()))) {
                    editTextReceiveFocus();
                } else {
                    editTextDontReceiveFocus();
                }*/

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
                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        if (clickDuration < MAX_CLICK_DURATION) {
                            if (openapp.getVisibility() == View.VISIBLE) {
                                openapp.setVisibility(View.GONE);
                                window_header.setVisibility(View.VISIBLE);
                                editTextReceiveFocus();
                            } else {
                                editTextDontReceiveFocus();
                                openapp.setVisibility(View.VISIBLE);
                                window_header.setVisibility(View.GONE);
                            }
                        } else {
                            if (layoutParams.y > (height * 0.6))
                                stopSelf();
                            MainActivity.started=false;
                        }

                        return true;

                    case MotionEvent.ACTION_MOVE:

                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        wm.updateViewLayout(mFloatingView, layoutParams);

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


/*        content_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, "Hello", Toast.LENGTH_SHORT).show();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
                wm.updateViewLayout(mFloatingView,layoutParams);
            }
        });*/

        return START_STICKY;
    }


    private void init() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.window, null);

        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_close_white);
        imageClose.setVisibility(View.INVISIBLE);

        window_root = mFloatingView.findViewById(R.id.window_root);

        window_header = mFloatingView.findViewById(R.id.window_header);
        openapp = mFloatingView.findViewById(R.id.openapp);
        content_text = mFloatingView.findViewById(R.id.content_text);
        content_button = mFloatingView.findViewById(R.id.content_button);
    }

    private boolean isViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private void editTextReceiveFocus() {
        if (!wasInFocus) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            wm.updateViewLayout(mFloatingView, layoutParams);
            wasInFocus = true;
        }
    }

    private void editTextDontReceiveFocus() {
        if (wasInFocus) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            wm.updateViewLayout(mFloatingView, layoutParams);
            wasInFocus = false;
            hideKeyboard(this, content_text);
        }
    }

    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        content_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, content_text.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        content_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
                wm.updateViewLayout(mFloatingView, layoutParams);
                wasInFocus = true;
                showSoftKeyboard(v);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        MainActivity.started=false;
        if (window_root != null) {
            wm.removeView(mFloatingView);
        }
        if (imageClose != null) {
            wm.removeView(imageClose);
        }
    }
}
