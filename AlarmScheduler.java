package com.mobileclock.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class AlarmScheduler {

    private final Context context;
    private final AlarmManager alarmManager;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AlarmScheduler(Context context) {

        this.context =
                context.getApplicationContext();

        alarmManager =
                (AlarmManager)
                        this.context.getSystemService(
                                Context.ALARM_SERVICE
                        );
    }


    // =========================================================
    // SCHEDULE ALARM
    // =========================================================

    public void scheduleAlarm(
            Alarm alarm
    ) {

        if (alarm == null) {
            return;
        }

        if (!alarm.isEnabled()) {
            return;
        }

        if (alarmManager == null) {
            return;
        }


        // =====================================================
        // CHECK EXACT ALARM PERMISSION
        // =====================================================

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {
                return;
            }
        }


        // =====================================================
        // GET NEXT ALARM TIME
        // =====================================================

        Calendar alarmTime =
                getNextAlarmTime(alarm);

        if (alarmTime == null) {
            return;
        }


        // =====================================================
        // CREATE INTENT
        // =====================================================

        Intent intent =
                new Intent(
                        context,
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
                false
        );


        // =====================================================
        // CREATE PENDING INTENT
        // =====================================================

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        alarm.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );


        // =====================================================
        // SCHEDULE
        // =====================================================

        try {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(),
                    pendingIntent
            );

        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // ALIAS: schedule()
    // =========================================================
    // Keeps compatibility with AddAlarmActivity and other
    // existing classes.

    public void schedule(
            Alarm alarm
    ) {

        scheduleAlarm(
                alarm
        );
    }


    // =========================================================
    // CANCEL ALARM
    // =========================================================

    public void cancelAlarm(
            Alarm alarm
    ) {

        if (alarm == null) {
            return;
        }

        if (alarmManager == null) {
            return;
        }


        Intent intent =
                new Intent(
                        context,
                        AlarmReceiver.class
                );


        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        alarm.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );


        alarmManager.cancel(
                pendingIntent
        );

        pendingIntent.cancel();
    }


    // =========================================================
    // ALIAS: cancel()
    // =========================================================
    // Keeps compatibility with AddAlarmActivity and other
    // existing classes.

    public void cancel(
            Alarm alarm
    ) {

        cancelAlarm(
                alarm
        );
    }


    // =========================================================
    // GET NEXT ALARM TIME
    // =========================================================

    private Calendar getNextAlarmTime(
            Alarm alarm
    ) {

        Calendar now =
                Calendar.getInstance();

        Calendar alarmTime =
                Calendar.getInstance();


        // =====================================================
        // SET ALARM TIME
        // =====================================================

        alarmTime.set(
                Calendar.HOUR_OF_DAY,
                alarm.getHour()
        );

        alarmTime.set(
                Calendar.MINUTE,
                alarm.getMinute()
        );

        alarmTime.set(
                Calendar.SECOND,
                0
        );

        alarmTime.set(
                Calendar.MILLISECOND,
                0
        );


        // =====================================================
        // ONE-TIME ALARM
        // =====================================================

        if (!alarm.hasRepeatDays()) {

            if (!alarmTime.after(now)) {

                alarmTime.add(
                        Calendar.DAY_OF_YEAR,
                        1
                );
            }

            return alarmTime;
        }


        // =====================================================
        // REPEATING ALARM
        // =====================================================

        for (int i = 0; i < 7; i++) {

            Calendar candidate =
                    (Calendar)
                            alarmTime.clone();

            candidate.add(
                    Calendar.DAY_OF_YEAR,
                    i
            );


            int dayOfWeek =
                    candidate.get(
                            Calendar.DAY_OF_WEEK
                    );


            if (isDaySelected(
                    alarm,
                    dayOfWeek
            )) {

                if (candidate.after(now)) {

                    return candidate;
                }
            }
        }


        // =====================================================
        // FALLBACK
        // =====================================================

        return alarmTime;
    }


    // =========================================================
    // CHECK REPEAT DAY
    // =========================================================

    private boolean isDaySelected(
            Alarm alarm,
            int dayOfWeek
    ) {

        switch (dayOfWeek) {

            case Calendar.MONDAY:

                return alarm.isMonday();


            case Calendar.TUESDAY:

                return alarm.isTuesday();


            case Calendar.WEDNESDAY:

                return alarm.isWednesday();


            case Calendar.THURSDAY:

                return alarm.isThursday();


            case Calendar.FRIDAY:

                return alarm.isFriday();


            case Calendar.SATURDAY:

                return alarm.isSaturday();


            case Calendar.SUNDAY:

                return alarm.isSunday();


            default:

                return false;
        }
    }
}
