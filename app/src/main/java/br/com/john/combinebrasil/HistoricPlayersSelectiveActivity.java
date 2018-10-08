package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerAthletesInfo;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAsyncTask;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

public class HistoricPlayersSelectiveActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerPlayers;
    EditText editSearch;
    AdapterRecyclerAthletesInfo adapterListAthletes;
    String idSelective ="";
    ArrayList<Athletes> athletes;
    ConstraintLayout linearNotSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_players_selective);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(clickedBack);
        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.linear_menu_button);
        btnMenu.setVisibility(View.GONE);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        linearAddAccount.setOnClickListener(clickedFilter);
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(R.string.players);

        recyclerPlayers = (RecyclerView) findViewById(R.id.recycler_players_historic);
        linearNotSearch = (ConstraintLayout) findViewById(R.id.linear_search_null);
        editSearch = (EditText) findViewById(R.id.edit_search);
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

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            idSelective = extras.getString("id_selective");
            callListPlayers();
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
        DatabaseHelper db = new DatabaseHelper(HistoricPlayersSelectiveActivity.this);
        db.openDataBase();
        ArrayList<Athletes> athletesList = db.searchAthletes(search);
        if(athletesList!= null && athletesList.size()>0) {
            showList(athletesList);
            recyclerPlayers.setVisibility(View.VISIBLE);
            linearNotSearch.setVisibility(View.GONE);
        }
        else{
            recyclerPlayers.setVisibility(View.GONE);
            linearNotSearch.setVisibility(View.VISIBLE);
        }
    }

    private void callInflateAthletes(){
        DatabaseHelper db = new DatabaseHelper(this);
        db.openDataBase();
        athletes = db.getAthletes();
        if(athletes!=null) {
            recyclerPlayers.setVisibility(View.VISIBLE);
            linearNotSearch.setVisibility(View.GONE);
            orderName();
            showList(athletes);
        }

    }

    private void orderName(){
        try {
            if (!(athletes == null || athletes.size() == 0)) {

                Collections.sort(athletes, new Comparator<Athletes>() {
                    public int compare(Athletes v1, Athletes v2) {
                        return v1.getName().toLowerCase().compareTo(v2.getName().toLowerCase());
                    }
                });

            }
        }catch (Exception e){}
    }

    private void showList(ArrayList<Athletes> arrayAthletes){
        if(!(arrayAthletes == null || arrayAthletes.size()==0)){
            athletes = arrayAthletes;
            String[] values = new String[arrayAthletes.size()];
            for(int i=0; i <=arrayAthletes.size()-1; i++){
                values[i] = arrayAthletes.get(i).getId();
            }
            inflateRecycler(arrayAthletes, values);
        }
    }

    private void callListPlayers(){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        TextView textView =(TextView)findViewById(R.id.text_progress);
        textView.setText(HistoricPlayersSelectiveActivity.this.getResources().getString(R.string.checking_athletes));


        if(Services.isOnline(HistoricPlayersSelectiveActivity.this)) {
            constraintProgress.setVisibility(View.VISIBLE);

            String url = Constants.URL + Constants.API_SELECTIVE_ATHLETES_SEARCH;
            PostAsyncTask post = new PostAsyncTask();
            post.setActivity(HistoricPlayersSelectiveActivity.this);
            post.setObjPut(querySearchData());
            post.setWhoCalled(Constants.CALLED_GET_ATHLETES);
            post.execute(url);
        }
        else{
            showNotConnection();
        }
    }

    private JSONObject querySearchData() {
        JSONObject object = new JSONObject();
        try {
            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put(Constants.SELECTIVEATHLETES_SELECTIVE, idSelective);
            object.put("query", jsonQuery);
            JSONArray jsonDates = new JSONArray();
            jsonDates.put(Constants.SELECTIVEATHLETES_ATHLETE);
            object.put("populate", jsonDates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static void returnGetPlayers(Activity act, String response, int status){
        ((HistoricPlayersSelectiveActivity)act).returnGetPlayers(response, status);
    }

    private void returnGetPlayers(String response, int status){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintProgress.setVisibility(View.GONE);
        if(status == 200 || status == 201){
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            athletes = des.getAthletesInSelective();

            orderName();

            if(athletes.size()>0){
                recordingSelectives(athletes);
            }
            else{
                ConstraintLayout constraint = (ConstraintLayout)findViewById(R.id.constraint_not_data);
                constraint.setVisibility(View.VISIBLE);
            }
        }
    }

    private void recordingSelectives(ArrayList<Athletes> athletes){
        DatabaseHelper db = new DatabaseHelper(HistoricPlayersSelectiveActivity.this);
        try {
            db.createDataBase();
            if(athletes!=null) {
                db.openDataBase();
                if(!MenuHistoricSelectiveActivity.enterSelective)
                    db.deleteTable(Constants.TABLE_ATHLETES);
                db.addAthletes(athletes);
                inflateAtheltes(athletes);
            }
        } catch (Exception e) {}
    }

    private void inflateAtheltes(ArrayList<Athletes> athletes){
        if(!(athletes == null || athletes.size()==0)){
            String[] values = new String[athletes.size()];
            for(int i=0; i <=athletes.size()-1; i++){
                values[i] = athletes.get(i).getId();
            }
            inflateRecycler(athletes, values);
        }
    }

    private void inflateRecycler(ArrayList<Athletes> athletes, String[] ids){
        recyclerPlayers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapterListAthletes = new AdapterRecyclerAthletesInfo(HistoricPlayersSelectiveActivity.this, athletes, ids);
        recyclerPlayers.setVisibility(View.VISIBLE);
        recyclerPlayers.setAdapter(adapterListAthletes);
    }

    private void showNotConnection(){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintProgress.setVisibility(View.GONE);
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        Button button = (Button) findViewById(R.id.btn_try_again_connect);
        constraint.setVisibility(View.VISIBLE);
        button.setOnClickListener(clickedTryAgain);
    }

    private View.OnClickListener clickedTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
            constraint.setVisibility(View.GONE);
            callListPlayers();
        }
    };

    private View.OnClickListener clickedBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener clickedFilter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showFilter();
        }
    };

    private void showFilter(){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_filter);
        constraint.setVisibility(View.VISIBLE);
    }

    public static void onClickListItem(Activity act, int position){
        ((HistoricPlayersSelectiveActivity)act).onClickListItem(position);
    }

    private void onClickListItem(int position){
        Athletes athlete = athletes.get(position);
        Intent intent = new Intent(this, HistoricPlayerActivity.class);
        HistoricPlayerActivity.athlete = athlete;
        intent.putExtra("id_athlete", athlete.getId());
        intent.putExtra("id_selective", idSelective);
        startActivity(intent);
    }
}
