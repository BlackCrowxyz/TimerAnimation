package com.example.timeranimation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class AnalogClockSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final long FRAME_DELAY_MS = 16L;

    private final SurfaceHolder holder;
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint facePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hourMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint minuteMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint minuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint secondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint milliHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Thread renderThread;
    private boolean running = false;

    private final SharedPreferences preferences;

    public AnalogClockSurfaceView(Context context) {
        this(context, null);
    }

    public AnalogClockSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
        initPaints();
        applyPreferenceColors();
    }

    private void initPaints() {
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);

        facePaint.setColor(Color.parseColor("#ECEFF1"));
        facePaint.setStyle(Paint.Style.FILL);

        hourMarkerPaint.setStyle(Paint.Style.STROKE);
        hourMarkerPaint.setStrokeWidth(8f);

        minuteMarkerPaint.setStyle(Paint.Style.STROKE);
        minuteMarkerPaint.setStrokeWidth(3f);

        numberPaint.setColor(Color.DKGRAY);
        numberPaint.setTextSize(48f);
        numberPaint.setTextAlign(Paint.Align.CENTER);

        hourHandPaint.setStrokeCap(Paint.Cap.ROUND);
        hourHandPaint.setStrokeWidth(18f);

        minuteHandPaint.setStrokeCap(Paint.Cap.ROUND);
        minuteHandPaint.setStrokeWidth(12f);

        secondHandPaint.setStrokeCap(Paint.Cap.ROUND);
        secondHandPaint.setStrokeWidth(6f);

        milliHandPaint.setStrokeCap(Paint.Cap.ROUND);
        milliHandPaint.setStrokeWidth(3f);

        centerDotPaint.setStyle(Paint.Style.FILL);
        centerDotPaint.setColor(Color.DKGRAY);
    }

    private void applyPreferenceColors() {
        int hourColor = ColorPreferences.getColor(getContext(), preferences,
                ColorPreferences.KEY_HOUR_HAND, R.color.clock_hour_hand_default);
        int minuteColor = ColorPreferences.getColor(getContext(), preferences,
                ColorPreferences.KEY_MINUTE_HAND, R.color.clock_minute_hand_default);
        int secondColor = ColorPreferences.getColor(getContext(), preferences,
                ColorPreferences.KEY_SECOND_HAND, R.color.clock_second_hand_default);
        int milliColor = ColorPreferences.getColor(getContext(), preferences,
                ColorPreferences.KEY_MILLI_HAND, R.color.clock_milli_hand_default);
        int markerColor = ColorPreferences.getColor(getContext(), preferences,
                ColorPreferences.KEY_MARKER_COLOR, R.color.clock_marker_default);

        hourHandPaint.setColor(hourColor);
        minuteHandPaint.setColor(minuteColor);
        secondHandPaint.setColor(secondColor);
        milliHandPaint.setColor(milliColor);
        hourMarkerPaint.setColor(markerColor);
        minuteMarkerPaint.setColor(markerColor);
        numberPaint.setColor(markerColor);
        centerDotPaint.setColor(markerColor);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startRendering();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stopRendering();
    }

    private void startRendering() {
        if (renderThread != null && renderThread.isAlive()) {
            return;
        }
        running = true;
        renderThread = new Thread(this, "AnalogClockRenderer");
        renderThread.start();
    }

    private void stopRendering() {
        running = false;
        if (renderThread != null) {
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            renderThread = null;
        }
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                drawClock(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(FRAME_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void drawClock(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        float size = Math.min(width, height) * 0.8f;
        float centerX = width / 2f;
        float centerY = height / 2f;
        float halfSize = size / 2f;
        float cornerRadius = size * 0.1f;

        canvas.drawRect(0, 0, width, height, backgroundPaint);
        
        RectF squareRect = new RectF(
            centerX - halfSize,
            centerY - halfSize,
            centerX + halfSize,
            centerY + halfSize
        );
        canvas.drawRoundRect(squareRect, cornerRadius, cornerRadius, facePaint);

        float radius = halfSize;
        drawTickMarks(canvas, centerX, centerY, radius);
        drawHourNumbers(canvas, centerX, centerY, radius * 0.85f);

        Calendar calendar = Calendar.getInstance();
        float millis = calendar.get(Calendar.MILLISECOND);
        float second = calendar.get(Calendar.SECOND) + (millis / 1000f);
        float minute = calendar.get(Calendar.MINUTE) + (second / 60f);
        float hour = calendar.get(Calendar.HOUR) + (minute / 60f);

        drawHand(canvas, centerX, centerY, radius * 0.55f, hour / 12f, hourHandPaint);
        drawHand(canvas, centerX, centerY, radius * 0.75f, minute / 60f, minuteHandPaint);
        drawHand(canvas, centerX, centerY, radius * 0.85f, second / 60f, secondHandPaint);
        drawHand(canvas, centerX, centerY, radius * 0.65f, millis / 1000f, milliHandPaint);

        canvas.drawCircle(centerX, centerY, 12f, centerDotPaint);
    }

    private void drawTickMarks(Canvas canvas, float cx, float cy, float radius) {
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6 - 90);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            
            float outerX, outerY, innerX, innerY;
            float tickLength = (i % 5 == 0) ? 40f : 20f;
            
            // Determine which edge of the square the tick mark is on
            if (Math.abs(cos) > Math.abs(sin)) {
                // Left or right edge
                outerX = cx + (cos > 0 ? radius : -radius);
                outerY = cy + radius * sin;
                
                // Draw tick mark perpendicular to the edge (pointing inward)
                innerX = outerX - (cos > 0 ? tickLength : -tickLength);
                innerY = outerY;
            } else {
                // Top or bottom edge
                outerX = cx + radius * cos;
                outerY = cy + (sin > 0 ? radius : -radius);
                
                // Draw tick mark perpendicular to the edge (pointing inward)
                innerX = outerX;
                innerY = outerY - (sin > 0 ? tickLength : -tickLength);
            }
            
            canvas.drawLine(innerX, innerY, outerX, outerY, 
                    i % 5 == 0 ? hourMarkerPaint : minuteMarkerPaint);
        }
    }

    private void drawHourNumbers(Canvas canvas, float cx, float cy, float radius) {
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            float x, y;
            
            // Calculate position on square perimeter
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            
            // Determine which side of the square the number is on
            if (Math.abs(cos) > Math.abs(sin)) {
                // Left or right side
                x = cx + (cos > 0 ? radius : -radius);
                y = cy + radius * sin;
            } else {
                // Top or bottom side
                x = cx + radius * cos;
                y = cy + (sin > 0 ? radius : -radius);
            }
            
            // Adjust for text centering
            y += (numberPaint.getTextSize() / 3);
            
            String label = String.valueOf(i);
            canvas.drawText(label, x, y, numberPaint);
        }
    }

    private void drawHand(Canvas canvas, float cx, float cy, float length, float normalizedValue, Paint paint) {
        double angle = Math.toRadians(normalizedValue * 360 - 90);
        float x = (float) (cx + length * Math.cos(angle));
        float y = (float) (cy + length * Math.sin(angle));
        canvas.drawLine(cx, cy, x, y, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        stopRendering();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (ColorPreferences.isClockColorKey(key)) {
            applyPreferenceColors();
        }
    }
}

