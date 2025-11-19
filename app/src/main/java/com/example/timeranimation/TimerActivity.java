package com.example.timeranimation;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class TimerActivity extends AppCompatActivity {

    private static final long MILLIS_IN_SECOND = 1000L;

    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;
    private TextView timerDisplay;
    private MaterialButton startButton;
    private MaterialButton pauseButton;
    private MaterialButton resetButton;

    private CountDownTimer countDownTimer;
    private long configuredDurationMillis;
    private long remainingMillis;
    private boolean isRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = findViewById(R.id.toolbar_timer);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_timer);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        minutesPicker = findViewById(R.id.picker_minutes);
        secondsPicker = findViewById(R.id.picker_seconds);
        timerDisplay = findViewById(R.id.text_timer_display);
        startButton = findViewById(R.id.button_start);
        pauseButton = findViewById(R.id.button_pause);
        resetButton = findViewById(R.id.button_reset);

        initNumberPickers();

        if (savedInstanceState != null) {
            configuredDurationMillis = savedInstanceState.getLong("configuredDurationMillis", MILLIS_IN_SECOND * 30);
            remainingMillis = savedInstanceState.getLong("remainingMillis", configuredDurationMillis);
            isRunning = savedInstanceState.getBoolean("isRunning", false);
            if (isRunning) {
                startTimer(remainingMillis);
            } else {
                updateDisplay(remainingMillis);
            }
        } else {
            configuredDurationMillis = MILLIS_IN_SECOND * 30;
            remainingMillis = configuredDurationMillis;
            updateDisplay(remainingMillis);
        }

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                if (remainingMillis <= 0) {
                    remainingMillis = Math.max(configuredDurationMillis, MILLIS_IN_SECOND);
                }
                startTimer(remainingMillis);
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                stopTimer();
            }
        });

        resetButton.setOnClickListener(v -> {
            stopTimer();
            remainingMillis = configuredDurationMillis;
            updateDisplay(remainingMillis);
        });
    }

    private void initNumberPickers() {
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        NumberPicker.OnValueChangeListener listener = (picker, oldVal, newVal) -> {
            configuredDurationMillis = ((minutesPicker.getValue() * 60L) + secondsPicker.getValue()) * MILLIS_IN_SECOND;
            if (!isRunning) {
                remainingMillis = configuredDurationMillis;
                updateDisplay(remainingMillis);
            }
        };

        minutesPicker.setOnValueChangedListener(listener);
        secondsPicker.setOnValueChangedListener(listener);

        minutesPicker.setValue(0);
        secondsPicker.setValue(30);
        configuredDurationMillis = ((minutesPicker.getValue() * 60L) + secondsPicker.getValue()) * MILLIS_IN_SECOND;
    }

    private void startTimer(long durationMillis) {
        countDownTimer = new CountDownTimer(durationMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                updateDisplay(remainingMillis);
            }

            @Override
            public void onFinish() {
                remainingMillis = 0;
                updateDisplay(remainingMillis);
                isRunning = false;
            }
        }.start();
        isRunning = true;
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
    }

    private void updateDisplay(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long hundredths = (millis % 1000) / 10;
        String formatted = String.format("%02d:%02d.%02d", minutes, seconds, hundredths);
        timerDisplay.setText(formatted);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("configuredDurationMillis", configuredDurationMillis);
        outState.putLong("remainingMillis", remainingMillis);
        outState.putBoolean("isRunning", isRunning);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }
}

