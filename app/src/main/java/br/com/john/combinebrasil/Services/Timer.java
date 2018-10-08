package br.com.john.combinebrasil.Services;

/**
 * Created by GTAC on 22/01/2017.
 */


import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;

import java.util.Date;

import br.com.john.combinebrasil.TimerActivity;

public class Timer {
    private static Thread timerThread = null;
    private static boolean play;
    private static final Date date = new Date();
    private static long milisecondCounter=00,secondCounter=00, minuteCounter=00;
    private static String timeCount = "";
    private static TextView textView;
    private static Activity Act;

    public Timer(){
    }

    public void setValue(long min, long sec){
        milisecondCounter = 00;
        secondCounter = sec;
        minuteCounter = min;
    }

    public void initCount(Activity act){
        Act = act;
        play = true;
        timeUpdate();
    }
    public void timeUpdate() {
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(play){
                    Date newDate = new Date();
                    if(((newDate.getTime()) - date.getTime()) > 100) {
                        milisecondCounter = milisecondCounter - 1;
                        mHandlerUpdateMili.post(mUpdateMili);
                        verifyTimer();
                        if (milisecondCounter <= 00) {
                            verifyTimer();
                            secondCounter = secondCounter - 1;
                            mHandlerUpdateSec.post(mUpdateSec);
                            milisecondCounter=100;
                            if (secondCounter <= 00) {
                                verifyTimer();
                                minuteCounter = minuteCounter - 1;
                                mHandlerUpdateMinute.post(mUpdateMinute);
                                secondCounter = 60;
                            }
                        }
                    }
                    try{
                        timerThread.sleep(10);
                    }catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });
        timerThread.start();
    }

    private void verifyTimer(){
        if(minuteCounter <= 0){
            if(secondCounter<=0){
                this.stop();
            }
        }
    }
    final Handler mHandlerUpdateMili = new Handler();
    final Runnable mUpdateMili = new Runnable() {
        public void run() {
            timeCount = getTimeCount();
            textView.setText(timeCount);
        }
    };
    final Handler mHandlerUpdateSec = new Handler();
    final Runnable mUpdateSec = new Runnable() {
        public void run() {
            timeCount = getTimeCount();
        }
    };
    final Handler mHandlerUpdateMinute = new Handler();
    final Runnable mUpdateMinute= new Runnable() {
        public void run() {
            timeCount = getTimeCount();
        }
    };
    final Handler mHandlerUpdateHour = new Handler();
    final Runnable mUpdateHour = new Runnable() {
        public void run() {
            timeCount = getTimeCount();
        }
    };

    public void setStop(){
        timerThread.interrupt();
        milisecondCounter =00;
        minuteCounter=00;
        secondCounter=00;
        play=false;
    }

    public void stop(){
        timerThread.interrupt();
        milisecondCounter =00;
        minuteCounter=00;
        secondCounter=00;
        play=false;
        alertFinishTimer();
        //SharedPreferencesAdapter.setTimeChronometer(AllActivitys.mainActivity, getTimeCount());
    }

    private void alertFinishTimer(){
        TimerActivity.alertFinishTimer(Act);
    }

    public void pause(){
        timerThread.interrupt();
        play=false;
    }

    public static String getTimeCount(){
        String minute=String.valueOf(minuteCounter),
                second=String.valueOf(secondCounter);

        if(minuteCounter<10)
            minute = String.valueOf("0"+minuteCounter);
        if(secondCounter<10)
            second = String.valueOf("0"+secondCounter);

        return String.valueOf(minute+":"+second);

    }

    public void setTimeCount(String time){
        try{
            String[] timeArray = time.split(":");
            minuteCounter = Long.parseLong(timeArray[0]);
            secondCounter = Long.parseLong(timeArray[1]);
            milisecondCounter = Long.parseLong(timeArray[1]);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void setPlay(boolean play){
        Timer.play = play;
    }

    public boolean getPlay(){
        return play;
    }

    public void setTextView(TextView txt){
        textView = txt;
    }
}
