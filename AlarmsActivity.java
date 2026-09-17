package com.mobileclock.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class AlarmsActivity extends AppCompatActivity {

    private LinearLayout alarmsContainer;
    private TextView noAlarmsText;

    private AlarmStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_alarms
        );

        storage =
                new AlarmStorage(this);

        Button backButton =
                findViewById(
                        R.id.backButton
                );

        Button addAlarmButton =
                findViewById(
                        R.id.addAlarmButton
                );

        noAlarmsText =
                findViewById(
                        R.id.noAlarmsText
                );

        alarmsContainer =
                findViewById(
                        R.id.alarmsContainer
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        addAlarmButton.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    AlarmsActivity.this,
                                    AddAlarmActivity.class
                            );

                    startActivity(intent);
                }
        );

        loadAlarms();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (storage != null) {
            loadAlarms();
        }
    }

    private void loadAlarms() {

        alarmsContainer.removeAllViews();

        List<Alarm> alarms =
                storage.getAlarms();

        if (alarms.isEmpty()) {

            noAlarmsText.setVisibility(
                    TextView.VISIBLE
            );

            return;
        }

        noAlarmsText.setVisibility(
                TextView.GONE
        );

        for (Alarm alarm : alarms) {

            addAlarmView(alarm);
        }
    }

    private void addAlarmView(
            Alarm alarm
    ) {

        LinearLayout alarmLayout =
                new LinearLayout(this);

        alarmLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        alarmLayout.setPadding(
                20,
                20,
                20,
                20
        );

        /*
         * TOP ROW
         */
        LinearLayout topRow =
                new LinearLayout(this);

        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView timeText =
                new TextView(this);

        String time =
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        alarm.getHour(),
                        alarm.getMinute()
                );

        timeText.setText(time);
        timeText.setTextSize(32);

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        topRow.addView(
                timeText,
                timeParams
        );

        Switch alarmSwitch =
                new Switch(this);

        alarmSwitch.setChecked(
                alarm.isEnabled()
        );

        alarmSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    alarm.setEnabled(
                            isChecked
                    );

                    storage.updateAlarm(
                            alarm
                    );

                    AlarmScheduler scheduler =
                            new AlarmScheduler(this);

                    if (isChecked) {

                        scheduler.schedule(
                                alarm
                        );

                    } else {

                        scheduler.cancel(
                                alarm
                        );
                    }
                }
        );

        topRow.addView(
                alarmSwitch
        );

        alarmLayout.addView(
                topRow
        );

        /*
         * LABEL
         */
        TextView labelText =
                new TextView(this);

        labelText.setText(
                alarm.getLabel()
        );

        labelText.setTextSize(16);

        alarmLayout.addView(
                labelText
        );

        /*
         * REPEAT DAYS
         */
        TextView repeatText =
                new TextView(this);

        repeatText.setText(
                getRepeatDays(alarm)
        );

        repeatText.setTextSize(14);

        alarmLayout.addView(
                repeatText
        );

        /*
         * BUTTON ROW
         */
        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonRow.setGravity(
                Gravity.CENTER
        );

        Button editButton =
                new Button(this);

        editButton.setText(
                "EDIT"
        );

        editButton.setOnClickListener(
                v -> editAlarm(alarm)
        );

        Button deleteButton =
                new Button(this);

        deleteButton.setText(
                "DELETE"
        );

        deleteButton.setOnClickListener(
                v -> confirmDelete(alarm)
        );

        buttonRow.addView(
                editButton
        );

        buttonRow.addView(
                deleteButton
        );

        alarmLayout.addView(
                buttonRow
        );

        alarmsContainer.addView(
                alarmLayout
        );
    }

    private String getRepeatDays(
            Alarm alarm
    ) {

        if (!alarm.hasRepeatDays()) {

            return "One time";
        }

        StringBuilder days =
                new StringBuilder();

        if (alarm.isMonday()) {
            days.append("Mon ");
        }

        if (alarm.isTuesday()) {
            days.append("Tue ");
        }

        if (alarm.isWednesday()) {
            days.append("Wed ");
        }

        if (alarm.isThursday()) {
            days.append("Thu ");
        }

        if (alarm.isFriday()) {
            days.append("Fri ");
        }

        if (alarm.isSaturday()) {
            days.append("Sat ");
        }

        if (alarm.isSunday()) {
            days.append("Sun");
        }

        return days.toString().trim();
    }

    private void confirmDelete(
            Alarm alarm
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete alarm?")
                .setMessage(
                        "Delete \"" +
                                alarm.getLabel() +
                                "\"?"
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> {

                            AlarmScheduler scheduler =
                                    new AlarmScheduler(this);

                            scheduler.cancel(
                                    alarm
                            );

                            storage.deleteAlarm(
                                    alarm.getId()
                            );

                            Toast.makeText(
                                    this,
                                    "Alarm deleted",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadAlarms();
                        }
                )
                .show();
    }

    private void editAlarm(
            Alarm alarm
    ) {

        Intent intent =
                new Intent(
                        this,
                        AddAlarmActivity.class
                );

        intent.putExtra(
                "editMode",
                true
        );

        intent.putExtra(
                "alarmId",
                alarm.getId()
        );

        startActivity(intent);
    }
}