package br.com.john.combinebrasil;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTestsResults;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.Results;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Connection.Connection;
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

public class ResultsOnlyOneActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgTestArrow, imgAthleteArrow;
    Button buttonAdd, btnReady, buttonWingspan;
    EditText editFirstResult, editWingspan;
    int position=0;
    TextView textNamePlayer, textNameTest, textNameTestDetails,  textNamePlayerDetails,
            textDetailsPlayer, textShowRating;
    boolean arrowDownTest=true,arrowDownPlayer=true;
    ConstraintLayout linearRating, linearWingSpan, linearProgress, linearNoConnection;
    RatingBar ratingBar;
    String idAthlete = "", wingspan=" ";
    float ratingValue = 0;
    boolean existTest = false;
    Tests test;
    TextView txtNameResult, txtRating;
    Button buttonBack;
    DatabaseHelper db;
    WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_only_one);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        ImageView imgInfo = (ImageView) findViewById(R.id.img_create_account);
        imgInfo.setVisibility(View.VISIBLE);
        imgInfo.setImageDrawable(ResultsOnlyOneActivity.this.getDrawable(R.drawable.ic_info));
        imgInfo.setOnClickListener(showInfoAthlete);

        LinearLayout deleteTest = (LinearLayout) findViewById(R.id.linear_delete);
        deleteTest.setVisibility(View.GONE);

        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        db = new DatabaseHelper(ResultsOnlyOneActivity.this);
        TestTypes test = db.getTestTypeFromId(AllActivities.testSelected);
        textTitle.setText(test.getName());

        linearProgress = (ConstraintLayout) findViewById(R.id.linear_progress);
        txtNameResult = (TextView) findViewById(R.id.txt_name_result);
        buttonBack = (Button) findViewById(R.id.button_back);
        txtRating = (TextView) findViewById(R.id.txt_rating_done);

        editWingspan = (EditText) findViewById(R.id.edit_wingspan);
        buttonWingspan = (Button) findViewById(R.id.button_wingspan);
        buttonWingspan.setOnClickListener(clickedWingspan);
        linearWingSpan = (ConstraintLayout) findViewById(R.id.constraint_wingspan);

        linearNoConnection = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        linearNoConnection.setOnClickListener(clickVerifyConnection);

        linearRating = (ConstraintLayout) findViewById(R.id.linear_rating);

        editFirstResult = (EditText) findViewById(R.id.edit_first_result);
        buttonAdd = (Button) findViewById(R.id.button_add_results);
        btnReady = (Button) findViewById(R.id.button_ready_results);

        textNamePlayer = (TextView) findViewById(R.id.text_name_player_result);
        textNameTest = (TextView) findViewById(R.id.text_name_test_result);

        textNameTestDetails = (TextView) findViewById(R.id.text_info_name_test);
        webView = (WebView) findViewById(R.id.webview);
        textNamePlayerDetails = (TextView) findViewById(R.id.text_info_name_athlete);
        textDetailsPlayer = (TextView) findViewById(R.id.text_info_details_athlete);
        textShowRating = (TextView) findViewById(R.id.text_show_qualify_cronometer);
        ratingBar = (RatingBar) findViewById(R.id.rating_cronometer);

        imgTestArrow = (ImageView) findViewById(R.id.img_test_arrow) ;
        imgAthleteArrow = (ImageView) findViewById(R.id.img_player_arrow);

        buttonAdd.setOnClickListener(clickSave);
        btnReady.setOnClickListener(clickSaveResult);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            idAthlete = extras.getString("id_player");
            position = extras.getInt("position");
            db = new DatabaseHelper(this);
            db.openDataBase();
            Athletes athlete = db.getAthleteById(idAthlete);
            textNamePlayer.setText(athlete.getName());

            TestTypes testypes = db.getTestTypeFromId(AllActivities.testSelected);
            textNameTest.setText(testypes.getName());
            verifyTest();
        }

        TextWatcher mask = MaskHeight.insert("#,##", editWingspan);
        editWingspan.addTextChangedListener(mask);
        mask = MaskHeight.insert("#,##", editFirstResult);
        editFirstResult.addTextChangedListener(mask);
        editFirstResult.addTextChangedListener(textEditFirst);
        enabledButtonAdd(false);
    }

    private TextWatcher textEditFirst =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>2)
                enabledButtonAdd(true);
            else
                enabledButtonAdd(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
                try {
                    Tests test = new Tests();
                    if(!values[i].equals(""))
                        test.setFirstValue(Long.parseLong(values[i]));
                    else
                        test.setFirstValue(0);
                    test.setType(testTypes.getValueType());
                    test.setAthlete("Teste " + (i + 1));
                    tests.add(test);
                }catch(Exception e){
                    Log.i("Exception ",e.getMessage());
                }
            }
            AdapterRecyclerTestsResults adapter = new AdapterRecyclerTestsResults(this, tests, values);
            RecyclerView recyclerTest = (RecyclerView) findViewById(R.id.recycler_results_tests);
            recyclerTest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerTest.setVisibility(View.VISIBLE);
            recyclerTest.setAdapter(adapter);
        }
        else{
            TestTypes testypes = db.getTestTypeFromId(AllActivities.testSelected);

            if (testypes.getName().toLowerCase().equals("salto vertical")) {
                linearWingSpan.setVisibility(View.VISIBLE);
            }
        }
    }

    private View.OnClickListener showInfoAthlete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConstraintLayout constraintInfo = (ConstraintLayout) findViewById(R.id.constraint_info);
            constraintInfo.setVisibility(View.VISIBLE);
            DatabaseHelper db  = new DatabaseHelper(ResultsOnlyOneActivity.this);
            db.openDataBase();
            TestTypes test = db.getTestTypeFromId(AllActivities.testSelected);
            Athletes athlete = db.getAthleteById(idAthlete);

            if(test!=null) {
                textNameTest.setText(test.getName());
                textNameTestDetails.setText(test.getName());
                webView.loadData(Services.convertHtml(test.getDescription(), test.getTutorialImageURL()), "text/html; charset=utf-8", "UTF-8");
            }

            if(athlete!=null){
                Positions positiom = db.getPositiomById(athlete.getDesirablePosition());
                String pos = "";
                if(positiom!=null){
                    pos = positiom.getNAME();
                }
                textNamePlayerDetails.setText(athlete.getName());
                textDetailsPlayer.setText(ResultsOnlyOneActivity.this.getResources().getString(R.string.nascimento) +": "+ Services.convertDate(athlete.getBirthday())+ "\n"+
                        ResultsOnlyOneActivity.this.getResources().getString(R.string.cpf) +": "+athlete.getCPF() +"\n"+
                        ResultsOnlyOneActivity.this.getResources().getString(R.string.address) +": "+athlete.getAddress() +"\n"+
                        ResultsOnlyOneActivity.this.getResources().getString(R.string.position_desired) +": "+pos +"\n"+
                        ResultsOnlyOneActivity.this.getResources().getString(R.string.altura) +": "+String.format("%.2f", athlete.getHeight()).replace(".",",") +"\n"+
                        ResultsOnlyOneActivity.this.getResources().getString(R.string.peso) +": "+String.format("%.0f",athlete.getWeight()).replace(".",",")+" Kg");
            }

            Button btnClose = (Button) findViewById(R.id.button_close_info);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConstraintLayout constraintInfo = (ConstraintLayout) findViewById(R.id.constraint_info);
                    constraintInfo.setVisibility(View.GONE);
                }
            });

            imgTestArrow.setOnClickListener(clickedImgArrowTest);
            imgAthleteArrow.setOnClickListener(clickedImgArrowPlayer);
        }
    };


    private View.OnClickListener clickedImgArrowTest = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if(arrowDownTest)
            {
                arrowDownTest=false;
                imgTestArrow.setImageDrawable(getDrawable(R.drawable.arrow_top_white));
                webView.setVisibility(View.VISIBLE);
            }
            else{
                arrowDownTest=true;
                imgTestArrow.setImageDrawable(getDrawable(R.drawable.arrow_down_white));
                webView.setVisibility(View.GONE);
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
                textDetailsPlayer.setVisibility(View.VISIBLE);
            }
            else{
                arrowDownPlayer = true;
                imgAthleteArrow.setImageDrawable(getDrawable(R.drawable.arrow_down_white));
                textDetailsPlayer.setVisibility(View.GONE);
            }

        }
    };

    public static void getMethodOutResultsActivity(Activity act, String method){
        ((ResultsOnlyOneActivity)act).calledMethod(method);
    }

    private void calledMethod(String method){
        if(method.equals("checkAndSaveResults")){
            checkAndSaveResults();
        }
        else if(method.equals("saveAllResults")){
            updateTest();
            Services.messageAlert(ResultsOnlyOneActivity.this, this.getResources().getString(R.string.message), this.getResources().getString(R.string.results_saved), "DialogSaveResults");
        }
        else if(method.equals("exitActivity")){
            finish();
        }
        else if(method.equals("deleteResults")){
            deleteTest();
        }
    }

    private void deleteTest(){
        if(Services.isOnline(ResultsOnlyOneActivity.this)) {
            Tests test = db.getTestFromAthleteAndType(idAthlete, AllActivities.testSelected);
            db.openDataBase();
            db.deleteValue(Constants.TABLE_TESTS, test.getId());
            Services.messageAlert(ResultsOnlyOneActivity.this, this.getResources().getString(R.string.message), this.getResources().getString(R.string.test_deleted), "");
            verifyTest();
        }
    }

    private void saveTest(){
        if(existTest){
            String value = editFirstResult.getText().toString();
            String values = test.getValues()+ String.valueOf(Services.convertMetersinCentimeters(value))+"/";
            test.setValues(values);
            sync(test);
        }
        else {
            db.openDataBase();
            User user = db.getUser();
            Selective selective = db.getSelective();
            Tests test = new Tests(
                    " ",
                    AllActivities.testSelected,
                    idAthlete,
                    selective.getId(),
                    0,
                    0,
                    ratingValue,
                    String.valueOf(editWingspan.getText().toString().isEmpty()?0:Services.convertMetersinCentimeters(editWingspan.getText().toString())),
                    user.getId(),
                    Services.convertBoolInInt(false),
                    false,
                    String.valueOf(Services.convertMetersinCentimeters(editFirstResult.getText().toString())));
            sync(test);
        }
    }

    private void sync(Tests test){
        if(Services.isOnline(ResultsOnlyOneActivity.this)) {
            linearProgress.setVisibility(View.VISIBLE);
            if(existTest){
                String url = Constants.URL+Constants.API_TESTS+"/"+test.getId();
                PutTests put = new PutTests();
                put.setActivity(this);
                put.setObjPut(CreateJSON.createObject(test));
                put.execute(url);
            }
            else {
                db = new DatabaseHelper(ResultsOnlyOneActivity.this);
                String url = Constants.URL + Constants.API_TESTS;
                PostSync post = new PostSync();
                post.setActivity(ResultsOnlyOneActivity.this);
                post.setAll(false);
                post.setObjPut(CreateJSON.createObject(test));
                post.execute(url);
            }
        }
        else
            Services.messageAlert(ResultsOnlyOneActivity.this, this.getResources().getString(R.string.message),  this.getResources().getString(R.string.sync_internet_connection),"");
    }

    public static void returnPostTest(Activity act, String resp, String result){
        ((ResultsOnlyOneActivity)act).returnPostTest(resp, result);
    }

    private void returnPostTest(String resp, String result){
        linearProgress.setVisibility(View.GONE);
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
            Services.messageAlert(this,  this.getResources().getString(R.string.message), this.getResources().getString(R.string.results_saved),"DIALOGSAVERESULTS");
        }
    }

     public static void returnUpdateSync(Activity act, String response){
        ((ResultsOnlyOneActivity)act).returnUpdateSync(response);
    }

    private void returnUpdateSync(String response) {
        linearProgress.setVisibility(View.GONE);
        if (!response.equals("[]")) {
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            Tests test = des.getTestObjectTest();
            if (test != null) {
                updateTest(test);
            } else
                Services.messageAlert(ResultsOnlyOneActivity.this, this.getResources().getString(R.string.message),  this.getResources().getString(R.string.erro_sync_tests), "");
        }
        else
            Services.messageAlert(ResultsOnlyOneActivity.this, this.getResources().getString(R.string.message), this.getResources().getString(R.string.erro_sync_tests), "");
    }

    private void updateTest(Tests test){
        DatabaseHelper db = new DatabaseHelper(ResultsOnlyOneActivity.this);
        db.updateTest(test);
        try {
            AthletesActivity.adapterTests.notifyItemChanged(position);
        }catch (Exception e){
            e.printStackTrace();
        }

        Services.messageAlert(ResultsOnlyOneActivity.this, this.getResources().getString(R.string.message),this.getResources().getString(R.string.results_saved),"DIALOGSAVERESULTS");
    }

    private void updateTest(){
        db.openDataBase();
        Tests test = db.getTestFromAthleteAndType(idAthlete, AllActivities.testSelected);
        AthletesActivity.adapterTests.notifyItemChanged(position);
    }

    private void checkAndSaveResults (){
        linearRating.setVisibility(View.GONE);
            saveTest();
    }

    private void enabledButtonAdd(boolean enabled){
        if(enabled) {
            buttonAdd.setEnabled(true);
            buttonAdd.setAlpha(1f);
        }else {
            buttonAdd.setEnabled(false);
            buttonAdd.setAlpha(.5f);
        }
    }

    private void messageOption(String title, String message, String method){
        new MessageOptions(ResultsOnlyOneActivity.this, title, message, method);
    }

    @Override
    public void onBackPressed(){
        exitActivity();
    }

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            exitActivity();
        }
    };

    private void exitActivity(){
        finish();
    }

    public View.OnClickListener clickSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int first = Integer.parseInt(editFirstResult.getText().toString().substring(0,1));
            if(first>=8){
                Services.changeColorEditBorderError(editFirstResult, ResultsOnlyOneActivity.this);
                Services.messageAlert(ResultsOnlyOneActivity.this, ResultsOnlyOneActivity.this.getResources().getString(R.string.message), ResultsOnlyOneActivity.this.getResources().getString(R.string.jump_too_high),"");

            }
            else
                linearRating.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener clickSaveResult = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            messageOption(ResultsOnlyOneActivity.this.getResources().getString(R.string.save), ResultsOnlyOneActivity.this.getResources().getString(R.string.save_results), "checkAndSaveResults");
        }
    };

    public static void finished(Activity act){
        ((ResultsOnlyOneActivity)act).finish();
    }

    private View.OnClickListener clickedWingspan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView extError = (TextView) findViewById(R.id.text_error_wingspan);
            if (editWingspan.getText().toString().trim().length()>3){
                wingspan = editWingspan.getText().toString();
                linearWingSpan.setVisibility(View.GONE);
                extError.setVisibility(View.INVISIBLE);
            }
            else {
                extError.setVisibility(View.VISIBLE);
                Services.changeColorEditBorderError(editWingspan, ResultsOnlyOneActivity.this);
            }
        }
    };

    private View.OnClickListener clickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MessageOptions(ResultsOnlyOneActivity.this, ResultsOnlyOneActivity.this.getResources().getString(R.string.delete), ResultsOnlyOneActivity.this.getResources().getString(R.string.delete_test_current), "deleteResults");
        }
    };

    private View.OnClickListener clickVerifyConnection = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            verifyTest();
        }
    };
}