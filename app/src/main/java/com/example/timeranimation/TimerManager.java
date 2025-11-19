package com.example.timeranimation;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TimerManager {

    public interface Listener {
        void onTick(long remainingMillis);
        void onStateChanged(boolean isRunning, long configuredDuration, long remainingMillis);
        void onTimerFinished();
    }

    private static TimerManager instance;

    private final Context appContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    private CountDownTimer countDownTimer;
    private long configuredDurationMillis = 30_000L;
    private long remainingMillis = configuredDurationMillis;
    private boolean running = false;

    private TimerManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static synchronized TimerManager getInstance(Context context) {
        if (instance == null) {
            instance = new TimerManager(context);
        }
        return instance;
    }

    public void setConfiguredDuration(long durationMillis) {
        configuredDurationMillis = Math.max(1_000L, durationMillis);
        if (!running) {
            remainingMillis = configuredDurationMillis;
            notifyState();
            notifyTick();
        }
    }

    public long getConfiguredDurationMillis() {
        return configuredDurationMillis;
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (running) {
            return;
        }
        long startMillis = remainingMillis > 0 ? remainingMillis : configuredDurationMillis;
        startInternal(startMillis);
    }

    public void pause() {
        if (!running) {
            return;
        }
        cancelTimer();
        running = false;
        notifyState();
    }

    public void reset() {
        cancelTimer();
        remainingMillis = configuredDurationMillis;
        running = false;
        notifyState();
        notifyTick();
    }

    private void startInternal(long durationMillis) {
        cancelTimer();
        countDownTimer = new CountDownTimer(durationMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                notifyTick();
            }

            @Override
            public void onFinish() {
                remainingMillis = 0;
                running = false;
                notifyTick();
                notifyState();
                notifyFinished();
                showFinishedToast();
            }
        }.start();
        running = true;
        notifyState();
    }

    private void showFinishedToast() {
        mainHandler.post(() -> Toast.makeText(appContext, R.string.timer_finished_toast, Toast.LENGTH_LONG).show());
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void addListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            listener.onStateChanged(running, configuredDurationMillis, remainingMillis);
            listener.onTick(remainingMillis);
        }
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyTick() {
        for (Listener listener : listeners) {
            listener.onTick(remainingMillis);
        }
    }

    private void notifyState() {
        for (Listener listener : listeners) {
            listener.onStateChanged(running, configuredDurationMillis, remainingMillis);
        }
    }

    private void notifyFinished() {
        for (Listener listener : listeners) {
            listener.onTimerFinished();
        }
    }
}

