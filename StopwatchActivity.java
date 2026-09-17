package com.mobileclock.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {

    private TextView stopwatchTimeText;
    private TextView lapTimeText;

    private Button startPauseButton;
    private Button lapButton;
    private Button resetButton;
    private Button backButton;

    private final Handler handler = new Handler();

    private long elapsedTime = 0;
    private long startTime = 0;

    private boolean running = false;

    private final ArrayList<Long> lapTimes =
            new ArrayList<>();

    private long previousLapTime = 0;

    private static final String PREFS_NAME =
            "mobile_clock_stopwatch";

    private static final String KEY_RUNNING =
            "running";

    private static final String KEY_ELAPSED =
            "elapsed_time";

    private static final String KEY_START_TIME =
            "start_time";

    private static final String KEY_PREVIOUS_LAP =
            "previous_lap_time";

    private final Runnable stopwatchRunnable =
            new Runnable() {

                @Override
                public void run() {

                    if (running) {

                        elapsedTime =
                                System.currentTimeMillis()
                                        - startTime;

                        updateDisplay();

                        handler.postDelayed(
                                this,
                                10
                        );
                    }
                }
            };

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_stopwatch
        );

        stopwatchTimeText =
                findViewById(
                        R.id.stopwatchTimeText
                );

        lapTimeText =
                findViewById(
                        R.id.lapTimeText
                );

        startPauseButton =
                findViewById(
                        R.id.startPauseButton
                );

        lapButton =
                findViewById(
                        R.id.lapButton
                );

        resetButton =
                findViewById(
                        R.id.resetButton
                );

        backButton =
                findViewById(
                        R.id.backButton
                );

        startPauseButton.setOnClickListener(
                v -> toggleStopwatch()
        );

        lapButton.setOnClickListener(
                v -> recordLap()
        );

        resetButton.setOnClickListener(
                v -> resetStopwatch()
        );

        backButton.setOnClickListener(
                v -> finish()
        );

        restoreStopwatch();

        updateDisplay();
        updateLapDisplay();
        updateButton();
    }

    // =========================================================
    // START / PAUSE / RESUME
    // =========================================================

    private void toggleStopwatch() {

        if (!running) {

            startTime =
                    System.currentTimeMillis()
                            - elapsedTime;

            running = true;

            saveStopwatch();

            updateButton();

            handler.post(
                    stopwatchRunnable
            );

        } else {

            elapsedTime =
                    System.currentTimeMillis()
                            - startTime;

            running = false;

            handler.removeCallbacks(
                    stopwatchRunnable
            );

            saveStopwatch();

            updateDisplay();
            updateButton();
        }
    }

    // =========================================================
    // LAP
    // =========================================================

    private void recordLap() {

        if (elapsedTime == 0) {

            lapTimeText.setText(
                    "Start the stopwatch first"
            );

            return;
        }

        if (running) {

            elapsedTime =
                    System.currentTimeMillis()
                            - startTime;
        }

        long splitTime =
                elapsedTime
                        - previousLapTime;

        lapTimes.add(
                splitTime
        );

        previousLapTime =
                elapsedTime;

        updateDisplay();
        updateLapDisplay();
    }

    // =========================================================
    // LAP DISPLAY
    // =========================================================

    private void updateLapDisplay() {

        if (lapTimes.isEmpty()) {

            lapTimeText.setText(
                    "Lap: --:--:---"
            );

            return;
        }

        StringBuilder history =
                new StringBuilder();

        for (int i = 0;
             i < lapTimes.size();
             i++) {

            history.append(
                    String.format(
                            Locale.getDefault(),
                            "LAP %02d     %s",
                            i + 1,
                            formatTime(
                                    lapTimes.get(i)
                            )
                    )
            );

            if (i <
                    lapTimes.size() - 1) {

                history.append("\n");
            }
        }

        lapTimeText.setText(
                history.toString()
        );
    }

    // =========================================================
    // RESET
    // =========================================================

    private void resetStopwatch() {

        running = false;

        elapsedTime = 0;

        startTime = 0;

        previousLapTime = 0;

        lapTimes.clear();

        handler.removeCallbacks(
                stopwatchRunnable
        );

        clearStopwatch();

        updateDisplay();
        updateLapDisplay();
        updateButton();
    }

    // =========================================================
    // BUTTON
    // =========================================================

    private void updateButton() {

        if (running) {

            startPauseButton.setText(
                    "PAUSE"
            );

        } else if (elapsedTime > 0) {

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
    // DISPLAY
    // =========================================================

    private void updateDisplay() {

        if (running) {

            elapsedTime =
                    System.currentTimeMillis()
                            - startTime;
        }

        stopwatchTimeText.setText(
                formatTime(
                        elapsedTime
                )
        );
    }

    // =========================================================
    // FORMAT
    // =========================================================

    private String formatTime(
            long milliseconds
    ) {

        long minutes =
                milliseconds / 60000;

        long seconds =
                (milliseconds / 1000)
                        % 60;

        long millis =
                milliseconds % 1000;

        return String.format(
                Locale.getDefault(),
                "%02d:%02d:%03d",
                minutes,
                seconds,
                millis
        );
    }

    // =========================================================
    // SAVE
    // =========================================================

    private void saveStopwatch() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        SharedPreferences.Editor editor =
                preferences.edit();

        editor.putBoolean(
                KEY_RUNNING,
                running
        );

        editor.putLong(
                KEY_ELAPSED,
                elapsedTime
        );

        editor.putLong(
                KEY_START_TIME,
                startTime
        );

        editor.putLong(
                KEY_PREVIOUS_LAP,
                previousLapTime
        );

        editor.apply();
    }

    // =========================================================
    // RESTORE
    // =========================================================

    private void restoreStopwatch() {

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

        elapsedTime =
                preferences.getLong(
                        KEY_ELAPSED,
                        0
                );

        startTime =
                preferences.getLong(
                        KEY_START_TIME,
                        0
                );

        previousLapTime =
                preferences.getLong(
                        KEY_PREVIOUS_LAP,
                        0
                );

        if (running) {

            long currentTime =
                    System.currentTimeMillis();

            elapsedTime =
                    currentTime
                            - startTime;

            if (elapsedTime < 0) {

                elapsedTime = 0;

                startTime =
                        currentTime;
            }

            handler.post(
                    stopwatchRunnable
            );
        }
    }

    // =========================================================
    // CLEAR
    // =========================================================

    private void clearStopwatch() {

        getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        )
                .edit()
                .clear()
                .apply();
    }

    // =========================================================
    // PAUSE ACTIVITY
    // =========================================================

    @Override
    protected void onPause() {

        super.onPause();

        if (running) {

            elapsedTime =
                    System.currentTimeMillis()
                            - startTime;
        }

        saveStopwatch();
    }

    // =========================================================
    // RESUME ACTIVITY
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (running) {

            elapsedTime =
                    System.currentTimeMillis()
                            - startTime;

            updateDisplay();

            handler.removeCallbacks(
                    stopwatchRunnable
            );

            handler.post(
                    stopwatchRunnable
            );
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(
                stopwatchRunnable
        );

        super.onDestroy();
    }
}