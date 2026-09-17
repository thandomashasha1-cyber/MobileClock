package com.mobileclock.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 100;

    private TextView timeText;
    private TextView amPmText;
    private TextView dateText;

    private final Handler handler = new Handler();

    private final Runnable clockRunnable =
            new Runnable() {

                @Override
                public void run() {

                    updateClock();

                    handler.postDelayed(
                            this,
                            1000
                    );
                }
            };

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        // Find clock views
        timeText =
                findViewById(
                        R.id.timeText
                );

        amPmText =
                findViewById(
                        R.id.amPmText
                );

        dateText =
                findViewById(
                        R.id.dateText
                );

        // Find buttons
        Button alarmsButton =
                findViewById(
                        R.id.alarmsButton
                );

        Button stopwatchButton =
                findViewById(
                        R.id.stopwatchButton
                );

        Button timerButton =
                findViewById(
                        R.id.timerButton
                );

        Button worldClockButton =
                findViewById(
                        R.id.worldClockButton
                );

        Button settingsButton =
                findViewById(
                        R.id.settingsButton
                );

        // ALARMS
        alarmsButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            AlarmsActivity.class
                    );

            startActivity(intent);
        });

        // STOPWATCH
        stopwatchButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            StopwatchActivity.class
                    );

            startActivity(intent);
        });

        // TIMER
        timerButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            TimerActivity.class
                    );

            startActivity(intent);
        });

        // WORLD CLOCK
        worldClockButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            WorldClockActivity.class
                    );

            startActivity(intent);
        });

        // SETTINGS
        settingsButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

        // Ask notification permission
        requestNotificationPermission();
    }

    @Override
    protected void onResume() {

        super.onResume();

        /*
         * Start the clock when the activity
         * becomes visible.
         */
        handler.post(clockRunnable);
    }

    @Override
    protected void onPause() {

        super.onPause();

        /*
         * Stop updating the clock when the
         * activity is no longer visible.
         */
        handler.removeCallbacks(
                clockRunnable
        );
    }

    private void updateClock() {

        /*
         * Read the user's selected time format.
         */
        SharedPreferences preferences =
                getSharedPreferences(
                        "mobile_clock_settings",
                        MODE_PRIVATE
                );

        String selectedFormat =
                preferences.getString(
                        "time_format",
                        "12"
                );

        Date now =
                new Date();

        /*
         * 12-hour format:
         * 10:35
         *
         * 24-hour format:
         * 22:35
         */
        String timePattern;

        if (selectedFormat.equals("24")) {

            timePattern = "HH:mm";

        } else {

            timePattern = "hh:mm";
        }

        SimpleDateFormat timeFormat =
                new SimpleDateFormat(
                        timePattern,
                        Locale.getDefault()
                );

        SimpleDateFormat amPmFormat =
                new SimpleDateFormat(
                        "a",
                        Locale.getDefault()
                );

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale.getDefault()
                );

        /*
         * Update main time.
         */
        timeText.setText(
                timeFormat.format(now)
        );

        /*
         * Hide AM/PM when using 24-hour format.
         */
        if (selectedFormat.equals("24")) {

            amPmText.setText("");

        } else {

            amPmText.setText(
                    amPmFormat.format(now)
            );
        }

        /*
         * Update date.
         */
        dateText.setText(
                dateFormat.format(now)
        );
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST
                );
            }
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(
                clockRunnable
        );

        super.onDestroy();
    }
}