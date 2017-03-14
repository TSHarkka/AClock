package com.example.e3juhleh.alarmclock;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.TimerTask;
import java.util.Timer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Color;


public class MainActivity extends AppCompatActivity {


    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer tTimer = new Timer();
    Boolean timeBool = false;
    Boolean ringBool = false;
    Boolean settingAlarm = false;
    Boolean alarmActive = false;
    Boolean snoozeOff = true;
    TextView hTextV;
    TextView mTextV;
    TextView sTextV;
    Button incTimerBtn;
    Button incTimerBigBtn;
    Button decTimerBtn;
    Button decTimerBigBtn;
    Button snzBtn;
    Button alrmBtn;
    Button stopBtn;
    Button timeBtn;
    Button startBtn;
    Button wakeybtn;
    int nCounter = 0;
    int nMins = 0;
    int nHrs = 0;
    int aHrs, aMins, aSecs = 0;

    Ringtone currentRingtone;

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // TimeBtn ei tee mitaan tallahetkella. Pitaisi togglena asettaa kellon aika.
    // Ringtone currentRingtone lisatty muuttujiin kun nakyvyys ei riittanyt on createn ulkopuolelle.
    // halytys tulee 1 sec aikaisemmin
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kellon tekstikentat
        hTextV = (TextView)findViewById(R.id.hoursText);
        mTextV = (TextView)findViewById(R.id.minutesText);
        sTextV = (TextView)findViewById(R.id.secondsText);

        // Ajan manipulointi napit
        incTimerBtn = (Button)findViewById(R.id.increaseTimer);
        incTimerBigBtn = (Button)findViewById(R.id.increaseTimerBig);
        decTimerBtn = (Button)findViewById(R.id.decreaseTimer);
        decTimerBigBtn = (Button)findViewById(R.id.decreaseTimerBig);

        // toipminta napit
        snzBtn = (Button)findViewById(R.id.snoozeButton);
        alrmBtn = (Button)findViewById(R.id.alarmButton);
        stopBtn = (Button)findViewById(R.id.stopButton);
        timeBtn = (Button)findViewById(R.id.timeButton);
        startBtn = (Button)findViewById(R.id.startButton);

        // Heratyksen huomionappi???
        wakeybtn = (Button)findViewById(R.id.wakey);

        // Nappien kuuntelijat
        incTimerBtn.setOnClickListener(timerInc);
        incTimerBigBtn.setOnClickListener(timerIncBig);
        decTimerBtn.setOnClickListener(timerDec);
        decTimerBigBtn.setOnClickListener(timerDecBig);
        snzBtn.setOnClickListener(snoozeListener);
        alrmBtn.setOnClickListener(alarmListener);
        stopBtn.setOnClickListener(alarmStopper);
        timeBtn.setOnClickListener(timeListener);
        startBtn.setOnClickListener(alarmStarter);

        // asetetaan kello aluksi -:-:-
        hTextV.setText("-");
        mTextV.setText("-");
        sTextV.setText("-");

        // Soittoaanen asettaminen
        Uri currentRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        Ringtone currentRingtone = RingtoneManager.getRingtone(this, currentRingtoneUri);

