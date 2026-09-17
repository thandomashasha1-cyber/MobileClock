package com.mobileclock.app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerReceiver extends BroadcastReceiver {

    public static final String ACTION_TIMER_FINISHED =
            "com.mobileclock.app.TIMER_FINISHED";

    private static final String CHANNEL_ID =
            "timer_channel";

    private static final int NOTIFICATION_ID =
            5002;

    private static final int TIMER_FINISHED_REQUEST_CODE =
            5003;

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        if (!ACTION_TIMER_FINISHED.equals(
                intent.getAction()
        )) {
            return;
        }

        // -----------------------------------------------------
        // CLEAR SAVED TIMER STATE
        // -----------------------------------------------------

        context.getSharedPreferences(
                        "mobile_clock_timer",
                        Context.MODE_PRIVATE
                )
                .edit()
                .clear()
                .apply();


        // -----------------------------------------------------
        // CREATE NOTIFICATION CHANNEL
        // -----------------------------------------------------

        createNotificationChannel(context);


        // -----------------------------------------------------
        // OPEN TIMER FINISHED ACTIVITY
        // -----------------------------------------------------

        Intent finishedIntent =
                new Intent(
                        context,
                        TimerFinishedActivity.class
                );

        finishedIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );


        PendingIntent finishedPendingIntent =
                PendingIntent.getActivity(
                        context,
                        TIMER_FINISHED_REQUEST_CODE,
                        finishedIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );


        // -----------------------------------------------------
        // BUILD NOTIFICATION
        // -----------------------------------------------------

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )

                        .setSmallIcon(
                                android.R.drawable.ic_lock_idle_alarm
                        )

                        .setContentTitle(
                                "Timer Finished"
                        )

                        .setContentText(
                                "Your countdown timer has finished."
                        )

                        .setPriority(
                                NotificationCompat.PRIORITY_MAX
                        )

                        .setCategory(
                                NotificationCompat.CATEGORY_ALARM
                        )

                        .setAutoCancel(true)

                        .setOngoing(false)

                        .setFullScreenIntent(
                                finishedPendingIntent,
                                true
                        )

                        .setContentIntent(
                                finishedPendingIntent
                        );


        // -----------------------------------------------------
        // SHOW NOTIFICATION
        // -----------------------------------------------------

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                // Notification permission has not been granted.
                // The scheduled timer itself has still completed.

                openFinishedActivity(context);

                return;
            }
        }


        NotificationManagerCompat
                .from(context)
                .notify(
                        NOTIFICATION_ID,
                        builder.build()
                );


        // -----------------------------------------------------
        // ALSO TRY TO OPEN FINISHED SCREEN
        // -----------------------------------------------------

        openFinishedActivity(context);
    }


    // =========================================================
    // OPEN FINISHED ACTIVITY
    // =========================================================

    private void openFinishedActivity(
            Context context
    ) {

        Intent finishedIntent =
                new Intent(
                        context,
                        TimerFinishedActivity.class
                );

        finishedIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );


        try {

            context.startActivity(
                    finishedIntent
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // NOTIFICATION CHANNEL
    // =========================================================

    private void createNotificationChannel(
            Context context
    ) {

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O) {

            return;
        }


        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );


        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Timer",
                        NotificationManager.IMPORTANCE_HIGH
                );


        channel.setDescription(
                "Notifications for finished countdown timers"
        );


        channel.setLockscreenVisibility(
                android.app.Notification.VISIBILITY_PUBLIC
        );


        notificationManager.createNotificationChannel(
                channel
        );
    }
}