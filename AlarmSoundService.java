package com.mobileclock.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmSoundService extends Service {

    private static final String CHANNEL_ID =
            "alarm_sound_service";

    private static final int NOTIFICATION_ID =
            2001;

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {

        super.onCreate();

        createNotificationChannel();
    }

    // =========================================================
    // SERVICE START
    // =========================================================

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        // -----------------------------------------------------
        // STOP ALARM
        // -----------------------------------------------------

        if (intent != null &&
                "STOP_ALARM".equals(
                        intent.getAction()
                )) {

            stopAlarmSound();

            return START_NOT_STICKY;
        }

        // -----------------------------------------------------
        // START FOREGROUND SERVICE
        // -----------------------------------------------------

        startForeground(
                NOTIFICATION_ID,
                createNotification()
        );

        // -----------------------------------------------------
        // GET SELECTED SOUND
        // -----------------------------------------------------

        String soundUri =
                null;

        if (intent != null) {

            soundUri =
                    intent.getStringExtra(
                            "soundUri"
                    );
        }

        startAlarmSound(
                soundUri
        );

        return START_STICKY;
    }

    // =========================================================
    // START ALARM SOUND
    // =========================================================

    private void startAlarmSound(
            String soundUriString
    ) {

        // Already playing
        if (mediaPlayer != null) {

            return;
        }

        try {

            Uri alarmSound = null;

            // -------------------------------------------------
            // USE SELECTED SOUND
            // -------------------------------------------------

            if (soundUriString != null &&
                    !soundUriString.trim().isEmpty()) {

                try {

                    alarmSound =
                            Uri.parse(
                                    soundUriString
                            );

                } catch (Exception e) {

                    e.printStackTrace();

                    alarmSound = null;
                }
            }

            // -------------------------------------------------
            // FALLBACK TO DEFAULT ALARM SOUND
            // -------------------------------------------------

            if (alarmSound == null) {

                alarmSound =
                        RingtoneManager.getDefaultUri(
                                RingtoneManager.TYPE_ALARM
                        );
            }

            if (alarmSound == null) {

                alarmSound =
                        RingtoneManager.getDefaultUri(
                                RingtoneManager.TYPE_NOTIFICATION
                        );
            }

            // -------------------------------------------------
            // CREATE MEDIA PLAYER
            // -------------------------------------------------

            mediaPlayer =
                    MediaPlayer.create(
                            this,
                            alarmSound
                    );

            if (mediaPlayer != null) {

                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(
                                        AudioAttributes
                                                .USAGE_ALARM
                                )
                                .setContentType(
                                        AudioAttributes
                                                .CONTENT_TYPE_SONIFICATION
                                )
                                .build()
                );

                mediaPlayer.setLooping(
                        true
                );

                mediaPlayer.start();
            }

        } catch (Exception e) {

            e.printStackTrace();

            // Try default sound if selected sound fails

            try {

                Uri defaultSound =
                        RingtoneManager.getDefaultUri(
                                RingtoneManager.TYPE_ALARM
                        );

                mediaPlayer =
                        MediaPlayer.create(
                                this,
                                defaultSound
                        );

                if (mediaPlayer != null) {

                    mediaPlayer.setLooping(
                            true
                    );

                    mediaPlayer.start();
                }

            } catch (Exception fallbackError) {

                fallbackError.printStackTrace();
            }
        }
    }

    // =========================================================
    // SERVICE NOTIFICATION
    // =========================================================

    private Notification createNotification() {

        Intent stopIntent =
                new Intent(
                        this,
                        AlarmSoundService.class
                );

        stopIntent.setAction(
                "STOP_ALARM"
        );

        PendingIntent stopPendingIntent =
                PendingIntent.getService(
                        this,
                        3001,
                        stopIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                |
                                PendingIntent.FLAG_IMMUTABLE
                );

        return new NotificationCompat.Builder(
                this,
                CHANNEL_ID
        )
                .setSmallIcon(
                        android.R.drawable
                                .ic_lock_idle_alarm
                )
                .setContentTitle(
                        "Mobile Clock Alarm"
                )
                .setContentText(
                        "Alarm is ringing"
                )
                .setCategory(
                        NotificationCompat.CATEGORY_ALARM
                )
                .setPriority(
                        NotificationCompat.PRIORITY_MAX
                )
                .setOngoing(true)
                .addAction(
                        android.R.drawable.ic_media_pause,
                        "STOP",
                        stopPendingIntent
                )
                .build();
    }

    // =========================================================
    // CREATE SERVICE CHANNEL
    // =========================================================

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Alarm Sound Service",
                            NotificationManager
                                    .IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Keeps the Mobile Clock alarm running"
            );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }

    // =========================================================
    // STOP ALARM
    // =========================================================

    private void stopAlarmSound() {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.stop();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            mediaPlayer.release();

            mediaPlayer = null;
        }

        stopForeground(
                true
        );

        stopSelf();
    }

    // =========================================================
    // SERVICE DESTROYED
    // =========================================================

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.stop();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            mediaPlayer.release();

            mediaPlayer = null;
        }

        super.onDestroy();
    }

    // =========================================================
    // NOT A BOUND SERVICE
    // =========================================================

    @Nullable
    @Override
    public IBinder onBind(
            Intent intent
    ) {

        return null;
    }
}