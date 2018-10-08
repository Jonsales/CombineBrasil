package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.sql.Time;
import java.util.ArrayList;
import java.util.UUID;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTestsResults;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Connection.JSONServices.CreateJSON;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostSync;
import br.com.john.combinebrasil.Connection.Posts.PutTests;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.MaskHeight;
import br.com.john.combinebrasil.Services.MessageOptions;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;
import br.com.john.combinebrasil.Services.Timer;

public class TimerActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView textNameTimer, textTimer, textNameTestInfo, textNamePlayerInfo,
            TextDetailsNameInfo, textProgress, textshowRating;
    WebView webview;
    ConstraintLayout constraintReset, constraintPlay, constraintStop, constraintInfo, constraintProgress,
            constraintRating, constraintConfigure;
    ImageView imgTestArrow,imgAthleteArrow;
    EditText editFirstResult;
    RecyclerView recyclerResults;
    Button btnSaveResults, btnCloseInfo, buttonCancelRating, buttonReady, buttonConfigureTimer, buttonCancelConfigureTimer;
    MaterialBetterSpinner spinnerMinutes, spinnerSeconds;
    LinearLayout linearSetting;
    DatabaseHelper db;

    private final Timer timer = new Timer();
    private boolean init = false, arrowDownTest=false, arrowDownPlayer=false, existTest=false;

    String idAthlete = "";
    int position = 0;
    long minutes=0, seconds=0;
    float ratingValue = 0;
    RatingBar ratingTimer;
    Tests test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);

        ImageView imgInfo = (ImageView) findViewById(R.id.img_create_account);
        imgInfo.setImageDrawable(TimerActivity.this.getDrawable(R.drawable.ic_info));
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo();
            }
        });

        linearSetting = (LinearLayout) findViewById(R.id.linear_setting);
        linearSetting.setVisibility(View.VISIBLE);
        linearSetting.setOnClickListener(configureTimer);

        LinearLayout linearDeleteTest = (LinearLayout) findViewById(R.id.linear_delete);
        linearDeleteTest.setVisibility(View.GONE);

        textTimer = (TextView) findViewById(R.id.txt_timer);
        textProgress = (TextView) findViewById(R.id.text_progress);
        textNameTimer = (TextView) findViewById(R.id.text_name_timer);
        textNameTestInfo = (TextView) findViewById(R.id.text_info_name_test);
        textNamePlayerInfo = (TextView) findViewById(R.id.text_info_name_athlete);
        webview = (WebView) findViewById(R.id.webview);
        TextDetailsNameInfo = (TextView) findViewById(R.id.text_info_details_athlete);
        textshowRating = (TextView) findViewById(R.id.text_show_qualify_cronometer);

        editFirstResult = (EditText) findViewById(R.id.edit_first_result);

        buttonCancelRating = (Button) findViewById(R.id.button_cancel) ;
        buttonCancelConfigureTimer = (Button) findViewById(R.id.button_cancel_configure) ;
        buttonConfigureTimer = (Button) findViewById(R.id.button_configure_timer);
        btnSaveResults = (Button) findViewById(R.id.button_save_results);
        buttonReady = (Button) findViewById(R.id.button_ready_results);
        btnCloseInfo = (Button) findViewById(R.id.button_close_info);

        recyclerResults = (RecyclerView) findViewById(R.id.recyclerResults);

        buttonCancelConfigureTimer.setOnClickListener(clickCancelConfigure);
        buttonConfigureTimer.setOnClickListener(clickAlterTimer);
        btnSaveResults.setOnClickListener(clickSaveResults);
        btnSaveResults.setEnabled(false);
        btnSaveResults.setAlpha(0.5f);
        buttonReady.setOnClickListener(saveTest);
        constraintProgress = (ConstraintLayout) findViewById(R.id.linear_progress);
        constraintConfigure = (ConstraintLayout) findViewById(R.id.constraint_configure);
        constraintPlay = (ConstraintLayout) findViewById(R.id.linear_button_play);
        constraintReset = (ConstraintLayout) findViewById(R.id.linear_reset);
        constraintStop = (ConstraintLayout)findViewById(R.id.linear_button_stop);
        constraintRating = (ConstraintLayout) findViewById(R.id.linear_rating);
        constraintInfo = (ConstraintLayout) findViewById(R.id.constraint_info);
        ratingTimer = (RatingBar) findViewById(R.id.rating_cronometer);


        imgTestArrow = (ImageView)findViewById(R.id.img_test_arrow);
        imgAthleteArrow = (ImageView)findViewById(R.id.img_player_arrow);

        constraintStop.setOnClickListener(clickedStop);
        constraintPlay.setOnClickListener(clickPlay);
        constraintReset.setOnClickListener(clickReset);

        ratingTimer.setOnRatingBarChangeListener(ratingChanged);

        spinnerMinutes = (MaterialBetterSpinner) findViewById(R.id.spinner_minutes);
        spinnerSeconds = (MaterialBetterSpinner) findViewById(R.id.spinner_seconds);

        TextWatcher mask = MaskHeight.insert("###", editFirstResult);
        editFirstResult.addTextChangedListener(mask);

        timer.setTextView(textTimer);
        db = new DatabaseHelper(TimerActivity.this);
        db.openDataBase();

        setTimer();

        inflateSpinner();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            idAthlete = extras.getString("id_player");
            position = extras.getInt("position");
            verifyTest();
            db.openDataBase();
            Athletes athlete = db.getAthleteById(idAthlete);
            textNameTimer.setText(athlete.getName());
        }


    }

    private void verifyTest(){
        db = new DatabaseHelper(this);
        db.openDataBase();
        test = db.getTestFromAthleteAndType(idAthlete, AllActivities.testSelected);

        if(test!=null){
            TestTypes testTypes = db.getTestTypeFromId(AllActivities.testSelected);
            test.setType(testTypes.getValueType());
            existTest = true;
            String[] values = Services.returnValues(test.getValues());
            int count = values.length;
            ArrayList<Tests> tests = new ArrayList<>();
            for(int i=0;i<=count-1;i++){
                Tests test = new Tests();
                try {
                    if(!values[i].equals(""))
                        test.setFirstValue(Long.parseLong(values[i]));
                    else
                        test.setFirstValue(0);
                    test.setType(testTypes.getValueType());
                    test.setAthlete("Teste " + (i + 1));
                    tests.add(test);
                }catch (Exception e){
                    Log.i("Exception",e.getMessage());
                }
            }
            AdapterRecyclerTestsResults adapter = new AdapterRecyclerTestsResults(this, tests, values);
            recyclerResults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerResults.setVisibility(View.VISIBLE);
            recyclerResults.setAdapter(adapter);
        }

    }
    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            exitActivity();
        }
    };

    private void exitActivity(){
        if (init) {
            new MessageOptions(TimerActivity.this, this.getResources().getString(R.string.message), this.getResources().getString(R.string.want_leave_test), "exit");
        } else {
            finish();
        }
    }

    private View.OnClickListener clickedStop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pauseTimer();
        }
    };

    private RatingBar.OnRatingBarChangeListener ratingChanged = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            TextView textQualication = (TextView) findViewById(R.id.text_show_qualify_cronometer);
            textQualication.setText(Services.verifyQualification(ratingBar.getRating()));
            if(ratingBar.getRating()>0)
                textQualication.setVisibility(View.VISIBLE);
            else
                textQualication.setVisibility(View.GONE);
            ratingValue = ratingBar.getRating();
        }
    };

    private View.OnClickListener clickPlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playTimer();
        }
    };

    private View.OnClickListener clickReset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetTimer();
        }
    };

    private void playTimer(){
        if(init==false)
            setTimer();
        timer.initCount(TimerActivity.this);
        init=true;
        constraintStop.setVisibility(View.VISIBLE);
        constraintPlay.setVisibility(View.GONE);
        constraintReset.setVisibility(View.VISIBLE);
        linearSetting.setVisibility(View.GONE);
    }

    private void pauseTimer(){
        linearSetting.setVisibility(View.GONE);
        timer.pause();
        init=true;
        constraintStop.setVisibility(View.GONE);
        constraintPlay.setVisibility(View.VISIBLE);
        constraintReset.setVisibility(View.VISIBLE);
        btnSaveResults.setEnabled(true);
        btnSaveResults.setAlpha(1f);
    }

    private void resetTimer(){
        linearSetting.setVisibility(View.VISIBLE);
        timer.setStop();
        timer.setValue(minutes, seconds);
        init=false;
        constraintStop.setVisibility(View.GONE);
        constraintPlay.setVisibility(View.VISIBLE);
        constraintReset.setVisibility(View.GONE);
        textTimer.setText(SharedPreferencesAdapter.getTimerDefault(TimerActivity.this));
    }

    public static void alertFinishTimer(Activity act){
        ((TimerActivity) act).alertFinishTimer();
    }
    private void alertFinishTimer(){
        init=false;
        this.runOnUiThread(new Runnable() {
            public void run() {
                Services.buildNotificationCommon(TimerActivity.this);
                Services.messageAlert(TimerActivity.this, TimerActivity.this.getResources().getString(R.string.message),TimerActivity.this.getResources().getString(R.string.timer_completed),"timer");
                textTimer.setText(SharedPreferencesAdapter.getTimerDefault(TimerActivity.this));
                linearSetting.setVisibility(View.VISIBLE);
                Vibrator v = (Vibrator) TimerActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                btnSaveResults.setEnabled(true);
                btnSaveResults.setAlpha(1f);
                resetTimer();
            }
        });
    }

    private void inflateSpinner(){
        String [] adapter = new String[60];
        for (int i=0; i<=60-1;i++) {
            String value = "";
            if(i<=9)
                value = String.valueOf("0"+i);
            else
                value = String.valueOf(i);
            adapter[i] = value;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, adapter);
        spinnerMinutes.setAdapter(arrayAdapter);
        spinnerSeconds.setAdapter(arrayAdapter);
    }

    private View.OnClickListener configureTimer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfigureTimer();
        }
    };
    private void showConfigureTimer(){
        constraintConfigure.setVisibility(View.VISIBLE);
        spinnerMinutes.setText("00");
        spinnerSeconds.setText("00");
    }

    private View.OnClickListener clickCancelConfigure = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintConfigure.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener clickAlterTimer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alterTime();
        }
    };

    private void alterTime(){
        long min = Long.parseLong(spinnerMinutes.getText().toString());
        long sec = Long.parseLong(spinnerSeconds.getText().toString());
        if(min == 0 && sec == 00){
            Services.messageAlert(TimerActivity.this, this.getResources().getString(R.string.message),this.getResources().getString(R.string.invalid_time),"");
        }
        else{
            SharedPreferencesAdapter.setTimerDefault(TimerActivity.this, spinnerMinutes.getText().toString()+":"+spinnerSeconds.getText().toString());
            constraintConfigure.setVisibility(View.GONE);
            textTimer.setText(SharedPreferencesAdapter.getTimerDefault(TimerActivity.this));
            timer.setValue(min, sec);
        }
    }

    private View.OnClickListener clickSaveResults = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!editFirstResult.getText().toString().isEmpty())
                constraintRating.setVisibility(View.VISIBLE);
            else
                Services.messageAlert(TimerActivity.this,  TimerActivity.this.getResources().getString(R.string.message),  TimerActivity.this.getResources().getString(R.string.number_save), "");
        }
    };

    private View.OnClickListener saveTest= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if(ratingTimer.getRating()>0)
                saveResult();
        }
    };


    private void saveResult() {
        if (existTest) {
            String values = test.getValues() + String.valueOf(editFirstResult.getText().toString()) + "/";
            test.setValues(values);
            sync(test);
        } else {
            db.openDataBase();
            User user = db.getUser();
            Selective selective = db.getSelective();
            Tests test = new Tests(
                    " ",
                    AllActivities.testSelected,
                    idAthlete,
                    selective.getId(),
                    00,
                    00,
                    ratingTimer.getRating(),
                    " ",
                    user.getId(),
                    Services.convertBoolInInt(false),
                    true,
                    String.valueOf(editFirstResult.getText().toString()) + "/"
            );
            sync(test);
        }
    }
    private void sync(Tests test){
        if(Services.isOnline(this)) {
            constraintProgress.setVisibility(View.VISIBLE);
            if(existTest){
                String url = Constants.URL+Constants.API_TESTS+"/"+test.getId();
                PutTests put = new PutTests();
                put.setActivity(this);
                put.setObjPut(CreateJSON.createObject(test));
                put.execute(url);
            }
            else{
                String url = Constants.URL + Constants.API_TESTS;
                PostSync post = new PostSync();
                post.setActivity(this);
                post.setAll(false);
                post.setObjPut(CreateJSON.createObject(test));
                post.execute(url);
            }
        }
        else
            Services.messageAlert(this, this.getResources().getString(R.string.message), this.getResources().getString(R.string.sync_internet_connection),"");
    }

    public static void returnPostTest(Activity act, String resp, String result){
        ((TimerActivity)act).returnPostTest(resp, result);
    }

    private void returnPostTest(String resp, String result){
        constraintProgress.setVisibility(View.GONE);
        Log.i("returnPostTest", result);

        if(resp.equals("OK")) {
            DeserializerJsonElements des = new DeserializerJsonElements(result);
            Tests test = des.getTestObject();
            editFirstResult.setText("");
            DatabaseHelper db = new DatabaseHelper(this);
            if(existTest)
                db.updateTest(test);
            else
                db.addTest(test);
            existTest = true;
            this.test = test;
            verifyTest();
            AthletesActivity.adapterTests.notifyItemChanged(position);
            clear();
            Services.messageAlert(this,  this.getResources().getString(R.string.message), this.getResources().getString(R.string.results_saved),"DIALOGSAVERESULTS");
        }
    }

    public static void finished(Activity act){
        ((TimerActivity)act).finish();
    }

    private void clear(){
        editFirstResult.setText("");
        resetTimer();
        btnSaveResults.setAlpha(0.5f);
        btnSaveResults.setEnabled(false);
        constraintRating.setVisibility(View.GONE);
        ratingTimer.setRating(0);

    }

    public static void returnOption (Activity act, String method){
        ((TimerActivity)act).returnOption(method);
    }

    private void returnOption(String whoCalled){
        if(whoCalled.equals("save")){
            AthletesActivity.adapterTests.notifyItemChanged(position);
            timer.setStop();
            this.finish();
        }

        else if(whoCalled.equals("exit")){
            timer.setStop();
            finish();
        }
    }

    private void setTimer(){
        String time = "";
        try{
            time = SharedPreferencesAdapter.getTimerDefault(TimerActivity.this);
            if(time==null || time.equals("")){
                SharedPreferencesAdapter.setTimerDefault(TimerActivity.this, "01:00");
                time="01:00";
            }
        }catch (Exception e){
            SharedPreferencesAdapter.setTimerDefault(TimerActivity.this, "01:00");
            time="01:00";
        }
        try {
            minutes = Long.parseLong(time.substring(0, 2));
            seconds = Long.parseLong(time.substring(3));
        }catch (Exception e){
            minutes = 01;
            seconds= 00;
        }
        textTimer.setText(time);
        timer.setValue(minutes, seconds);
    }

    private void showInfo(){
        constraintInfo.setVisibility(View.VISIBLE);
        DatabaseHelper db  = new DatabaseHelper(TimerActivity.this);
        db.openDataBase();
        TestTypes test = db.getTestTypeFromId(AllActivities.testSelected);
        if(test!=null) {
            textNameTestInfo.setText(test.getName());
            webview.loadData(Services.convertHtml(test.getDescription(), test.getTutorialImageURL()), "text/html; charset=utf-8", "UTF-8");
        }

        Athletes athlete = db.getAthleteById(idAthlete);

        if(athlete!=null){
            Positions positiom = db.getPositiomById(athlete.getDesirablePosition());
            String pos = "";
            if(positiom!=null){
                pos = positiom.getNAME();
            }
            textNamePlayerInfo.setText(athlete.getName());
            TextDetailsNameInfo.setText(this.getResources().getString(R.string.nascimento) + ": "+ Services.convertDate(athlete.getBirthday())+ "\n"+
                    this.getResources().getString(R.string.cpf) + ": "+athlete.getCPF() +"\n"+
                    this.getResources().getString(R.string.address) + ": "+athlete.getAddress() +"\n"+
                    this.getResources().getString(R.string.position_desired) + ": "+pos +"\n"+
                    this.getResources().getString(R.string.altura) + ": "+String.format("%.2f", athlete.getHeight()).replace(".",",") +"\n"+
                    this.getResources().getString(R.string.peso) + ": "+String.format("%.0f",athlete.getWeight()).replace(".",",")+" Kg");
        }

        btnCloseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintInfo.setVisibility(View.GONE);
            }
        });

        imgTestArrow.setOnClickListener(clickedImgArrowTest);
        imgAthleteArrow.setOnClickListener(clickedImgArrowPlayer);
    }

    private View.OnClickListener clickedImgArrowTest = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if(arrowDownTest)
            {
                arrowDownTest=false;
                imgTestArrow.setImageDrawable(getDrawable(R.drawable.arrow_top_white));
                webview.setVisibility(View.VISIBLE);
            }
            else{
                arrowDownTest=true;
                imgTestArrow.setImageDrawable(getDrawable(R.drawable.arrow_down_white));
                webview.setVisibility(View.GONE);
            }
        }
    };

    private View.OnClickListener clickedImgArrowPlayer = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if(arrowDownPlayer)
            {
                arrowDownPlayer=false;
                imgAthleteArrow.setImageDrawable(getDrawable(R.drawable.arrow_top_white));
                TextDetailsNameInfo.setVisibility(View.VISIBLE);
            }
            else{
                arrowDownPlayer = true;
                imgAthleteArrow.setImageDrawable(getDrawable(R.drawable.arrow_down_white));
                TextDetailsNameInfo.setVisibility(View.GONE);
            }

        }
    };
}
