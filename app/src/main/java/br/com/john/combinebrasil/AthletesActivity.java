package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerAthletes;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.SelectiveAthletes;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAsyncTask;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.MessageOptions;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

public class AthletesActivity extends AppCompatActivity {
    public static RecyclerView listViewPlayers;
    public static AdapterRecyclerAthletes adapterTests;
    public LinearLayoutManager mLayoutManager;
    Toolbar toolbar;
    ArrayList<Athletes> athletesArrayList;
    private static Context myContext;
    private TextView textOptionName, textOptionCode, textProgress;
    private EditText editSearch;
    private ImageView imgOrder;
    private Button btnCancel;
    private LinearLayout linearOrder;
    ConstraintLayout linearNotSearch, linearProgress, constraintNotConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athletes);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);

        listViewPlayers = (RecyclerView) findViewById(R.id.list_players);
        listViewPlayers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        linearOrder = (LinearLayout) findViewById(R.id.linear_order_by);
        linearNotSearch = (ConstraintLayout) findViewById(R.id.linear_search_null);
        linearProgress = (ConstraintLayout) findViewById(R.id.linear_progress);
        constraintNotConnect = (ConstraintLayout) findViewById(R.id.constraint_not_connection);

        editSearch = (EditText) findViewById(R.id.edit_search);
        textOptionName = (TextView) findViewById(R.id.text_option_order_name);
        textOptionCode = (TextView) findViewById(R.id.text_option_order_code);
        textProgress = (TextView) findViewById(R.id.text_progress);

        btnCancel= (Button) findViewById(R.id.btn_cancel_order);

        try {
            TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
            DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
            db.getTestTypeFromId(AllActivities.testSelected).getName();
            textTitle.setText(db.getTestTypeFromId(AllActivities.testSelected).getName());
        }catch(Exception e){}

        imgOrder = (ImageView) findViewById(R.id.img_order);

        imgOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearOrder.setVisibility(View.VISIBLE);
            }
        });
        linearOrder.setOnClickListener(hideOptionsOrder);

        myContext = AthletesActivity.this;

        hideKeyboard();

        textOptionCode.setOnClickListener(clickedOrderCode);
        textOptionName.setOnClickListener(clickedOrderName);
        btnCancel.setOnClickListener(hideOptionsOrder);
        linearNotSearch.setOnClickListener(clickedUpdateAthletes);

        editSearch.setOnTouchListener(editTouch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                    searchPlayer(s.toString());
                else {
                    callInflateAthletes();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard();
                }
            }
        });

        getTests();
    }

    private void getTests(){
        if(Services.isOnline(AthletesActivity.this)) {
            DatabaseHelper db = new DatabaseHelper(this);
            db.openDataBase();
            String shared = "Date "+db.getTestTypeFromId(AllActivities.testSelected).getName();
            String updateDate = SharedPreferencesAdapter.getValueStringSharedPreferences(this, shared);
            if(updateDate.equals(""))
                getTeamsInit();
            else
                getUpdateTeams(updateDate);
        }
        else
            constraintNotConnect.setVisibility(View.VISIBLE);
    }

    private void getTeamsInit(){
        constraintNotConnect.setVisibility(View.GONE);
        linearProgress.setVisibility(View.VISIBLE);
        textProgress.setText(AthletesActivity.this.getResources().getString(R.string.update_tests));
        DatabaseHelper db = new DatabaseHelper(this);
        db.openDataBase();
        Selective sel = db.getSelective();
        String url = Constants.URL + Constants.API_TESTS+"?selective="+sel.getId()+"&type="+AllActivities.testSelected;
        Connection task = new Connection(url, 0, Constants.CALLED_GET_TESTS, false, AthletesActivity.this);
        task.callByJsonStringRequest();
    }

    private void getUpdateTeams(String updateDate){
        constraintNotConnect.setVisibility(View.GONE);
        linearProgress.setVisibility(View.VISIBLE);
        textProgress.setText(AthletesActivity.this.getResources().getString(R.string.update_tests));
        String url = Constants.URL + Constants.API_SELECTIVE_TESTS_SEARCH;
        PostAsyncTask post = new PostAsyncTask();
        post.setActivity(this);
        post.setWhoCalled(Constants.CALLED_GET_TESTS);
        post.setObjPut(createQuerie(updateDate));
        post.execute(url);
    }

    private JSONObject createQuerie(String date){
        DatabaseHelper db = new DatabaseHelper(this);
        db.openDataBase();
        Selective sel = db.getSelective();
        JSONObject object = new JSONObject();
        try{
            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put("type",AllActivities.testSelected);
            jsonQuery.put("selective", sel.getId());
            JSONObject jsonGte = new JSONObject();
            jsonGte.put("$gte",date);
            jsonQuery.put("updatedAt", jsonGte);
            object.put("query", jsonQuery);
        }catch (Exception ex){
            Log.i("Exception", ex.getMessage());
        }
        return object;
    }

    public static void returnGetAllTests(Activity act, String response, int status){
        ((AthletesActivity)act).returnGetAllTests(response, status);
    }

    private void returnGetAllTests(String response, int status){
        linearProgress.setVisibility(View.GONE);
        if(status == 200 || status == 201) {
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            ArrayList<Tests> tests = des.getTest();
            try{
                if (tests!=null) {
                     recordingTests(tests);
                }
            }catch (Exception e){Log.i("Exception: ", e.getMessage());}
        }
        callInflateAthletes();
    }

    private void recordingTests(ArrayList<Tests> tests){
        DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
        db.openDataBase();
        String shared = "Date "+db.getTestTypeFromId(AllActivities.testSelected).getName();
        String currentDate = Services.getCurrentDateTime();
        SharedPreferencesAdapter.setValueStringSharedPreferences(this, shared, currentDate);
        try {
            db.createDataBase();
            if(tests!=null && tests.size()>0) {
                db.openDataBase();
                db.updateTests(tests);
            }
        } catch (Exception e) {Log.i("recordingTests",e.getMessage());}
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        editSearch.setText("");
        hideKeyboard();
        callInflateAthletes();
    }

    private void callInflateAthletes(){
        DatabaseHelper db = new DatabaseHelper(myContext);
        db.openDataBase();
        athletesArrayList = db.getAthletes();
        if(athletesArrayList!=null) {
            listViewPlayers.setVisibility(View.VISIBLE);
            linearNotSearch.setVisibility(View.GONE);
            orderName();
            showList(athletesArrayList);
        }

    }

    private void showList(ArrayList<Athletes> arrayAthletes){
        if(!(arrayAthletes == null || arrayAthletes.size()==0)){
            this.athletesArrayList = arrayAthletes;
            String[] values = new String[arrayAthletes.size()];
            for(int i=0; i <=arrayAthletes.size()-1; i++){
                values[i] = arrayAthletes.get(i).getId();
            }
            inflateRecyclerView(arrayAthletes, values);
        }
    }

    private void inflateRecyclerView(ArrayList<Athletes> athletesArrayList, String[] values){

        adapterTests = new AdapterRecyclerAthletes(AthletesActivity.this, athletesArrayList, values);

        listViewPlayers.setVisibility(View.VISIBLE);
        listViewPlayers.setAdapter(adapterTests);
    }

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public void onBackPressed(){
        finish();
    }

    public static void onClickItemList(Activity activity, int positionArray){
        ((AthletesActivity) activity).onClickItemList(positionArray);
    }

    public void onClickItemList(int position){
        Athletes player  = athletesArrayList.get(position);
        Intent i;
        DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
        db.openDataBase();
        TestTypes testTypes = db.getTestTypeFromId(AllActivities.testSelected);
        if(testTypes.getValueType().toLowerCase().equals("corrida") || testTypes.getValueType().toLowerCase().equals("tempo"))
            //i = new Intent(AthletesActivity.this, CronometerActivity.class);
            i = new Intent(AthletesActivity.this, CronometerOnlyOneActivity.class);
        else if (testTypes.getValueType().toLowerCase().equals("repeticao")|| testTypes.getValueType().toLowerCase().equals("repeticao por tempo"))
            i = new Intent(AthletesActivity.this, TimerActivity.class);
        else
            //i = new Intent(AthletesActivity.this, ResultsActivity.class);
            i = new Intent(AthletesActivity.this, ResultsOnlyOneActivity.class);

        i.putExtra("id_player",player.getId());
        i.putExtra("position",position);
        startActivity(i);
    }

    /******************** BUSCAS E ORDENAÇÃO ***********************/
    private View.OnTouchListener editTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean ret = false;
            editSearch.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            imgOrder.setVisibility(View.GONE);
            editSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, R.drawable.close, 0);

            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            try {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editSearch.getRight() - editSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        editSearch.setText("");
                        hideKeyboard();
                        imgOrder.setVisibility(View.VISIBLE);
                        editSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
                        editSearch.setCursorVisible(false);
                        editSearch.clearFocus();
                        ret = true;
                        callInflateAthletes();
                    }
                }
                ret = false;
            } catch (Exception e) {
                Log.i("Clicked", e.getMessage());
            }
            return ret;
        }

    };

    private View.OnClickListener clickedOrderCode = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
                orderCode();
            linearOrder.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener clickedOrderName = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            orderName();
            linearOrder.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener hideOptionsOrder = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            linearOrder.setVisibility(View.GONE);
        }
    };

    private void orderName (){
        try {
            if (!(athletesArrayList == null || athletesArrayList.size() == 0)) {

                Collections.sort(athletesArrayList, new Comparator<Athletes>() {
                    public int compare(Athletes v1, Athletes v2) {
                        return v1.getName().toLowerCase().compareTo(v2.getName().toLowerCase());
                    }
                });

                showList(athletesArrayList);
                textOptionName.setTextColor(ContextCompat.getColor(AthletesActivity.this, R.color.color_primary));
                textOptionCode.setTextColor(ContextCompat.getColor(AthletesActivity.this, R.color.black));
            }
        }catch (Exception e){}
    }

    private void orderCode(){
        try {
            if(!(athletesArrayList == null || athletesArrayList.size()==0)) {
                Collections.sort(athletesArrayList, new Comparator<Athletes>() {
                    public int compare(Athletes v1, Athletes v2) {
                        return v1.getCode().toLowerCase().substring(3).compareTo(v2.getCode().toLowerCase().substring(3));
                    }
                });

                showList(athletesArrayList);
                textOptionCode.setTextColor(ContextCompat.getColor(AthletesActivity.this, R.color.color_primary));
                textOptionName.setTextColor(ContextCompat.getColor(AthletesActivity.this, R.color.black));
            }
        }catch (Exception e){
            Log.i("Error ",e.getMessage());
            try {
                if(!(athletesArrayList == null || athletesArrayList.size()==0)) {
                    Collections.sort(athletesArrayList, new Comparator<Athletes>() {
                        public int compare(Athletes v1, Athletes v2) {
                            return v1.getCode().compareTo(v2.getCode());
                        }
                    });

                    showList(athletesArrayList);
                    textOptionCode.setTextColor(ContextCompat.getColor(AthletesActivity.this, R.color.color_primary));
                    textOptionName.setTextColor(ContextCompat.getColor(AthletesActivity.this, R.color.black));
                }
            }catch (Exception error){
                Log.i("Error ",error.getMessage());
            }

        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchPlayer(String search){
        DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
        db.openDataBase();
        ArrayList<Athletes> athletesList = db.searchAthletes(search);
        if(athletesList!= null && athletesList.size()>0) {
            showList(athletesList);
            listViewPlayers.setVisibility(View.VISIBLE);
            linearNotSearch.setVisibility(View.GONE);
        }
        else{
            listViewPlayers.setVisibility(View.GONE);
            linearNotSearch.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener clickedUpdateAthletes = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MessageOptions(AthletesActivity.this, AthletesActivity.this.getResources().getString(R.string.message), AthletesActivity.this.getResources().getString(R.string.registered_athletes),"UpdateAthletes");
        }
    };

    public static void returnOptions(Activity act, String whoCalled){
        ((AthletesActivity)act).returnOptions(whoCalled);
    }
    private void returnOptions(String whoCalled){
        if(whoCalled.equals("UpdateAthletes"))
            updateAthletes();
    }
    private void updateAthletes(){
        if(Services.isOnline(AthletesActivity.this)){
            linearProgress.setVisibility(View.VISIBLE);
            textProgress.setText("Atualziando Atletas");
            DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
            Selective selective = db.getSelective();
            String url = Constants.URL+Constants.API_SELECTIVEATHLETES+"?"+Constants.SELECTIVEATHLETES_SELECTIVE+"="+
                    selective.getId();
            Connection task =new Connection(url, 0,"UpdateSelectiveAthletes",false, AthletesActivity.this);
            task.callByJsonStringRequest();
        }
        else
            Services.messageAlert(AthletesActivity.this, AthletesActivity.this.getResources().getString(R.string.warning), AthletesActivity.this.getResources().getString(R.string.connection_required),"");
    }

    public static void returnUpdateSelectiveAthletes(Activity activity, String response, int statusCode){
        ((AthletesActivity)activity).returnUpdateSelectiveAthletes(response, statusCode);
    }
    private void returnUpdateSelectiveAthletes(String response, int statusCode) {
        if (statusCode == 200 || statusCode == 201) {
            linearProgress.setVisibility(View.GONE);
            if (response.trim().equals("[]") || response.isEmpty()) {
                Services.messageAlert(AthletesActivity.this, AthletesActivity.this.getResources().getString(R.string.message),  AthletesActivity.this.getResources().getString(R.string.no_athlete_upgrade), "");
            } else {
                DeserializerJsonElements des = new DeserializerJsonElements(response);
                ArrayList<SelectiveAthletes> sele = des.getSelectiveAthletes();
                DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
                db.openDataBase();
                db.addSelectivesAthletes(sele);
                String url = Constants.URL+Constants.API_ATHLETES;
                linearProgress.setVisibility(View.VISIBLE);
                textProgress.setText("Atualziando Atletas");
                Connection task =new Connection(url, 0,"UpdateAthletes",false, AthletesActivity.this);
                task.callByJsonStringRequest();
            }
        }
        else
            Services.messageAlert(AthletesActivity.this,  AthletesActivity.this.getResources().getString(R.string.message),  AthletesActivity.this.getResources().getString(R.string.error_trying_update), "");

    }

    public static void returnUpdateAthletes(Activity act, String response, int status){
        ((AthletesActivity)act).returnUpdateAthletes(response, status);
    }
    private void returnUpdateAthletes(String response, int status){
        if (status == 200 || status == 201) {
            if (response.trim().equals("[]") || response.isEmpty()) {
                Services.messageAlert(AthletesActivity.this, AthletesActivity.this.getResources().getString(R.string.message), AthletesActivity.this.getResources().getString(R.string.no_athlete_upgrade), "");
            } else {
                DeserializerJsonElements des = new DeserializerJsonElements(response);
                ArrayList<Athletes> athletes = des.getAthletes();
                DatabaseHelper db = new DatabaseHelper(AthletesActivity.this);
                db.openDataBase();
                for(Athletes athlete : athletes){
                    Athletes obj = db.getAthleteById(athlete.getId());
                    if(obj==null){
                        SelectiveAthletes sel = db.getSelectiveAthletesFromAthlete(athlete.getId());
                        if(sel!=null){
                            athlete.setCode(sel.getInscriptionNumber());
                            db.addAthlete(athlete);
                        }
                    }

                    else if(obj.getCode().isEmpty()){
                        SelectiveAthletes sel = db.getSelectiveAthletesFromAthlete(athlete.getId());
                        if(sel!=null){
                            athlete.setCode(sel.getInscriptionNumber());
                            db.updateAthlete(athlete);
                        }
                    }
                }

                linearProgress.setVisibility(View.GONE);
                Services.messageAlert(AthletesActivity.this, AthletesActivity.this.getResources().getString(R.string.message),AthletesActivity.this.getResources().getString(R.string.all_athletes_updated),"");
                callInflateAthletes();
            }
        }
        else
            Services.messageAlert(AthletesActivity.this, AthletesActivity.this.getResources().getString(R.string.message), AthletesActivity.this.getResources().getString(R.string.error_trying_update), "");
    }

}
