package br.com.john.combinebrasil;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import br.com.john.combinebrasil.Services.CountDownTimer;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.MessageOptions;
import br.com.john.combinebrasil.Services.Services;

public class CronometerOnlyOneActivity extends AppCompatActivity {
    private final CountDownTimer countDownTimer = new CountDownTimer();
    Toolbar toolbar;
    DatabaseHelper db;
    RecyclerView recyclerTest;
    ConstraintLayout constraintInfo, constraintVisibilityReset, constraintVisibilityPlay, constraintVisibilityStop,
            constraintReset, constraintPlay, constraintStop, constraintRating,constraintProgress;
    Button btnSave, btnCancel, btnReady;
    RatingBar ratingBar;
    ImageView imgIconButtonPlay, imgTestArrow, imgAthleteArrow, imgDelete;
    TextView textNamePlayer, textInfoNameTest, textInfoNameAthlete, textInfoDetailsAthlete,
            txtRating, textProgress,textCronometer,textShowQualify;
    private boolean init = false, isPause=false, arrowDownTest=false, arrowDownPlayer=false, existTest=false;
    String idAthlete = "", nameTest="";
    WebView webview;
    int position = 0;
    private float ratingValue = 0;
    AdapterRecyclerTestsResults adapter;
    Tests test;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cronometer_only_one);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);

        ImageView imgInfo = (ImageView) findViewById(R.id.img_create_account);
        imgInfo.setImageDrawable(CronometerOnlyOneActivity.this.getDrawable(R.drawable.ic_info));
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo();
            }
        });

        LinearLayout deleteTest = (LinearLayout) findViewById(R.id.linear_delete);
        deleteTest.setVisibility(View.GONE);

        constraintInfo = (ConstraintLayout) findViewById(R.id.constraint_info);

        constraintVisibilityReset = (ConstraintLayout)findViewById(R.id.linear_visibility_reset);
        constraintVisibilityPlay = (ConstraintLayout) findViewById(R.id.linear_button_play);
        constraintVisibilityStop = (ConstraintLayout) findViewById(R.id.linear_button_stop);
        constraintReset = (ConstraintLayout)findViewById(R.id.constraint_reset);
        constraintPlay = (ConstraintLayout)findViewById(R.id.constraint_play);
        constraintStop = (ConstraintLayout)findViewById(R.id.constraint_stop);
        constraintRating = (ConstraintLayout) findViewById(R.id.linear_rating_cronometer);
        constraintProgress = (ConstraintLayout) findViewById(R.id.linear_progress);

        textNamePlayer = (TextView) findViewById(R.id.text_name_player_cronometer);
        textInfoNameTest = (TextView) findViewById(R.id.text_info_name_test);
        textInfoNameAthlete = (TextView) findViewById(R.id.text_info_name_athlete);
        textInfoDetailsAthlete = (TextView) findViewById(R.id.text_info_details_athlete);
        webview = (WebView) findViewById(R.id.webview);

        imgIconButtonPlay = (ImageView) findViewById(R.id.image_icon_button_play);
        imgTestArrow = (ImageView)findViewById(R.id.img_test_arrow);
        imgAthleteArrow = (ImageView)findViewById(R.id.img_player_arrow);
        imgDelete = (ImageView) findViewById(R.id.img_delete);

        textCronometer = (TextView) findViewById(R.id.text_cronometer);
        textShowQualify = (TextView) findViewById(R.id.text_show_qualify_cronometer);

        txtRating = (TextView) findViewById(R.id.txt_rating_done);
        textProgress = (TextView) findViewById(R.id.text_progress);

        btnSave = (Button) findViewById(R.id.button_save_results);
        btnReady = (Button) findViewById(R.id.button_ready_cronometer);
        btnCancel = (Button) findViewById(R.id.button_cancel);

        ratingBar = (RatingBar) findViewById(R.id.rating_cronometer);

        recyclerTest =(RecyclerView) findViewById(R.id.recycler_tests);
        btnSave.setOnClickListener(clickedSave);
        btnCancel.setOnClickListener(clickCancel);

        countDownTimer.setTextView(textCronometer);
        countDownTimer.setButton(btnSave);

        constraintStop.setOnClickListener(clickedPause);
        constraintReset.setOnClickListener(btnResetClickListener);
        constraintPlay.setOnClickListener(clickedPlayAndStop);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            idAthlete = extras.getString("id_player");
            position = extras.getInt("position");
            db = new DatabaseHelper(this);
            db.openDataBase();
            Athletes athlete = db.getAthleteById(idAthlete);
            textNamePlayer.setText(athlete.getName());

            verifyTests();
        }

        enabledButtonAdd(false);
    }

    private void verifyTests(){
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
            adapter = new AdapterRecyclerTestsResults(this, tests, values);
            recyclerTest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerTest.setVisibility(View.VISIBLE);
            recyclerTest.setAdapter(adapter);
        }
    }

    private void exitActivity() {
        if (init) {
            messageOption(CronometerOnlyOneActivity.this.getResources().getString(R.string.exit), CronometerOnlyOneActivity.this.getResources().getString(R.string.quit_test),"exitCronometer");
        } else {
            if (countDownTimer.getPlay())
                stopCronometer();
            finish();
        }
    }

    public static void finished(Activity act){
        ((CronometerOnlyOneActivity)act).finish();
    }

    private void showInfo(){
        constraintInfo.setVisibility(View.VISIBLE);
        DatabaseHelper db  = new DatabaseHelper(CronometerOnlyOneActivity.this);
        db.openDataBase();
        TestTypes test = db.getTestTypeFromId(AllActivities.testSelected);
        if(test!=null) {
            textInfoNameTest.setText(test.getName());
            webview.loadData(Services.convertHtml(test.getDescription(), test.getTutorialImageURL()), "text/html; charset=utf-8", "UTF-8");
        }

        Athletes athlete = db.getAthleteById(idAthlete);

        if(athlete!=null){
            Positions positiom = db.getPositiomById(athlete.getDesirablePosition());
            String pos = "";
            if(positiom!=null){
                pos = positiom.getNAME();
            }
            textInfoNameAthlete.setText(athlete.getName());
            textInfoDetailsAthlete.setText(CronometerOnlyOneActivity.this.getResources().getString(R.string.nascimento)+ " "+ Services.convertDate(athlete.getBirthday())+ "\n"+
                    CronometerOnlyOneActivity.this.getResources().getString(R.string.cpf) +" "+athlete.getCPF() +"\n"+
                    CronometerOnlyOneActivity.this.getResources().getString(R.string.address) +" "+athlete.getAddress() +"\n"+
                    CronometerOnlyOneActivity.this.getResources().getString(R.string.position) +" "+pos +"\n"+
                    CronometerOnlyOneActivity.this.getResources().getString(R.string.altura) +" "+String.format("%.2f", athlete.getHeight()).replace(".",",") +"\n"+
                    CronometerOnlyOneActivity.this.getResources().getString(R.string.peso) +" "+String.format("%.0f",athlete.getWeight()).replace(".",",")+" Kg");
        }

        Button btnClose = (Button) findViewById(R.id.button_close_info);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintInfo.setVisibility(View.GONE);
            }
        });

        imgTestArrow.setOnClickListener(clickedImgArrowTest);
        imgAthleteArrow.setOnClickListener(clickedImgArrowPlayer);
    }


    private void messageOption(String title, String message, String method){
        pauseCronometer();
        new MessageOptions(CronometerOnlyOneActivity.this, title, message, method);
    }

     /*
    ************************** MÉTODOS DO CRONÔMETRO ***********************************
    */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void playCronometer(){
        init=true;
        isPause=false;
        countDownTimer.initCount();
        constraintVisibilityStop.setVisibility(View.VISIBLE);
        constraintVisibilityPlay.setVisibility(View.GONE);
        constraintVisibilityReset.setVisibility(View.VISIBLE);

        enabledButtonAdd(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopCronometer(){
        countDownTimer.stop();
        init=false;

        constraintVisibilityStop.setVisibility(View.GONE);
        constraintVisibilityPlay.setVisibility(View.VISIBLE);
        constraintVisibilityReset.setVisibility(View.VISIBLE);
        textCronometer.setText("00:00");
        enabledButtonAdd(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void pauseCronometer(){
        enabledButtonAdd(true);
        isPause=true;
        countDownTimer.pause();
        constraintVisibilityStop.setVisibility(View.GONE);
        constraintVisibilityPlay.setVisibility(View.VISIBLE);
        constraintVisibilityReset.setVisibility(View.VISIBLE);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void resetCrometer(){
        if(init) {
            isPause = true;
            init = false;
            countDownTimer.stop();

            constraintVisibilityStop.setVisibility(View.GONE);
            constraintVisibilityPlay.setVisibility(View.VISIBLE);
            constraintVisibilityReset.setVisibility(View.VISIBLE);

            textCronometer.setText("00:00");
            enabledButtonAdd(false);
        }
    }

    private void checkAndSaveRun(){
        showRating();
    }

    private void showRating(){
        pauseCronometer();
        constraintRating.setVisibility(View.VISIBLE);
        btnReady.setEnabled(false);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating > 0.0) {
                    btnReady.setEnabled(true);
                    btnReady.setAlpha(1f);
                    textShowQualify.setVisibility(View.VISIBLE);
                }
                else {
                    btnReady.setEnabled(false);
                    btnReady.setAlpha(.5f);
                }
                textShowQualify.setText(Services.verifyQualification(rating));
            }
        });

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CronometerOnlyOneActivity.this,
                        String.valueOf(Services.verifyQualification(ratingBar.getRating())),
                        Toast.LENGTH_SHORT).show();
                ratingValue = ratingBar.getRating();
                saveTest();
                constraintRating.setVisibility(View.GONE);
            }
        });
    }

    private void saveTest(){
        if(existTest){
            String values = test.getValues()+ String.valueOf(Services.convertInMilliSeconds(textCronometer.getText().toString()))+"/";
            test.setValues(values);
            sync(test);
        }
        else{
            db.openDataBase();
            User user= db.getUser();
            Selective selective = db.getSelective();
            Tests test = new Tests(
                    " ",
                    AllActivities.testSelected,
                    idAthlete,
                    selective.getId(),
                    00,
                    00,
                    ratingValue,
                    " ",
                    user.getId(),
                    Services.convertBoolInInt(false),
                    false,
                    String.valueOf(Services.convertInMilliSeconds(textCronometer.getText().toString()))+"/"
            );
            sync(test);
        }


    }

    private void sync(Tests test){
        if(Services.isOnline(CronometerOnlyOneActivity.this)) {
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
                post.setActivity(CronometerOnlyOneActivity.this);
                post.setAll(false);
                post.setObjPut(CreateJSON.createObject(test));
                post.execute(url);
            }
        }
        else
            Services.messageAlert(CronometerOnlyOneActivity.this, CronometerOnlyOneActivity.this.getResources().getString(R.string.warning),CronometerOnlyOneActivity.this.getResources().getString(R.string.sync_internet_connection),"");
    }

    public static void returnPostTest(Activity act, String resp, String result){
        ((CronometerOnlyOneActivity)act).returnPostTest(resp, result);
    }

    private void returnPostTest(String resp, String result){
        constraintProgress.setVisibility(View.GONE);

        Log.i("ERROR", result);

        if(resp.equals("OK")) {
            DeserializerJsonElements des = new DeserializerJsonElements(result);
            Tests test = des.getTestObject();
            countDownTimer.stop();
            DatabaseHelper db = new DatabaseHelper(CronometerOnlyOneActivity.this);
            if(existTest)
                db.updateTest(test);
            else
                db.addTest(test);
            existTest = true;
            this.test = test;
            verifyTests();
            AthletesActivity.adapterTests.notifyItemChanged(position);
            Services.messageAlert(CronometerOnlyOneActivity.this, CronometerOnlyOneActivity.this.getResources().getString(R.string.message), CronometerOnlyOneActivity.this.getResources().getString(R.string.results_saved),"DIALOGSAVECRONOMETER");
        }
    }


    /*********CLICKED*********************/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void clickePause(){
        if(!isPause){
            pauseCronometer();
        }
    }

    private View.OnClickListener clickedPlayAndStop = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            constraintVisibilityStop.setVisibility(View.VISIBLE);
            constraintVisibilityPlay.setVisibility(View.GONE);
            constraintVisibilityReset.setVisibility(View.VISIBLE);

            if(init==false){
                playCronometer();
            }
            else {
                if(!isPause) {
                    stopCronometer();

                }
                else
                    playCronometer();
            }
        }
    };

    private View.OnClickListener clickedSaveTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkAndSaveRun();
        }
    };
    private View.OnClickListener clickedPause = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickePause();
        }
    };

    private View.OnClickListener clickedSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkAndSaveRun();
        }
    };

    private View.OnClickListener clickCancel = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            constraintRating.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener btnResetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetCrometer();
        }
    };

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            exitActivity();
        }
    };
    @Override
    public void onBackPressed(){
        exitActivity();
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

    private void enabledButtonAdd(boolean enabled){
        if(enabled) {
            btnSave.setEnabled(true);
            btnSave.setAlpha(1f);
        }else {
            btnSave.setEnabled(false);
            btnSave.setAlpha(.5f);
        }
    }

    private View.OnClickListener clickedImgArrowPlayer = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if(arrowDownPlayer)
            {
                arrowDownPlayer=false;
                imgAthleteArrow.setImageDrawable(getDrawable(R.drawable.arrow_top_white));
                textInfoDetailsAthlete.setVisibility(View.VISIBLE);
            }
            else{
                arrowDownPlayer = true;
                imgAthleteArrow.setImageDrawable(getDrawable(R.drawable.arrow_down_white));
                textInfoDetailsAthlete.setVisibility(View.GONE);
            }
        }
    };

    public static void getMethodOutActivity(Activity act, String method){
        ((CronometerOnlyOneActivity)act).calledFunctions(method);
    }

    private void calledFunctions(String method){
        if(method.equals("exitCronometer")){
            try {
                stopCronometer();
            }catch(Exception e){}
            finish();
        }
        else if(method.equals("saveResult")){
            checkAndSaveRun();
        }
        else if(method.equals("resetCronometer")){
            resetCrometer();
        }
        else if(method.equals("saveAllResults")){
            if(nameTest.toString().toLowerCase().equals("sprint 40 jardas")|nameTest.toString().toLowerCase().equals("sprint 40")){
                saveTest();
                constraintProgress.setVisibility(View.GONE);
            }else
                showRating();
        }
        else if(method.equals("deleteCronometer")){
            deleteTest();
        }
    }
    private void deleteTest(){
        Tests test = db.getTestFromAthleteAndType(idAthlete, AllActivities.testSelected);
        db.openDataBase();
        db.deleteValue(Constants.TABLE_TESTS, test.getId());
        Services.messageAlert(CronometerOnlyOneActivity.this, CronometerOnlyOneActivity.this.getResources().getString(R.string.message),CronometerOnlyOneActivity.this.getResources().getString(R.string.test_deleted),"");
        //verifyTest();
    }

}