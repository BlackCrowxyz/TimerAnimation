package com.example.timeranimation;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class TimerActivity extends AppCompatActivity {

    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;
    private TextView timerDisplay;
    private MaterialButton startButton;
    private MaterialButton pauseButton;
    private MaterialButton resetButton;

    private TimerManager timerManager;
    private boolean suppressPickerListener;

    private final TimerManager.Listener timerListener = new TimerManager.Listener() {
        @Override
        public void onTick(long remainingMillis) {
            updateDisplay(remainingMillis);
        }

        @Override
        public void onStateChanged(boolean isRunning, long configuredDuration, long remainingMillis) {
            updateButtonStates(isRunning);
            if (!isRunning) {
                updatePickerValues(configuredDuration);
            }
        }

        @Override
        public void onTimerFinished() {
            updateDisplay(0);
        }
    };

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

        timerManager = TimerManager.getInstance(getApplicationContext());
        initNumberPickers();
        updatePickerValues(timerManager.getConfiguredDurationMillis());
        updateDisplay(timerManager.getRemainingMillis());
        updateButtonStates(timerManager.isRunning());

        startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(v -> {
            timerManager.start();
        });

        pauseButton = findViewById(R.id.button_pause);
        pauseButton.setOnClickListener(v -> {
            timerManager.pause();
        });

        resetButton = findViewById(R.id.button_reset);
        resetButton.setOnClickListener(v -> {
            timerManager.reset();
        });
    }

    private void initNumberPickers() {
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        NumberPicker.OnValueChangeListener listener = (picker, oldVal, newVal) -> {
            if (suppressPickerListener) {
                return;
            }
            long duration = ((minutesPicker.getValue() * 60L) + secondsPicker.getValue()) * 1000L;
            timerManager.setConfiguredDuration(duration);
        };

        minutesPicker.setOnValueChangedListener(listener);
        secondsPicker.setOnValueChangedListener(listener);
    }

    private void updatePickerValues(long durationMillis) {
        suppressPickerListener = true;
        long minutes = durationMillis / 60000;
        long seconds = (durationMillis % 60000) / 1000;
        minutesPicker.setValue((int) minutes);
        secondsPicker.setValue((int) seconds);
        suppressPickerListener = false;
    }

    private void updateButtonStates(boolean isRunning) {
        startButton.setEnabled(!isRunning);
        pauseButton.setEnabled(isRunning);
    }

    private void updateDisplay(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long hundredths = (millis % 1000) / 10;
        String formatted = String.format("%02d:%02d.%02d", minutes, seconds, hundredths);
        timerDisplay.setText(formatted);
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerManager.addListener(timerListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerManager.removeListener(timerListener);
    }
}

