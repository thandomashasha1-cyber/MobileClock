package com.mobileclock.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    private EditText hoursInput;
    private EditText minutesInput;
    private EditText secondsInput;

    private TextView timerDisplay;

    private Button startPauseButton;
    private Button resetButton;
    private Button backButton;

    private AlarmManager alarmManager;
    private PendingIntent timerPendingIntent;

    private final Handler handler = new Handler();

    private long remainingMillis = 0;
    private long initialMillis = 0;
    private long endTimeMillis = 0;

    private boolean running = false;

    private static final int TIMER_REQUEST_CODE = 5001;

    private static final String PREFS_NAME =
            "mobile_clock_timer";

    private static final String KEY_RUNNING =
            "timer_running";

    private static final String KEY_END_TIME =
            "timer_end_time";

    private static final String KEY_REMAINING =
            "timer_remaining";

    private static final String KEY_INITIAL =
            "timer_initial";

    // =========================================================
    // LIVE TIMER UPDATE
    // =========================================================

    private final Runnable timerRunnable =
            new Runnable() {

                @Override
                public void run() {

                    if (!running) {
                        return;
                    }

                    updateRemainingTime();

                    if (running) {

                        handler.postDelayed(
                                this,
                                250
                        );
                    }
                }
            };

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_timer
        );

        hoursInput =
                findViewById(
                        R.id.hoursInput
                );

        minutesInput =
                findViewById(
                        R.id.minutesInput
                );

        secondsInput =
                findViewById(
                        R.id.secondsInput
                );

        timerDisplay =
                findViewById(
                        R.id.timerDisplay
                );

        startPauseButton =
                findViewById(
                        R.id.startPauseButton
                );

        resetButton =
                findViewById(
                        R.id.resetButton
                );

        backButton =
                findViewById(
                        R.id.backButton
                );

        alarmManager =
                (AlarmManager)
                        getSystemService(
                                ALARM_SERVICE
                        );

        timerPendingIntent =
                createTimerPendingIntent();

        startPauseButton.setOnClickListener(
                v -> toggleTimer()
        );

        resetButton.setOnClickListener(
                v -> resetTimer()
        );

        backButton.setOnClickListener(
                v -> finish()
        );

        restoreTimer();

        updateDisplay();
    }

    // =========================================================
    // CREATE TIMER PENDING INTENT
    // =========================================================

    private PendingIntent createTimerPendingIntent() {

        Intent intent =
                new Intent(
                        this,
                        TimerReceiver.class
                );

        intent.setAction(
                "com.mobileclock.app.TIMER_FINISHED"
        );

        return PendingIntent.getBroadcast(
                this,
                TIMER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );
    }

    // =========================================================
    // START / PAUSE
    // =========================================================

    private void toggleTimer() {

        if (running) {

            pauseTimer();

        } else {

            if (remainingMillis > 0) {

                resumeTimer();

            } else {

                startTimer();
            }
        }
    }

    // =========================================================
    // START TIMER
    // =========================================================

    private void startTimer() {

        long duration =
                getInputDuration();

        if (duration <= 0) {

            Toast.makeText(
                    this,
                    "Enter a timer duration",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        initialMillis = duration;

        remainingMillis = duration;

        endTimeMillis =
                System.currentTimeMillis()
                        + duration;

        running = true;

        scheduleTimerAlarm();

        saveTimerState();

        updateButtons();

        startLiveUpdates();
    }

    // =========================================================
    // RESUME TIMER
    // =========================================================

    private void resumeTimer() {

        if (remainingMillis <= 0) {

            startTimer();

            return;
        }

        endTimeMillis =
                System.currentTimeMillis()
                        + remainingMillis;

        running = true;

        scheduleTimerAlarm();

        saveTimerState();

        updateButtons();

        startLiveUpdates();
    }

    // =========================================================
    // PAUSE TIMER
    // =========================================================

    private void pauseTimer() {

        updateRemainingTime();

        running = false;

        cancelTimerAlarm();

        handler.removeCallbacks(
                timerRunnable
        );

        saveTimerState();

        updateButtons();

        updateDisplay();
    }

    // =========================================================
    // RESET TIMER
    // =========================================================

    private void resetTimer() {

        running = false;

        remainingMillis = 0;

        initialMillis = 0;

        endTimeMillis = 0;

        cancelTimerAlarm();

        handler.removeCallbacks(
                timerRunnable
        );

        clearTimerState();

        hoursInput.setText("");
        minutesInput.setText("");
        secondsInput.setText("");

        updateButtons();

        updateDisplay();
    }

    // =========================================================
    // LIVE UPDATES
    // =========================================================

    private void startLiveUpdates() {

        handler.removeCallbacks(
                timerRunnable
        );

        handler.post(
                timerRunnable
        );
    }

    private void updateRemainingTime() {

        if (!running) {
            return;
        }

        remainingMillis =
                endTimeMillis
                        - System.currentTimeMillis();

        if (remainingMillis <= 0) {

            remainingMillis = 0;

            running = false;

            handler.removeCallbacks(
                    timerRunnable
            );

            clearTimerState();

            updateDisplay();

            updateButtons();

            return;
        }

        updateDisplay();
    }

    // =========================================================
    // DISPLAY
    // =========================================================

    private void updateDisplay() {

        long totalSeconds =
                remainingMillis / 1000;

        long hours =
                totalSeconds / 3600;

        long minutes =
                (totalSeconds % 3600)
                        / 60;

        long seconds =
                totalSeconds % 60;

        String time =
                String.format(
                        java.util.Locale.getDefault(),
                        "%02d:%02d:%02d",
                        hours,
                        minutes,
                        seconds
                );

        timerDisplay.setText(time);
    }

    // =========================================================
    // BUTTON STATE
    // =========================================================

    private void updateButtons() {

        if (running) {

            startPauseButton.setText(
                    "PAUSE"
            );

        } else if (remainingMillis > 0) {

            startPauseButton.setText(
                    "RESUME"
            );

        } else {

            startPauseButton.setText(
                    "START"
            );
        }
    }

    // =========================================================
    // READ INPUT
    // =========================================================

    private long getInputDuration() {

        int hours = parseInput(
                hoursInput.getText().toString()
        );

        int minutes = parseInput(
                minutesInput.getText().toString()
        );

        int seconds = parseInput(
                secondsInput.getText().toString()
        );

        return (
                hours * 60L * 60L * 1000L
        )
                + (
                minutes * 60L * 1000L
        )
                + (
                seconds * 1000L
        );
    }

    private int parseInput(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return 0;
        }

        try {

            return Integer.parseInt(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    // =========================================================
    // ALARM MANAGER
    // =========================================================

    private void scheduleTimerAlarm() {

        if (endTimeMillis <= 0) {
            return;
        }

        try {

            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.S) {

                if (!alarmManager.canScheduleExactAlarms()) {

                    Toast.makeText(
                            this,
                            "Allow exact alarms in Settings",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent =
                            new Intent(
                                    android.provider.Settings
                                            .ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            );

                    startActivity(intent);

                    return;
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    endTimeMillis,
                    timerPendingIntent
            );

        } catch (SecurityException e) {

            Toast.makeText(
                    this,
                    "Exact alarm permission is required",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // CANCEL ALARM
    // =========================================================

    private void cancelTimerAlarm() {

        if (alarmManager != null &&
                timerPendingIntent != null) {

            alarmManager.cancel(
                    timerPendingIntent
            );
        }
    }

    // =========================================================
    // SAVE STATE
    // =========================================================

    private void saveTimerState() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        preferences.edit()
                .putBoolean(
                        KEY_RUNNING,
                        running
                )
                .putLong(
                        KEY_END_TIME,
                        endTimeMillis
                )
                .putLong(
                        KEY_REMAINING,
                        remainingMillis
                )
                .putLong(
                        KEY_INITIAL,
                        initialMillis
                )
                .apply();
    }

    // =========================================================
    // RESTORE STATE
    // =========================================================

    private void restoreTimer() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        running =
                preferences.getBoolean(
                        KEY_RUNNING,
                        false
                );

        endTimeMillis =
                preferences.getLong(
                        KEY_END_TIME,
                        0
                );

        remainingMillis =
                preferences.getLong(
                        KEY_REMAINING,
                        0
                );

        initialMillis =
                preferences.getLong(
                        KEY_INITIAL,
                        0
                );

        if (running) {

            long currentTime =
                    System.currentTimeMillis();

            if (endTimeMillis <= currentTime) {

                running = false;

                remainingMillis = 0;

                endTimeMillis = 0;

                clearTimerState();

            } else {

                remainingMillis =
                        endTimeMillis
                                - currentTime;

                scheduleTimerAlarm();

                startLiveUpdates();
            }
        }

        updateButtons();
        updateDisplay();
    }

    // =========================================================
    // CLEAR STATE
    // =========================================================

    private void clearTimerState() {

        getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        )
                .edit()
                .clear()
                .apply();
    }

    // =========================================================
    // RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (running) {

            updateRemainingTime();

            startLiveUpdates();
        }
    }

    // =========================================================
    // PAUSE ACTIVITY
    // =========================================================

    @Override
    protected void onPause() {

        super.onPause();

        if (running) {

            updateRemainingTime();

            saveTimerState();
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(
                timerRunnable
        );

        super.onDestroy();
    }
}