package com.mobileclock.app;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AddAlarmActivity extends AppCompatActivity {

    private static final int SOUND_PICKER_REQUEST = 200;

    private TimePicker timePicker;
    private EditText labelInput;
    private CheckBox vibrationCheckBox;

    private CheckBox mondayCheckBox;
    private CheckBox tuesdayCheckBox;
    private CheckBox wednesdayCheckBox;
    private CheckBox thursdayCheckBox;
    private CheckBox fridayCheckBox;
    private CheckBox saturdayCheckBox;
    private CheckBox sundayCheckBox;

    private Button chooseSoundButton;
    private TextView selectedSoundText;

    private AlarmStorage storage;

    private boolean editMode = false;
    private int editingAlarmId = -1;

    // Stores the selected ringtone URI
    private String selectedSoundUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_alarm
        );

        storage =
                new AlarmStorage(this);

        timePicker =
                findViewById(
                        R.id.timePicker
                );

        labelInput =
                findViewById(
                        R.id.labelInput
                );

        vibrationCheckBox =
                findViewById(
                        R.id.vibrationCheckBox
                );

        mondayCheckBox =
                findViewById(
                        R.id.mondayCheckBox
                );

        tuesdayCheckBox =
                findViewById(
                        R.id.tuesdayCheckBox
                );

        wednesdayCheckBox =
                findViewById(
                        R.id.wednesdayCheckBox
                );

        thursdayCheckBox =
                findViewById(
                        R.id.thursdayCheckBox
                );

        fridayCheckBox =
                findViewById(
                        R.id.fridayCheckBox
                );

        saturdayCheckBox =
                findViewById(
                        R.id.saturdayCheckBox
                );

        sundayCheckBox =
                findViewById(
                        R.id.sundayCheckBox
                );

        chooseSoundButton =
                findViewById(
                        R.id.chooseSoundButton
                );

        selectedSoundText =
                findViewById(
                        R.id.selectedSoundText
                );

        Button backButton =
                findViewById(
                        R.id.backButton
                );

        Button saveButton =
                findViewById(
                        R.id.saveAlarmButton
                );

        // ---------------------------------------------------------
        // BACK
        // ---------------------------------------------------------

        backButton.setOnClickListener(
                v -> finish()
        );

        // ---------------------------------------------------------
        // SOUND PICKER
        // ---------------------------------------------------------

        chooseSoundButton.setOnClickListener(
                v -> chooseAlarmSound()
        );

        // ---------------------------------------------------------
        // CHECK EDIT MODE
        // ---------------------------------------------------------

        editMode =
                getIntent().getBooleanExtra(
                        "editMode",
                        false
                );

        editingAlarmId =
                getIntent().getIntExtra(
                        "alarmId",
                        -1
                );

        if (editMode &&
                editingAlarmId != -1) {

            loadAlarmForEditing(
                    editingAlarmId
            );

            saveButton.setText(
                    "UPDATE ALARM"
            );

        } else {

            saveButton.setText(
                    "SAVE ALARM"
            );
        }

        // ---------------------------------------------------------
        // SAVE
        // ---------------------------------------------------------

        saveButton.setOnClickListener(
                v -> saveAlarm()
        );
    }

    // =========================================================
    // CHOOSE ALARM SOUND
    // =========================================================

    private void chooseAlarmSound() {

        Uri currentSoundUri;

        if (selectedSoundUri == null ||
                selectedSoundUri.isEmpty()) {

            currentSoundUri =
                    RingtoneManager.getDefaultUri(
                            RingtoneManager.TYPE_ALARM
                    );

        } else {

            currentSoundUri =
                    Uri.parse(
                            selectedSoundUri
                    );
        }

        Intent intent =
                new Intent(
                        RingtoneManager.ACTION_RINGTONE_PICKER
                );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_ALARM
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                "Choose alarm sound"
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                currentSoundUri
        );

        startActivityForResult(
                intent,
                SOUND_PICKER_REQUEST
        );
    }

    // =========================================================
    // SOUND PICKER RESULT
    // =========================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode !=
                SOUND_PICKER_REQUEST) {

            return;
        }

        if (resultCode !=
                RESULT_OK ||
                data == null) {

            return;
        }

        Uri selectedUri;

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            selectedUri =
                    data.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                            Uri.class
                    );

        } else {

            selectedUri =
                    data.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                    );
        }

        if (selectedUri == null) {

            selectedSoundUri = "";

            selectedSoundText.setText(
                    "Default alarm sound"
            );

            return;
        }

        selectedSoundUri =
                selectedUri.toString();

        // Get the ringtone name
        Ringtone ringtone =
                RingtoneManager.getRingtone(
                        this,
                        selectedUri
                );

        if (ringtone != null) {

            String ringtoneTitle =
                    ringtone.getTitle(this);

            if (ringtoneTitle != null &&
                    !ringtoneTitle.trim().isEmpty()) {

                selectedSoundText.setText(
                        ringtoneTitle
                );

            } else {

                selectedSoundText.setText(
                        "Selected alarm sound"
                );
            }

        } else {

            selectedSoundText.setText(
                    "Selected alarm sound"
            );
        }
    }

    // =========================================================
    // LOAD ALARM FOR EDITING
    // =========================================================

    private void loadAlarmForEditing(
            int alarmId
    ) {

        List<Alarm> alarms =
                storage.getAlarms();

        for (Alarm alarm : alarms) {

            if (alarm.getId() == alarmId) {

                // TIME

                if (Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.M) {

                    timePicker.setHour(
                            alarm.getHour()
                    );

                    timePicker.setMinute(
                            alarm.getMinute()
                    );

                } else {

                    timePicker.setCurrentHour(
                            alarm.getHour()
                    );

                    timePicker.setCurrentMinute(
                            alarm.getMinute()
                    );
                }

                // LABEL

                labelInput.setText(
                        alarm.getLabel()
                );

                // VIBRATION

                vibrationCheckBox.setChecked(
                        alarm.isVibration()
                );

                // REPEAT DAYS

                mondayCheckBox.setChecked(
                        alarm.isMonday()
                );

                tuesdayCheckBox.setChecked(
                        alarm.isTuesday()
                );

                wednesdayCheckBox.setChecked(
                        alarm.isWednesday()
                );

                thursdayCheckBox.setChecked(
                        alarm.isThursday()
                );

                fridayCheckBox.setChecked(
                        alarm.isFriday()
                );

                saturdayCheckBox.setChecked(
                        alarm.isSaturday()
                );

                sundayCheckBox.setChecked(
                        alarm.isSunday()
                );

                // SOUND

                selectedSoundUri =
                        alarm.getSoundUri();

                updateSoundName();

                break;
            }
        }
    }

    // =========================================================
    // UPDATE SOUND NAME
    // =========================================================

    private void updateSoundName() {

        if (selectedSoundUri == null ||
                selectedSoundUri.isEmpty()) {

            selectedSoundText.setText(
                    "Default alarm sound"
            );

            return;
        }

        try {

            Uri uri =
                    Uri.parse(
                            selectedSoundUri
                    );

            Ringtone ringtone =
                    RingtoneManager.getRingtone(
                            this,
                            uri
                    );

            if (ringtone != null) {

                String title =
                        ringtone.getTitle(this);

                if (title != null &&
                        !title.trim().isEmpty()) {

                    selectedSoundText.setText(
                            title
                    );

                    return;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        selectedSoundText.setText(
                "Selected alarm sound"
        );
    }

    // =========================================================
    // SAVE ALARM
    // =========================================================

    private void saveAlarm() {

        int hour;
        int minute;

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.M) {

            hour =
                    timePicker.getHour();

            minute =
                    timePicker.getMinute();

        } else {

            hour =
                    timePicker.getCurrentHour();

            minute =
                    timePicker.getCurrentMinute();
        }

        String label =
                labelInput.getText()
                        .toString()
                        .trim();

        if (label.isEmpty()) {

            label = "Alarm";
        }

        boolean vibration =
                vibrationCheckBox.isChecked();

        // =====================================================
        // EDIT EXISTING ALARM
        // =====================================================

        if (editMode &&
                editingAlarmId != -1) {

            Alarm oldAlarm =
                    findAlarm(
                            editingAlarmId
                    );

            if (oldAlarm == null) {

                Toast.makeText(
                        this,
                        "Alarm could not be found.",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

                return;
            }

            if (!canScheduleExactAlarms()) {
                return;
            }

            AlarmScheduler scheduler =
                    new AlarmScheduler(this);

            scheduler.cancel(
                    oldAlarm
            );

            Alarm updatedAlarm =
                    new Alarm(
                            oldAlarm.getId(),
                            hour,
                            minute,
                            label,
                            true,
                            vibration
                    );

            setRepeatDays(
                    updatedAlarm
            );

            updatedAlarm.setSoundUri(
                    selectedSoundUri
            );

            scheduler.schedule(
                    updatedAlarm
            );

            storage.updateAlarm(
                    updatedAlarm
            );

            Toast.makeText(
                    this,
                    "Alarm updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // =====================================================
        // CREATE NEW ALARM
        // =====================================================

        Alarm alarm =
                new Alarm(
                        createAlarmId(),
                        hour,
                        minute,
                        label,
                        true,
                        vibration
                );

        setRepeatDays(
                alarm
        );

        alarm.setSoundUri(
                selectedSoundUri
        );

        if (!canScheduleExactAlarms()) {
            return;
        }

        AlarmScheduler scheduler =
                new AlarmScheduler(this);

        scheduler.schedule(
                alarm
        );

        storage.saveAlarm(
                alarm
        );

        Toast.makeText(
                this,
                "Alarm saved successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    // =========================================================
    // FIND ALARM
    // =========================================================

    private Alarm findAlarm(
            int alarmId
    ) {

        List<Alarm> alarms =
                storage.getAlarms();

        for (Alarm alarm : alarms) {

            if (alarm.getId() == alarmId) {

                return alarm;
            }
        }

        return null;
    }

    // =========================================================
    // SET REPEAT DAYS
    // =========================================================

    private void setRepeatDays(
            Alarm alarm
    ) {

        alarm.setMonday(
                mondayCheckBox.isChecked()
        );

        alarm.setTuesday(
                tuesdayCheckBox.isChecked()
        );

        alarm.setWednesday(
                wednesdayCheckBox.isChecked()
        );

        alarm.setThursday(
                thursdayCheckBox.isChecked()
        );

        alarm.setFriday(
                fridayCheckBox.isChecked()
        );

        alarm.setSaturday(
                saturdayCheckBox.isChecked()
        );

        alarm.setSunday(
                sundayCheckBox.isChecked()
        );
    }

    // =========================================================
    // CREATE ALARM ID
    // =========================================================

    private int createAlarmId() {

        return (int)
                (System.currentTimeMillis()
                        & 0xfffffff);
    }

    // =========================================================
    // EXACT ALARM PERMISSION
    // =========================================================

    private boolean canScheduleExactAlarms() {

        AlarmManager alarmManager =
                (AlarmManager)
                        getSystemService(
                                Context.ALARM_SERVICE
                        );

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

            if (alarmManager != null &&
                    !alarmManager.canScheduleExactAlarms()) {

                Toast.makeText(
                        this,
                        "Please allow exact alarms for Mobile Clock.",
                        Toast.LENGTH_LONG
                ).show();

                Intent intent =
                        new Intent(
                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        );

                startActivity(intent);

                return false;
            }
        }

        return true;
    }
}