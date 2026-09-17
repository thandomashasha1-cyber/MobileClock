package com.mobileclock.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {

    private static final int TIMER_REQUEST_CODE = 5001;

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        if (!Intent.ACTION_BOOT_COMPLETED.equals(
                intent.getAction()
        )) {

            return;
        }

        // =====================================================
        // RESTORE NORMAL ALARMS
        // =====================================================

        restoreAlarms(context);

        // =====================================================
        // RESTORE TIMER
        // =====================================================

        restoreTimer(context);
    }


    // =========================================================
    // RESTORE NORMAL ALARMS
    // =========================================================

    private void restoreAlarms(
            Context context
    ) {

        AlarmStorage storage =
                new AlarmStorage(context);

        AlarmScheduler scheduler =
                new AlarmScheduler(context);

        for (Alarm alarm : storage.getAlarms()) {

            if (alarm.isEnabled()) {

                scheduler.scheduleAlarm(
                        alarm
                );
            }
        }
    }


    // =========================================================
    // RESTORE TIMER
    // =========================================================

    private void restoreTimer(
            Context context
    ) {

        android.content.SharedPreferences preferences =
                context.getSharedPreferences(
                        "mobile_clock_timer",
                        Context.MODE_PRIVATE
                );

        boolean running =
                preferences.getBoolean(
                        "timer_running",
                        false
                );

        if (!running) {

            return;
        }


        long endTimeMillis =
                preferences.getLong(
                        "timer_end_time",
                        0
                );


        if (endTimeMillis <= 0) {

            clearTimer(context);

            return;
        }


        long now =
                System.currentTimeMillis();


        // =====================================================
        // TIMER ALREADY FINISHED DURING REBOOT
        // =====================================================

        if (endTimeMillis <= now) {

            clearTimer(context);

            showTimerFinished(context);

            return;
        }


        // =====================================================
        // CHECK EXACT ALARM PERMISSION
        // =====================================================

        AlarmManager alarmManager =
                (AlarmManager)
                        context.getSystemService(
                                Context.ALARM_SERVICE
                        );


        if (alarmManager == null) {

            return;
        }


        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {

                return;
            }
        }


        // =====================================================
        // CREATE TIMER INTENT
        // =====================================================

        Intent timerIntent =
                new Intent(
                        context,
                        TimerReceiver.class
                );

        timerIntent.setAction(
                TimerReceiver.ACTION_TIMER_FINISHED
        );


        PendingIntent timerPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        TIMER_REQUEST_CODE,
                        timerIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );


        // =====================================================
        // RESCHEDULE TIMER
        // =====================================================

        try {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    endTimeMillis,
                    timerPendingIntent
            );

        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // CLEAR TIMER
    // =========================================================

    private void clearTimer(
            Context context
    ) {

        context.getSharedPreferences(
                        "mobile_clock_timer",
                        Context.MODE_PRIVATE
                )
                .edit()
                .clear()
                .apply();
    }


    // =========================================================
    // TIMER FINISHED DURING REBOOT
    // =========================================================

    private void showTimerFinished(
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
}