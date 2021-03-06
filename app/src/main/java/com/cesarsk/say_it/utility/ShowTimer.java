package com.cesarsk.say_it.utility;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

/**
 * Created by cesarsk on 15/03/2017.
 */

@SuppressWarnings("ALL")
public class ShowTimer {

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int secs = 0;
    int mins = 0;
    TextView textView;

    public String getOld_time() {
        return old_time;
    }

    String old_time;

    public long getDurationInSecs() {
        return secs;
    }

    public ShowTimer(TextView timerTextView) {
        this.textView = timerTextView;
        old_time = timerTextView.getText().toString();
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    public void setTimer(String time) {
        textView.setText(time);
    }

    public void clearTimer() {
        textView.setText("");
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (timeInMilliseconds / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int hours = mins / 60;
            mins = mins % 60;
            int milliseconds = (int) (updatedTime % 1000);

            //String timer = "" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
            //String timer = "" + String.format("%02d", mins) + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            //String timer = "" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            old_time = "" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            textView.setText(old_time);
            customHandler.postDelayed(this, 0);
        }

    };
}
