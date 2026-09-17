package com.mobileclock.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AlarmRingingActivity extends AppCompatActivity {

    private TextView alarmTimeText;
    private TextView alarmLabelText;

    private Button snoozeButton;
    private Button stopButton;

    private int alarmId;
    private int hour;
    private int minute;

    private String label;
    private boolean vibration;
    private String soundUri;

    private boolean stopped = false;

    private static final String STOP_ALARM =
            "STOP_ALARM";

    private static final int SNOOZE_REQUEST_CODE = 6001;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_alarm_ringing
        );

        // Keep screen awake
        getWindow().addFlags(
                android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        alarmTimeText =
                findViewById(
                        R.id.alarmTimeText
                );

        alarmLabelText =
                findViewById(
                        R.id.alarmLabelText
                );

        snoozeButton =
                findViewById(
                        R.id.snoozeButton
                );

        stopButton =
                findViewById(
                        R.id.stopButton
                );

        // Receive alarm information
        Intent intent = getIntent();

        alarmId =
                intent.getIntExtra(
                        "alarmId",
                        -1
                );

        hour =
                intent.getIntExtra(
                        "hour",
                        0
                );

        minute =
                intent.getIntExtra(
                        "minute",
                        0
                );

        label =
                intent.getStringExtra(
                        "label"
                );

        vibration =
                intent.getBooleanExtra(
                        "vibration",
                        true
                );

        soundUri =
                intent.getStringExtra(
                        "soundUri"
                );

        displayAlarm();

        updateSnoozeButton();

        snoozeButton.setOnClickListener(
                v -> snoozeAlarm()
        );

        stopButton.setOnClickListener(
                v -> stopAlarm()
        );
    }

    // =========================================================
    // DISPLAY ALARM
    // =========================================================

    private void displayAlarm() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "mobile_clock_settings",
                        MODE_PRIVATE
                );

        String timeFormat =
                preferences.getString(
                        "time_format",
                        "12"
                );

        String formattedTime;

        if ("24".equals(timeFormat)) {

            formattedTime =
                    String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            hour,
                            minute
                    );

        } else {

            int displayHour = hour;

            String amPm = "AM";

            if (displayHour >= 12) {
                amPm = "PM";
            }

            if (displayHour == 0) {
                displayHour = 12;

            } else if (displayHour > 12) {
                displayHour -= 12;
            }

            formattedTime =
                    String.format(
                            Locale.getDefault(),
                            "%02d:%02d %s",
                            displayHour,
                            minute,
                            amPm
                    );
        }

        alarmTimeText.setText(
                formattedTime
        );

        if (label == null ||
                label.trim().isEmpty()) {

            alarmLabelText.setText(
                    "Alarm"
            );

        } else {

            alarmLabelText.setText(
                    label
            );
        }
    }

    // =========================================================
    // SNOOZE BUTTON TEXT
    // =========================================================

    private void updateSnoozeButton() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "mobile_clock_settings",
                        MODE_PRIVATE
                );

        int snoozeMinutes =
                preferences.getInt(
                        "snooze_duration",
                        5
                );

        String text;

        if (snoozeMinutes == 1) {

            text = "SNOOZE 1 MINUTE";

        } else {

            text =
                    "SNOOZE "
                            + snoozeMinutes
                            + " MINUTES";
        }

        snoozeButton.setText(
                text
        );
    }

    // =========================================================
    // SNOOZE
    // =========================================================

    private void snoozeAlarm() {

        if (stopped) {
            return;
        }

        stopped = true;

        // Stop current alarm sound
        stopAlarmSound();

        // Remove current alarm notification
        cancelAlarmNotification();

        // Read user's selected snooze duration
        SharedPreferences preferences =
                getSharedPreferences(
                        "mobile_clock_settings",
                        MODE_PRIVATE
                );

        int snoozeMinutes =
                preferences.getInt(
                        "snooze_duration",
                        5
                );

        long snoozeMillis =
                snoozeMinutes
                        * 60L
                        * 1000L;

        long snoozeTime =
                System.currentTimeMillis()
                        + snoozeMillis;

        // Create a temporary snooze alarm
        Alarm snoozeAlarm =
                new Alarm(
                        createSnoozeId(),
                        hour,
                        minute,
                        label,
                        true,
                        vibration,
                        soundUri
                );

        // Schedule using a dedicated snooze time
        scheduleSnooze(
                snoozeAlarm,
                snoozeTime
        );

        finish();
    }

    // =========================================================
    // CREATE SNOOZE ID
    // =========================================================

    private int createSnoozeId() {

        return (int)
                (System.currentTimeMillis()
                        & 0x7FFFFFFF);
    }

    // =========================================================
    // SCHEDULE SNOOZE
    // =========================================================

    private void scheduleSnooze(
            Alarm alarm,
            long triggerTime
    ) {

        AlarmManager alarmManager =
                (AlarmManager)
                        getSystemService(
                                Context.ALARM_SERVICE
                        );

        Intent intent =
                new Intent(
                        this,
                        AlarmReceiver.class
                );

        intent.putExtra(
                "alarmId",
                alarm.getId()
        );

        intent.putExtra(
                "hour",
                alarm.getHour()
        );

        intent.putExtra(
                "minute",
                alarm.getMinute()
        );

        intent.putExtra(
                "label",
                alarm.getLabel()
        );

        intent.putExtra(
                "vibration",
                alarm.isVibration()
        );

        intent.putExtra(
                "soundUri",
                alarm.getSoundUri()
        );

        intent.putExtra(
                "snoozeAlarm",
                true
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        alarm.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        try {

            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.S) {

                if (!alarmManager.canScheduleExactAlarms()) {
                    return;
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );

        } catch (SecurityException ignored) {
        }
    }

    // =========================================================
    // STOP ALARM
    // =========================================================

    private void stopAlarm() {

        if (stopped) {
            return;
        }

        stopped = true;

        stopAlarmSound();

        cancelAlarmNotification();

        finish();
    }

    // =========================================================
    // STOP ALARM SOUND SERVICE
    // =========================================================

    private void stopAlarmSound() {

        Intent intent =
                new Intent(
                        this,
                        AlarmSoundService.class
                );

        intent.setAction(
                STOP_ALARM
        );

        try {

            startService(intent);

        } catch (Exception ignored) {
        }
    }

    // =========================================================
    // CANCEL NOTIFICATION
    // =========================================================

    private void cancelAlarmNotification() {

        NotificationManager manager =
                (NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        if (manager != null &&
                alarmId != -1) {

            manager.cancel(
                    alarmId
            );
        }
    }

    // =========================================================
    // CLEAN UP
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}