        // kaynnistetaan kello
        initializeTimerTask();
        tTimer.schedule(mTimerTask, 100, 1000); // onko tarkotuksella 100???
    }

    View.OnClickListener timerDec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (settingAlarm) {
                aSecs--;
                printTime(aSecs, aMins, aHrs);
            } else {
                nMins--;
                if (nMins <= 0) {
                    nMins = 0;
                }
                printTime(nCounter, nMins, nHrs);
            }
        }
    };

    View.OnClickListener timerDecBig = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            nHrs--;
            if(nHrs<=0){
                nHrs = 0;
            }
            printTime(nCounter, nMins, nHrs);
        }
    };

    View.OnClickListener timerInc = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if (settingAlarm) {
                aSecs++;
                printTime(aSecs, aMins, aHrs);
            } else {
                nMins++;
                if(nMins>=60){
                    nMins = 60;
                }
                printTime(nCounter, nMins, nHrs);
            }
        }
    };

    View.OnClickListener timerIncBig = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            nHrs++;
            if(nHrs>=60){
                nHrs = 60;
            }
            printTime(nCounter, nMins, nHrs);
        }
    };


    View.OnClickListener alarmStarter = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            System.out.println(" Alarm activated ");
            alarmActive = true;
            startBtn.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            //initializeTimerTask();
        }
    };

    // painaminen asetaa halytyksen poispaalta
    View.OnClickListener alarmStopper = new View.OnClickListener(){
        @Override
        public void onClick(View v){
        alarmActive = false;
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);
        wakeybtn.setVisibility(View.INVISIBLE);

        /*if(mTimerTask!=null){
            System.out.println(" Timer Stopped ");
            mTimerTask.cancel();
        }
        */
        }
    };

    // torkutaan 15 valein ja soitetaan halytysaani.
    View.OnClickListener snoozeListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            // asetetaan 15s paahan nykyisesta ajasta
            aHrs = nHrs;
            aMins = nMins;
            aSecs = nCounter + 15;
            // jos +15 menee yli 60 nollataan.
            if(aMins>=60){
                aMins = aMins - 60;
                aHrs++;
            }
            wakeybtn.setVisibility(View.INVISIBLE);
            //wakeUpRing(currentRingtone, settingAlarm);
        }
    };

    // Halytyksen aktivoiminen
    View.OnClickListener alarmListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            // asetetaan haly paalle, jos ei ole ja nollataan kello.
            if(!settingAlarm){
                settingAlarm = true;
                alrmBtn.setBackgroundColor(Color.RED);
                printTime(aSecs, aMins, aHrs);
                //nHrs = nMins = nCounter = 0;
            }
            // halypaalla niin kello->halytyskello
            else if(settingAlarm){
                settingAlarm = false;
                alrmBtn.setBackgroundColor(Color.BLUE);
                printTime(nCounter, nMins, nHrs);
            }
        }
    };

    View.OnClickListener timeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    public void printTime(int valueS, int valueM, int valueH){
        String m1 = String.format("%02d", valueS);
        sTextV.setText(m1);
        mTextV.setText(Integer.toString(valueM));
        hTextV.setText(Integer.toString(valueH));
        System.out.println("printTimessa");
    }

    public void wakeUpRing(Ringtone asd, boolean trigger){
            if (trigger == true) {
                asd.play();
            }
            if (trigger == false){
                asd.stop();
            }
    }


    public void initializeTimerTask(){
        mTimerTask = new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable(){
                    public void run()
                    {
                        nCounter++;
                        // muunnetaan counterin arvo min ja hrs
                        if(nCounter >= 60){
                            nCounter = 0;
                            nMins++;
                            if(nMins >= 60){
                                nMins = 0;
                                nHrs++;
                                if (nHrs >=25) {
                                    nHrs = 0;
                                }
                            }
                        }

                        // jos ei olla asettamassa halytysta naytetaan kellonaika
                        if (!settingAlarm)
                        {
                            printTime(nCounter, nMins, nHrs);
                        }

                        //  Halytyksen tarkistus
                        if(alarmActive && nHrs == aHrs){
                            if(nMins == aMins){
                                if(nCounter == aSecs){
                                    wakeybtn.setVisibility(View.VISIBLE);
                                    //settingAlarm = true;
                                    //wakeUpRing(currentRingtone ,settingAlarm);
                                        /*if(mTimerTask!=null) {
                                            System.out.println(" Timer Stopped ");
                                            mTimerTask.cancel();
                                            startBtn.setVisibility(View.VISIBLE);
                                            stopBtn.setVisibility(View.GONE);
                                        }
                                        */
                                }
                            }
                        }

                        System.out.println(" tick " + nCounter);
                    }
                });
            }
        };

    }
}
