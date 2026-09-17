package com.mobileclock.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID =
            "alarm_channel";

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        int alarmId =
                intent.getIntExtra(
                        "alarmId",
                        -1
                );

        int hour =
                intent.getIntExtra(
                        "hour",
                        0
                );

        int minute =
                intent.getIntExtra(
                        "minute",
                        0
                );

        String label =
                intent.getStringExtra(
                        "label"
                );

        boolean vibration =
                intent.getBooleanExtra(
                        "vibration",
                        true
                );

        String soundUri =
                intent.getStringExtra(
                        "soundUri"
                );

        boolean snoozeAlarm =
                intent.getBooleanExtra(
                        "snoozeAlarm",
                        false
                );

        createNotificationChannel(context);

        // =====================================================
        // START ALARM SOUND SERVICE
        // =====================================================

        Intent soundIntent =
                new Intent(
                        context,
                        AlarmSoundService.class
                );

        soundIntent.putExtra(
                "soundUri",
                soundUri
        );

        soundIntent.putExtra(
                "vibration",
                vibration
        );

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            context.startForegroundService(
                    soundIntent
            );

        } else {

            context.startService(
                    soundIntent
            );
        }

        // =====================================================
        // OPEN RINGING SCREEN
        // =====================================================

        Intent ringingIntent =
                new Intent(
                        context,
                        AlarmRingingActivity.class
                );

        ringingIntent.putExtra(
                "alarmId",
                alarmId
        );

        ringingIntent.putExtra(
                "hour",
                hour
        );

        ringingIntent.putExtra(
                "minute",
                minute
        );

        ringingIntent.putExtra(
                "label",
                label
        );

        ringingIntent.putExtra(
                "vibration",
                vibration
        );

        ringingIntent.putExtra(
                "soundUri",
                soundUri
        );

        ringingIntent.putExtra(
                "snoozeAlarm",
                snoozeAlarm
        );

        ringingIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent ringingPendingIntent =
                PendingIntent.getActivity(
                        context,
                        alarmId,
                        ringingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        // =====================================================
        // NOTIFICATION
        // =====================================================

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_lock_idle_alarm
                        )
                        .setContentTitle(
                                label == null ||
                                        label.trim().isEmpty()
                                        ? "Alarm"
                                        : label
                        )
                        .setContentText(
                                "Alarm is ringing"
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_MAX
                        )
                        .setCategory(
                                NotificationCompat.CATEGORY_ALARM
                        )
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setFullScreenIntent(
                                ringingPendingIntent,
                                true
                        );

        // =====================================================
        // SHOW NOTIFICATION
        // =====================================================

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.notify(
                    alarmId,
                    builder.build()
            );
        }

        // =====================================================
        // NORMAL REPEATING ALARM
        // =====================================================

        // IMPORTANT:
        //
        // Snooze alarms are temporary.
        // They must NOT be treated as the original
        // repeating alarm.
        //
        // Normal alarms remain controlled by
        // AlarmScheduler / AlarmStorage.

        if (!snoozeAlarm) {

            // The original alarm remains stored.
            // AlarmScheduler will determine its next
            // occurrence when required.
        }
    }

    // =========================================================
    // NOTIFICATION CHANNEL
    // =========================================================

    private void createNotificationChannel(
            Context context
    ) {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Alarms",
                            NotificationManager
                                    .IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Alarm notifications"
            );

            channel.setLockscreenVisibility(
                    android.app.Notification
                            .VISIBILITY_PUBLIC
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }
}