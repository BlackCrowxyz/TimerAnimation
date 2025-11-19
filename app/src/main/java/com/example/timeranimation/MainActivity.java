package com.example.timeranimation;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TimerSurfaceView timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new TimerSurfaceView(this, 400);
        setContentView(timer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.onResumeTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.onPauseTimer();
    }
}