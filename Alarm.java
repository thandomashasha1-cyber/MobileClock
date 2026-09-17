package com.mobileclock.app;

public class Alarm {

    private int id;
    private int hour;
    private int minute;
    private String label;
    private boolean enabled;
    private boolean vibration;
    private String soundUri;

    // Days of the week
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;


    // =========================================================
    // 6-ARGUMENT CONSTRUCTOR
    // =========================================================

    public Alarm(
            int id,
            int hour,
            int minute,
            String label,
            boolean enabled,
            boolean vibration
    ) {

        this(
                id,
                hour,
                minute,
                label,
                enabled,
                vibration,
                ""
        );
    }


    // =========================================================
    // 7-ARGUMENT CONSTRUCTOR
    // =========================================================

    public Alarm(
            int id,
            int hour,
            int minute,
            String label,
            boolean enabled,
            boolean vibration,
            String soundUri
    ) {

        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.enabled = enabled;
        this.vibration = vibration;

        if (soundUri == null) {
            this.soundUri = "";
        } else {
            this.soundUri = soundUri;
        }

        // Default: no repeating days
        this.monday = false;
        this.tuesday = false;
        this.wednesday = false;
        this.thursday = false;
        this.friday = false;
        this.saturday = false;
        this.sunday = false;
    }


    // =========================================================
    // BASIC GETTERS
    // =========================================================

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isVibration() {
        return vibration;
    }


    // =========================================================
    // SOUND
    // =========================================================

    public String getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(String soundUri) {

        if (soundUri == null) {
            this.soundUri = "";
        } else {
            this.soundUri = soundUri;
        }
    }


    // =========================================================
    // ENABLED
    // =========================================================

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    // =========================================================
    // REPEAT DAYS
    // =========================================================

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }


    // =========================================================
    // SET REPEAT DAYS
    // =========================================================

    public void setMonday(boolean value) {
        monday = value;
    }

    public void setTuesday(boolean value) {
        tuesday = value;
    }

    public void setWednesday(boolean value) {
        wednesday = value;
    }

    public void setThursday(boolean value) {
        thursday = value;
    }

    public void setFriday(boolean value) {
        friday = value;
    }

    public void setSaturday(boolean value) {
        saturday = value;
    }

    public void setSunday(boolean value) {
        sunday = value;
    }


    // =========================================================
    // CHECK REPEAT DAYS
    // =========================================================

    public boolean hasRepeatDays() {

        return monday
                || tuesday
                || wednesday
                || thursday
                || friday
                || saturday
                || sunday;
    }
}