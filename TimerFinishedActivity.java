package com.mobileclock.app;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.VibratorManager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;

public class TimerFinishedActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private Vibrator vibrator;

    private TextView titleText;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer_finished);

        titleText = findViewById(
                R.id.timerFinishedTitle
        );

        stopButton = findViewById(
                R.id.stopTimerButton
        );

        stopButton.setOnClickListener(
                v -> stopTimer()
        );

        // Modern Android back-button handling
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        stopTimer();
                    }
                }
        );

        startAlarmSound();

        startVibration();
    }

    // =========================================================
    // ALARM SOUND
    // =========================================================

    private void startAlarmSound() {

        try {

            Uri alarmUri =
                    RingtoneManager.getDefaultUri(
                            RingtoneManager.TYPE_ALARM
                    );

            if (alarmUri == null) {

                alarmUri =
                        RingtoneManager.getDefaultUri(
                                RingtoneManager.TYPE_NOTIFICATION
                        );
            }

            ringtone =
                    RingtoneManager.getRingtone(
                            this,
                            alarmUri
                    );

            if (ringtone != null) {
                ringtone.play();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // VIBRATION
    // =========================================================

    private void startVibration() {

        try {

            if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S) {

                VibratorManager manager =
                        (VibratorManager)
                                getSystemService(
                                        VIBRATOR_MANAGER_SERVICE
                                );

                if (manager != null) {

                    vibrator =
                            manager.getDefaultVibrator();
                }

            } else {

                vibrator =
                        (Vibrator)
                                getSystemService(
                                        VIBRATOR_SERVICE
                                );
            }

            if (vibrator != null) {

                long[] pattern = {
                        0,
                        500,
                        300,
                        500,
                        300,
                        500
                };

                vibrator.vibrate(
                        pattern,
                        -1
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // STOP TIMER
    // =========================================================

    private void stopTimer() {

        stopSound();

        stopVibration();

        cancelNotification();

        finish();
    }

    // =========================================================
    // STOP SOUND
    // =========================================================

    private void stopSound() {

        if (ringtone != null &&
                ringtone.isPlaying()) {

            ringtone.stop();
        }
    }

    // =========================================================
    // STOP VIBRATION
    // =========================================================

    private void stopVibration() {

        if (vibrator != null) {

            vibrator.cancel();
        }
    }

    // =========================================================
    // CANCEL NOTIFICATION
    // =========================================================

    private void cancelNotification() {

        android.app.NotificationManager manager =
                (android.app.NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.cancel(5002);
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        stopSound();

        stopVibration();

        super.onDestroy();
    }
}