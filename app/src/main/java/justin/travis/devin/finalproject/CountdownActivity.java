package justin.travis.devin.finalproject;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CountdownActivity extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private TextView textView;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                Log.d("buttonClick", "UI toggle ");
            }
        });

        //------[Power button]------------------------------------------------------------------------------
        android.widget.Button cancel_button = findViewById(R.id.countdown_power_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("buttonClick", "Exit Button Clicked");
                Log.d("buildInfo", "" + Build.VERSION.SDK_INT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("buildInfo", "build check passed");
                    if (mNotificationManager != null && mNotificationManager.isNotificationPolicyAccessGranted()) {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        Log.d("notificationManager", "Notifications unmuted");
                    }
                } else if (audio != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !audio.isVolumeFixed()) {
                    //mute audio

                    int notifications = prefs.getInt("notifications", 0);
                    int alarm = prefs.getInt("alarm", 0);
                    //                        int music = prefs.getInt("music",0);
                    int ring = prefs.getInt("ring", 0);
                    int system = prefs.getInt("system", 0);

                    Log.d("audioManager", "Notifications Shared Volume: " + notifications);
                    Log.d("audioManager", "Alarm Shared Volume: " + alarm);
                    //                        Log.d("audioManager", "Music Shared Volume: " + music);
                    Log.d("audioManager", "Ring Shared Volume: " + ring);
                    Log.d("audioManager", "System Shared Volume: " + system);

                    audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notifications, 0);
                    audio.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
                    //                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, music, 0);
                    audio.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
                    audio.setStreamVolume(AudioManager.STREAM_SYSTEM, system, 0);

                    Log.d("audioManager", "Notifications Volume: " + audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                    Log.d("audioManager", "Alarm Volume: " + audio.getStreamVolume(AudioManager.STREAM_ALARM));
                    //                        Log.d("audioManager", "Music Volume: " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                    Log.d("audioManager", "Ring Volume: " + audio.getStreamVolume(AudioManager.STREAM_RING));
                    Log.d("audioManager", "System Volume: " + audio.getStreamVolume(AudioManager.STREAM_SYSTEM));

                    Log.d("audioManager", "All audio unmuted");

                }
//                MyCountdownTimer.finish();
                finish();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.countdown_power_button).setOnTouchListener(mDelayHideTouchListener);

        //initialize the ui component
        textView = findViewById(R.id.fullscreen_content);

        //get the text from intent
        int hoursTimer = getIntent().getIntExtra("hours", 0);
        int minutesTimer = getIntent().getIntExtra("minutes", 0);
        Toast.makeText(this, hoursTimer + " hours\n" + minutesTimer + " minutes", Toast.LENGTH_SHORT).show();

        //convert the string into integer
        int timeSeconds = (minutesTimer + (hoursTimer * 60)) * 60;
//        int timeSeconds = 10;

        //Initialize a CountDownTimer class with the time data from previous activity
        //which will set the text view with countDown time
        CountDownTimer MyCountdownTimer = new CountDownTimer(timeSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //set the remaining time in the textView
                String temp = (millisUntilFinished / 1000) / 3600 % 24 + ":" + (millisUntilFinished / 1000) / 60 % 60 + ":" + (millisUntilFinished / 1000) % 60;
                Log.d("timer", temp + "");
                textView.setText(temp);
            }

            public void onFinish() {
                Log.d("timer", "Timer done");
                textView.setText(R.string.done);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Log.d("buildInfo", "" + Build.VERSION.SDK_INT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("buildInfo", "build check passed");
                    if (mNotificationManager != null && mNotificationManager.isNotificationPolicyAccessGranted()) {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        Log.d("notificationManager", "Notifications unmuted");
                    }
                } else if (audio != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !audio.isVolumeFixed()) {
                    //mute audio

                    int notifications = prefs.getInt("notifications", 0);
                    int alarm = prefs.getInt("alarm", 0);
                    int music = prefs.getInt("music", 0);
                    int ring = prefs.getInt("ring", 0);
                    int system = prefs.getInt("system", 0);

                    Log.d("audioManager", "Notifications Shared Volume: " + notifications);
                    Log.d("audioManager", "Alarm Shared Volume: " + alarm);
                    Log.d("audioManager", "Music Shared Volume: " + music);
                    Log.d("audioManager", "Ring Shared Volume: " + ring);
                    Log.d("audioManager", "System Shared Volume: " + system);

                    audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notifications, 0);
                    audio.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, music, 0);
                    audio.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
                    audio.setStreamVolume(AudioManager.STREAM_SYSTEM, system, 0);

                    Log.d("audioManager", "Notifications Volume: " + audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                    Log.d("audioManager", "Alarm Volume: " + audio.getStreamVolume(AudioManager.STREAM_ALARM));
                    Log.d("audioManager", "Music Volume: " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                    Log.d("audioManager", "Ring Volume: " + audio.getStreamVolume(AudioManager.STREAM_RING));
                    Log.d("audioManager", "System Volume: " + audio.getStreamVolume(AudioManager.STREAM_SYSTEM));

                    Log.d("audioManager", "All audio unmuted");

                }
                MediaPlayer ring = MediaPlayer.create(CountdownActivity.this, R.raw.ring);
                ring.start();
                Log.d("buttonClick", "That happened and we all let it happen");


                finish();
            }
        }.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "YOU SHALL NEVER LEAVE!\n THIS IS MY DOMAIN!", Toast.LENGTH_SHORT).show();
        Log.d("buttonClick", "Back Button pressed");
    }
}
