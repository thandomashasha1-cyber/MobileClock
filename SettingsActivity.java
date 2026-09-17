package com.mobileclock.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME =
            "mobile_clock_settings";

    private static final String TIME_FORMAT =
            "time_format";

    private static final String VIBRATION =
            "default_vibration";

    private static final String THEME =
            "theme";

    private static final String SNOOZE_DURATION =
            "snooze_duration";


    // =========================================================
    // TIME FORMAT
    // =========================================================

    private RadioGroup timeFormatGroup;

    private RadioButton twelveHourRadio;

    private RadioButton twentyFourHourRadio;


    // =========================================================
    // THEME
    // =========================================================

    private RadioGroup themeGroup;

    private RadioButton darkThemeRadio;

    private RadioButton lightThemeRadio;

    private RadioButton systemThemeRadio;


    // =========================================================
    // VIBRATION
    // =========================================================

    private Switch vibrationSwitch;


    // =========================================================
    // SNOOZE
    // =========================================================

    private RadioGroup snoozeGroup;

    private RadioButton snooze1Radio;

    private RadioButton snooze5Radio;

    private RadioButton snooze10Radio;

    private RadioButton snooze15Radio;

    private RadioButton snooze30Radio;


    // =========================================================
    // BACK BUTTON
    // =========================================================

    private Button backButton;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_settings
        );


        // =====================================================
        // TIME FORMAT VIEWS
        // =====================================================

        timeFormatGroup =
                findViewById(
                        R.id.timeFormatGroup
                );

        twelveHourRadio =
                findViewById(
                        R.id.twelveHourRadio
                );

        twentyFourHourRadio =
                findViewById(
                        R.id.twentyFourHourRadio
                );


        // =====================================================
        // THEME VIEWS
        // =====================================================

        themeGroup =
                findViewById(
                        R.id.themeGroup
                );

        darkThemeRadio =
                findViewById(
                        R.id.darkThemeRadio
                );

        lightThemeRadio =
                findViewById(
                        R.id.lightThemeRadio
                );

        systemThemeRadio =
                findViewById(
                        R.id.systemThemeRadio
                );


        // =====================================================
        // VIBRATION
        // =====================================================

        vibrationSwitch =
                findViewById(
                        R.id.vibrationSwitch
                );


        // =====================================================
        // SNOOZE VIEWS
        // =====================================================

        snoozeGroup =
                findViewById(
                        R.id.snoozeGroup
                );

        snooze1Radio =
                findViewById(
                        R.id.snooze1Radio
                );

        snooze5Radio =
                findViewById(
                        R.id.snooze5Radio
                );

        snooze10Radio =
                findViewById(
                        R.id.snooze10Radio
                );

        snooze15Radio =
                findViewById(
                        R.id.snooze15Radio
                );

        snooze30Radio =
                findViewById(
                        R.id.snooze30Radio
                );


        // =====================================================
        // BACK BUTTON
        // =====================================================

        backButton =
                findViewById(
                        R.id.backButton
                );


        // =====================================================
        // LOAD SETTINGS
        // =====================================================

        loadSettings();


        // =====================================================
        // TIME FORMAT
        // =====================================================

        timeFormatGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId ==
                            R.id.twelveHourRadio) {

                        saveSetting(
                                TIME_FORMAT,
                                "12"
                        );

                    } else if (checkedId ==
                            R.id.twentyFourHourRadio) {

                        saveSetting(
                                TIME_FORMAT,
                                "24"
                        );
                    }
                }
        );


        // =====================================================
        // THEME
        // =====================================================

        themeGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId ==
                            R.id.darkThemeRadio) {

                        saveSetting(
                                THEME,
                                "dark"
                        );

                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate
                                                .MODE_NIGHT_YES
                                );

                    } else if (checkedId ==
                            R.id.lightThemeRadio) {

                        saveSetting(
                                THEME,
                                "light"
                        );

                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate
                                                .MODE_NIGHT_NO
                                );

                    } else if (checkedId ==
                            R.id.systemThemeRadio) {

                        saveSetting(
                                THEME,
                                "system"
                        );

                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate
                                                .MODE_NIGHT_FOLLOW_SYSTEM
                                );
                    }
                }
        );


        // =====================================================
        // VIBRATION
        // =====================================================

        vibrationSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    getSharedPreferences(
                            PREFS_NAME,
                            MODE_PRIVATE
                    )
                            .edit()
                            .putBoolean(
                                    VIBRATION,
                                    isChecked
                            )
                            .apply();
                }
        );


        // =====================================================
        // SNOOZE
        // =====================================================

        snoozeGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    int duration = 5;

                    if (checkedId ==
                            R.id.snooze1Radio) {

                        duration = 1;

                    } else if (checkedId ==
                            R.id.snooze5Radio) {

                        duration = 5;

                    } else if (checkedId ==
                            R.id.snooze10Radio) {

                        duration = 10;

                    } else if (checkedId ==
                            R.id.snooze15Radio) {

                        duration = 15;

                    } else if (checkedId ==
                            R.id.snooze30Radio) {

                        duration = 30;
                    }


                    saveSetting(
                            SNOOZE_DURATION,
                            duration
                    );
                }
        );


        // =====================================================
        // BACK
        // =====================================================

        backButton.setOnClickListener(
                v -> finish()
        );
    }


    // =========================================================
    // LOAD SETTINGS
    // =========================================================

    private void loadSettings() {

        android.content.SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );


        // =====================================================
        // TIME FORMAT
        // =====================================================

        String timeFormat =
                preferences.getString(
                        TIME_FORMAT,
                        "12"
                );


        if ("24".equals(timeFormat)) {

            twentyFourHourRadio.setChecked(
                    true
            );

        } else {

            twelveHourRadio.setChecked(
                    true
            );
        }


        // =====================================================
        // VIBRATION
        // =====================================================

        boolean vibration =
                preferences.getBoolean(
                        VIBRATION,
                        true
                );


        vibrationSwitch.setChecked(
                vibration
        );


        // =====================================================
        // THEME
        // =====================================================

        String theme =
                preferences.getString(
                        THEME,
                        "dark"
                );


        if ("light".equals(theme)) {

            lightThemeRadio.setChecked(
                    true
            );

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO
                    );

        } else if ("system".equals(theme)) {

            systemThemeRadio.setChecked(
                    true
            );

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_FOLLOW_SYSTEM
                    );

        } else {

            darkThemeRadio.setChecked(
                    true
            );

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES
                    );
        }


        // =====================================================
        // SNOOZE
        // =====================================================

        int snoozeDuration =
                preferences.getInt(
                        SNOOZE_DURATION,
                        5
                );


        switch (snoozeDuration) {

            case 1:

                snooze1Radio.setChecked(
                        true
                );

                break;

            case 10:

                snooze10Radio.setChecked(
                        true
                );

                break;

            case 15:

                snooze15Radio.setChecked(
                        true
                );

                break;

            case 30:

                snooze30Radio.setChecked(
                        true
                );

                break;

            case 5:
            default:

                snooze5Radio.setChecked(
                        true
                );

                break;
        }
    }


    // =========================================================
    // SAVE STRING
    // =========================================================

    private void saveSetting(
            String key,
            String value
    ) {

        getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        key,
                        value
                )
                .apply();
    }


    // =========================================================
    // SAVE INTEGER
    // =========================================================

    private void saveSetting(
            String key,
            int value
    ) {

        getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        )
                .edit()
                .putInt(
                        key,
                        value
                )
                .apply();
    }
}
