package com.mobileclock.app;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WorldClockActivity extends AppCompatActivity {

    private LinearLayout citiesContainer;
    private Button backButton;

    private final Handler handler = new Handler();

    private final List<CityClock> cities = new ArrayList<>();

    private final Runnable clockRunnable =
            new Runnable() {

                @Override
                public void run() {

                    updateAllClocks();

                    handler.postDelayed(
                            this,
                            1000
                    );
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_world_clock
        );

        citiesContainer =
                findViewById(
                        R.id.citiesContainer
                );

        backButton =
                findViewById(
                        R.id.backButton
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        createCities();

        buildCityCards();

        updateAllClocks();

        handler.post(clockRunnable);
    }

    private void createCities() {

        cities.add(
                new CityClock(
                        "Johannesburg",
                        "South Africa",
                        "Africa/Johannesburg"
                )
        );

        cities.add(
                new CityClock(
                        "London",
                        "United Kingdom",
                        "Europe/London"
                )
        );

        cities.add(
                new CityClock(
                        "New York",
                        "United States",
                        "America/New_York"
                )
        );

        cities.add(
                new CityClock(
                        "Dubai",
                        "United Arab Emirates",
                        "Asia/Dubai"
                )
        );

        cities.add(
                new CityClock(
                        "Tokyo",
                        "Japan",
                        "Asia/Tokyo"
                )
        );

        cities.add(
                new CityClock(
                        "Sydney",
                        "Australia",
                        "Australia/Sydney"
                )
        );
    }

    private void buildCityCards() {

        citiesContainer.removeAllViews();

        for (int i = 0;
             i < cities.size();
             i++) {

            CityClock city =
                    cities.get(i);

            LinearLayout card =
                    new LinearLayout(this);

            card.setOrientation(
                    LinearLayout.VERTICAL
            );

            card.setPadding(
                    32,
                    28,
                    32,
                    28
            );

            card.setBackgroundColor(
                    0xFF1A1C21
            );

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(
                    0,
                    0,
                    0,
                    24
            );

            card.setLayoutParams(
                    cardParams
            );

            TextView cityName =
                    new TextView(this);

            cityName.setText(
                    city.cityName
            );

            cityName.setTextColor(
                    0xFFFFFFFF
            );

            cityName.setTextSize(
                    22
            );

            cityName.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            TextView countryName =
                    new TextView(this);

            countryName.setText(
                    city.countryName
            );

            countryName.setTextColor(
                    0xFFAAAAAA
            );

            countryName.setTextSize(
                    14
            );

            TextView timeText =
                    new TextView(this);

            timeText.setTextColor(
                    0xFFFFFFFF
            );

            timeText.setTextSize(
                    36
            );

            timeText.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            TextView dateText =
                    new TextView(this);

            dateText.setTextColor(
                    0xFFAAAAAA
            );

            dateText.setTextSize(
                    14
            );

            card.addView(cityName);
            card.addView(countryName);
            card.addView(timeText);
            card.addView(dateText);

            citiesContainer.addView(card);

            city.timeText = timeText;
            city.dateText = dateText;
        }
    }

    private void updateAllClocks() {

        Date now =
                new Date();

        for (CityClock city : cities) {

            if (city.timeText == null) {
                continue;
            }

            TimeZone timeZone =
                    TimeZone.getTimeZone(
                            city.timeZone
                    );

            SimpleDateFormat timeFormat =
                    new SimpleDateFormat(
                            "hh:mm:ss a",
                            Locale.getDefault()
                    );

            timeFormat.setTimeZone(
                    timeZone
            );

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "EEEE, dd MMMM yyyy",
                            Locale.getDefault()
                    );

            dateFormat.setTimeZone(
                    timeZone
            );

            city.timeText.setText(
                    timeFormat.format(now)
            );

            city.dateText.setText(
                    dateFormat.format(now)
            );
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(
                clockRunnable
        );

        super.onDestroy();
    }

    private static class CityClock {

        String cityName;
        String countryName;
        String timeZone;

        TextView timeText;
        TextView dateText;

        CityClock(
                String cityName,
                String countryName,
                String timeZone
        ) {

            this.cityName =
                    cityName;

            this.countryName =
                    countryName;

            this.timeZone =
                    timeZone;
        }
    }
}