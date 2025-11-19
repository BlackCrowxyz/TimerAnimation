package com.example.timeranimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TimerSurfaceView extends SurfaceView implements Runnable {
    private float length;
    private Thread thread;
    private SurfaceHolder surfaceHolder;
    private boolean running = false;

    public TimerSurfaceView(Context context, float length) {
        super(context);
        this.length = length;
        surfaceHolder = getHolder();
    }

    public void onResumeTimer(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    public void onPauseTimer(){
        running = false;

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        int sec = 0;

        while(running){
            if(surfaceHolder.getSurface().isValid()){
                // get canvas from holder
                Canvas canvas = surfaceHolder.lockCanvas();

                int w = getWidth(), h = getHeight();

                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                canvas.drawPaint(paint);

                // make the regpoly objects
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(5);

                RegPoly secMarks = new RegPoly(60, w/2, h/2, length, canvas, paint);
                RegPoly secHand  = new RegPoly(60, w/2, h/2, length-20, canvas, paint);

                // draw the marks and hand
                secMarks.drawNodes();
                secHand.drawRadius(sec+45);

                surfaceHolder.unlockCanvasAndPost(canvas);

                //wait a sec
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                sec++;
            }
        }
    }
}
