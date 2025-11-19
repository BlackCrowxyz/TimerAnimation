package com.example.timeranimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View clockButton = findViewById(R.id.button_clock);
        clockButton.setOnClickListener(v -> startActivity(new Intent(this, ClockActivity.class)));
        View timerButton = findViewById(R.id.button_timer);
        timerButton.setOnClickListener(v -> startActivity(new Intent(this, TimerActivity.class)));
    }
}