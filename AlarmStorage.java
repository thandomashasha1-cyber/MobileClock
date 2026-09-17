package com.mobileclock.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlarmStorage {

    private static final String PREF_NAME = "mobile_clock_alarms";
    private static final String KEY_ALARMS = "alarms";

    private final SharedPreferences preferences;

    public AlarmStorage(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // ---------------------------------------------------------
    // SAVE NEW ALARM
    // ---------------------------------------------------------

    public void saveAlarm(Alarm alarm) {

        List<Alarm> alarms = getAlarms();

        alarms.add(alarm);

        saveAll(alarms);
    }

    // ---------------------------------------------------------
    // GET ALL ALARMS
    // ---------------------------------------------------------

    public List<Alarm> getAlarms() {

        List<Alarm> alarms = new ArrayList<>();

        String json =
                preferences.getString(
                        KEY_ALARMS,
                        "[]"
                );

        try {

            JSONArray array =
                    new JSONArray(json);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                Alarm alarm =
                        new Alarm(
                                object.getInt("id"),
                                object.getInt("hour"),
                                object.getInt("minute"),
                                object.getString("label"),
                                object.getBoolean("enabled"),
                                object.getBoolean("vibration")
                        );

                // -------------------------------------------------
                // ALARM SOUND
                // -------------------------------------------------

                if (object.has("soundUri")) {

                    alarm.setSoundUri(
                            object.optString(
                                    "soundUri",
                                    ""
                            )
                    );
                }

                // -------------------------------------------------
                // REPEAT DAYS
                // -------------------------------------------------

                alarm.setMonday(
                        object.optBoolean(
                                "monday",
                                false
                        )
                );

                alarm.setTuesday(
                        object.optBoolean(
                                "tuesday",
                                false
                        )
                );

                alarm.setWednesday(
                        object.optBoolean(
                                "wednesday",
                                false
                        )
                );

                alarm.setThursday(
                        object.optBoolean(
                                "thursday",
                                false
                        )
                );

                alarm.setFriday(
                        object.optBoolean(
                                "friday",
                                false
                        )
                );

                alarm.setSaturday(
                        object.optBoolean(
                                "saturday",
                                false
                        )
                );

                alarm.setSunday(
                        object.optBoolean(
                                "sunday",
                                false
                        )
                );

                alarms.add(alarm);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return alarms;
    }

    // ---------------------------------------------------------
    // DELETE ALARM
    // ---------------------------------------------------------

    public void deleteAlarm(int alarmId) {

        List<Alarm> alarms =
                getAlarms();

        for (int i = alarms.size() - 1;
             i >= 0;
             i--) {

            if (alarms.get(i).getId() == alarmId) {

                alarms.remove(i);
            }
        }

        saveAll(alarms);
    }

    // ---------------------------------------------------------
    // UPDATE ALARM
    // ---------------------------------------------------------

    public void updateAlarm(
            Alarm updatedAlarm
    ) {

        List<Alarm> alarms =
                getAlarms();

        for (int i = 0;
             i < alarms.size();
             i++) {

            if (alarms.get(i).getId()
                    == updatedAlarm.getId()) {

                alarms.set(
                        i,
                        updatedAlarm
                );

                break;
            }
        }

        saveAll(alarms);
    }

    // ---------------------------------------------------------
    // SAVE EVERYTHING
    // ---------------------------------------------------------

    private void saveAll(
            List<Alarm> alarms
    ) {

        JSONArray array =
                new JSONArray();

        try {

            for (Alarm alarm : alarms) {

                JSONObject object =
                        new JSONObject();

                // -------------------------------------------------
                // BASIC ALARM INFORMATION
                // -------------------------------------------------

                object.put(
                        "id",
                        alarm.getId()
                );

                object.put(
                        "hour",
                        alarm.getHour()
                );

                object.put(
                        "minute",
                        alarm.getMinute()
                );

                object.put(
                        "label",
                        alarm.getLabel()
                );

                object.put(
                        "enabled",
                        alarm.isEnabled()
                );

                object.put(
                        "vibration",
                        alarm.isVibration()
                );

                // -------------------------------------------------
                // ALARM SOUND
                // -------------------------------------------------

                object.put(
                        "soundUri",
                        alarm.getSoundUri()
                );

                // -------------------------------------------------
                // REPEAT DAYS
                // -------------------------------------------------

                object.put(
                        "monday",
                        alarm.isMonday()
                );

                object.put(
                        "tuesday",
                        alarm.isTuesday()
                );

                object.put(
                        "wednesday",
                        alarm.isWednesday()
                );

                object.put(
                        "thursday",
                        alarm.isThursday()
                );

                object.put(
                        "friday",
                        alarm.isFriday()
                );

                object.put(
                        "saturday",
                        alarm.isSaturday()
                );

                object.put(
                        "sunday",
                        alarm.isSunday()
                );

                array.put(object);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        preferences
                .edit()
                .putString(
                        KEY_ALARMS,
                        array.toString()
                )
                .apply();
    }
}