package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.HashMap;

import br.com.john.combinebrasil.AdapterList.AdapterCoverflow;
import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTeam;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAsyncTask;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

public class ChooseTeamSelectiveActivity extends AppCompatActivity {
    Button btnAddTeam, btnNextPass, btnCancelarCodigo, btnPronto, btnCancelaEsqueci, btnEnviaEsqueci;
    ImageView imgPrevious, imgNext, imgCancelSearch;
    Toolbar toolbar;
    ViewPager pager;
    ArrayList<Team> teams;
    public static Activity act;
    ConstraintLayout constraintSearch, constraintCodigoEquipe, constraintEsqueci;
    EditText editSearch, editCodigo, editEqueciSenha;
    PagerAdapter adapter;
    TextView textEsqueciCodigo, textTeste;
    boolean codigo = false, esqueci =  false;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team_selective);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnClickListenter);

        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.linear_menu_button);
        btnMenu.setVisibility(View.GONE);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.VISIBLE);
        imgSearch.setOnClickListener(clickSearch);
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(R.string.create_selective);

        constraintCodigoEquipe = (ConstraintLayout) findViewById(R.id.constraint_codigo);
        textEsqueciCodigo = (TextView) findViewById(R.id.text_esqueci_codigo);
        textTeste = (TextView) findViewById(R.id.text_teste);
        btnCancelarCodigo = (Button) findViewById(R.id.button_cancela_codigo);
        btnPronto = (Button) findViewById(R.id.button_ready_selective);
        editCodigo = (EditText) findViewById(R.id.edit_code_team);

        btnCancelaEsqueci = (Button) findViewById(R.id.button_cancela_esqueci);
        btnEnviaEsqueci = (Button) findViewById(R.id.button_envia_esqueci);
        constraintEsqueci = (ConstraintLayout) findViewById(R.id.constaint_esqueci_codigo);
        editEqueciSenha = (EditText) findViewById(R.id.edit_email_team);

        btnCancelaEsqueci.setOnClickListener(cancelaEsqueci);
        btnEnviaEsqueci.setOnClickListener(enviaEsqueciCodigo);
        btnCancelarCodigo.setOnClickListener(clickCancelarCodigo);
        btnPronto.setOnClickListener(clickPronto);

        act = ChooseTeamSelectiveActivity.this;
        btnAddTeam= (Button) findViewById(R.id.button_add_team);
        btnNextPass = (Button) findViewById(R.id.button_next_pass);
        imgPrevious = (ImageView) findViewById(R.id.img_previous);
        imgNext = (ImageView) findViewById(R.id.img_next);
        btnAddTeam.setOnClickListener(clickAddTeamListener);
        btnNextPass.setOnClickListener(clickNextPassListener);
        imgPrevious.setOnClickListener(btnPreviousListener);
        imgNext.setOnClickListener(btnNextListener);

        constraintSearch = (ConstraintLayout) findViewById(R.id.contraint_search);
        editSearch = (EditText) findViewById(R.id.edit_search);
        imgCancelSearch = (ImageView) findViewById(R.id.img_cancel_search);
        imgCancelSearch.setOnClickListener(closeSearch);
        editSearch.addTextChangedListener(textSearchChange);
        textEsqueciCodigo.setOnClickListener(clickEsqueciCodigo);
        textEsqueciCodigo.setVisibility(View.VISIBLE);
        teams = new ArrayList<Team>();

        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        pager = mContainer.getViewPager();
        mContainer.setBackgroundColor(Color.argb(255,255,255,255));

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                confereEquipeTeste(teams.get(getItem(0)));
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        boolean showRotate = getIntent().getBooleanExtra("showRotate",true);

        if(showRotate){
            new CoverFlow.Builder()
                    .with(pager)
                    .scale(0.6f)
                    .pagerMargin(0.3f)
                    .spaceSize(0.7f)
                    .rotationY(0f)
                    .build();
        }
        getTeams();
    }

    private void getTeams(){
        if(Services.isOnline(ChooseTeamSelectiveActivity.this)) {
            String updateDate = SharedPreferencesAdapter.getValueStringSharedPreferences(this,Constants.DATE_UPDATE_TEAM);
            if(updateDate.equals(""))
                getTeamsInit();
            else
                getUpdateTeams(updateDate);
        }
        else
            showNotConnect();
    }

    private void getTeamsInit(){
        hideNotConnect();
        showProgress(getString(R.string.update_teams));
        String url = Constants.URL + Constants.API_TEAMS+"?isStarTeam=true";
        Connection task = new Connection(url, 0, Constants.CALLED_GET_TEAM, false, ChooseTeamSelectiveActivity.this);
        task.callByJsonStringRequest();
    }

    private void getUpdateTeams(String updateDate){
        hideNotConnect();
        showProgress(getString(R.string.update_teams));
        String url = Constants.URL + Constants.API_SELECTIVE_TEAM_SEARCH;
        PostAsyncTask post = new PostAsyncTask();
        post.setActivity(this);
        post.setWhoCalled(Constants.CALLED_GET_TEAM);
        post.setObjPut(createQuerie(updateDate));
        post.execute(url);
    }

    private JSONObject createQuerie(String date){
        JSONObject object = new JSONObject();
        try{
            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put("isStarTeam", true);
            JSONObject jsonGte = new JSONObject();
            jsonGte.put("$gte",date);
            jsonQuery.put("updatedAt", jsonGte);
            object.put("query", jsonQuery);
        }catch (Exception ex){
            Log.i("Exception", ex.getMessage());
        }
        return object;
    }

    public static void returnGetAllTeams(Activity act, String response, int status){
        ((ChooseTeamSelectiveActivity)act).returnGetAllTeams(response, status);
    }

    private void returnGetAllTeams(String response, int status){
        hideProgress();
        if(status == 200 || status == 201) {
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            ArrayList<Team> teams = des.getTeam();
            try{
                if (teams!=null) {
                    teams =  recordingTeams(teams);
                    inflateTeam(teams);
                }
            }catch (Exception e){Log.i("Exception: ", e.getMessage());}
        }
        else
            Services.messageAlert(this, "Aviso",response,"hide");
    }

    private ArrayList<Team> recordingTeams(ArrayList<Team> teams){
        DatabaseHelper db = new DatabaseHelper(ChooseTeamSelectiveActivity.this);

        SharedPreferencesAdapter.setValueStringSharedPreferences(this, Constants.DATE_UPDATE_TEAM, Services.getCurrentDateTime());
        try {
            db.createDataBase();
            if(teams!=null && teams.size()>0) {
                Team combineTeam = null;
                for (Team team : teams) {
                    if (team.getId().equals("59cdb8fee7afe900122679f0") || team.getName().equals("Time de Teste - Combine Brasil")) {
                        combineTeam = team;
                        break;
                    }
                }

                ArrayList<Team> teamsAdd = new ArrayList<Team>();
                teamsAdd.add(combineTeam);
                for (Team team : teams) {
                    if (!team.getId().equals("59cdb8fee7afe900122679f0") || !team.getName().equals("Time de Teste - Combine Brasil"))
                        teamsAdd.add(team);
                }
                db.openDataBase();
                db.addTeam(teamsAdd);
            }
            teams = db.getTeams();

        } catch (Exception e) {}
        return teams;
    }


    private void inflateTeam(ArrayList<Team> teams){
        Team combineTeam = null;
        for (Team team : teams) {
            if (team.getId().equals("59cdb8fee7afe900122679f0") || team.getName().equals("Time de Teste - Combine Brasil")) {
                combineTeam = team;
                break;
            }
        }

        ArrayList<Team> teamsAdd = new ArrayList<Team>();
        teamsAdd.add(combineTeam);
        for (Team team : teams) {
            if (!team.getId().equals("59cdb8fee7afe900122679f0") || !team.getName().equals("Time de Teste - Combine Brasil"))
                teamsAdd.add(team);
        }
        this.teams = teamsAdd;
        adapter = new AdapterCoverflow(ChooseTeamSelectiveActivity. this, this.teams);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setClipChildren(false);
        confereEquipeTeste(this.teams.get(getItem(0)));
    }

    private View.OnClickListener btnPreviousListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getItem(+1)>0) {
                pager.setCurrentItem(getItem(-1), true);
                confereEquipeTeste(teams.get(getItem(0)));
            }
        }
    };

    private View.OnClickListener btnNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getItem(+1)<teams.size()) {
                pager.setCurrentItem(getItem(+1), true);
                confereEquipeTeste(teams.get(getItem(0)));
            }
        }
    };

    private void confereEquipeTeste(Team team){
        if(team.getId().equals("59cdb8fee7afe900122679f0") || team.getName().equals("Time de Teste - Combine Brasil"))
            textTeste.setVisibility(View.VISIBLE);
        else
            textTeste.setVisibility(View.GONE);
   }

    private int getItem(int i) {
        return pager.getCurrentItem() + i;
    }

    private View.OnClickListener btnClickListenter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener clickAddTeamListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChooseTeamSelectiveActivity.this, CreateTeamActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener clickNextPassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(teams.get(getItem(0)).getName().equals("Time de Teste - Combine Brasil") || teams.get(getItem(0)).getId().equals("59cdb8fee7afe900122679f0"))
                nextPass();
            else {
                codigo = true;
                constraintCodigoEquipe.setVisibility(View.VISIBLE);
            }
        }
    };

    private void showNotConnect(){
        TextView textMessage = (TextView) findViewById(R.id.txt_message_not_connect);
        textMessage.setText(Html.fromHtml(getString(R.string.no_connection)));
        ConstraintLayout constraintNoConnect = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintNoConnect.setVisibility(View.VISIBLE);
        Button btnTryAgain =(Button) findViewById(R.id.btn_try_again_connect);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTeams();
            }
        });
    }

    private void hideNotConnect(){
        ConstraintLayout constraintNoConnect = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintNoConnect.setVisibility(View.GONE);
    }

    private void showProgress(String message){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        TextView textProgress = (TextView) findViewById(R.id.text_progress);
            textProgress.setText(message);
        constraintProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress (){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintProgress.setVisibility(View.GONE);
    }


    public static void finishActity (){
        ((ChooseTeamSelectiveActivity)act).finish();
    }

    //Search

    private TextWatcher textSearchChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>3){
                searchTeam(s.toString());
            }
            else{
                RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_search_team);
                recycler.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener clickSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintSearch.setVisibility(View.VISIBLE);
            editSearch.setFocusable(true);
            showSoft(editSearch);
        }
    };

    private void searchTeam(String search){
        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<Team> team = db.getSearchTeambyName(search);
        if(team != null)
            inflateListSearch(team);
    }

    private void inflateListSearch(ArrayList<Team> team){
        String[] values = new String[team.size()];
        for(int i=0;i<=team.size()-1; i++){
            values[i] = team.get(i).getId();
        }
        callInflateListSearch(team, values);
    }
    private void callInflateListSearch(ArrayList<Team> teams, String[] values){
        AdapterRecyclerTeam adapater = new AdapterRecyclerTeam(this, teams, values);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_search_team);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapater);
        recycler.setVisibility(View.VISIBLE);
    }

    public static void clickTeamSearch(Activity act, Team team){
        ((ChooseTeamSelectiveActivity)act).clickTeamSearch(team);
    }
    private void clickTeamSearch(Team team){
        hideSoft();
        editSearch.setText("");
        int item = findTeamChoose(team.getId());
        pager.setCurrentItem(item, true);
        adapter.notifyDataSetChanged();
        constraintSearch.setVisibility(View.GONE);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_search_team);
        recycler.setVisibility(View.INVISIBLE);
    }
    private int findTeamChoose(String id){
        int count = 0;
        for(int i=0; i<=teams.size()-1; i++){
            if (teams.get(i).getId().equals(id)){
                count = i;
                break;
            }
        }
        return count;
    }

    private View.OnClickListener closeSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideSoft();
            editSearch.setText("");
            constraintSearch.setVisibility(View.GONE);
            RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_search_team);
            recycler.setVisibility(View.INVISIBLE);
        }
    };

    private void showSoft(EditText edit){
        edit.setFocusable(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSoft(){
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private View.OnClickListener clickCancelarCodigo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editCodigo.setText("");
            codigo = false;
            constraintCodigoEquipe.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener clickPronto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            verifyCodigoEquipe();
        }
    };

    private void verifyCodigoEquipe(){
        if(!editCodigo.getText().toString().isEmpty()){
            if(teams.get(getItem(0)).getCode().toString().toUpperCase().equals(editCodigo.getText().toString().toUpperCase()))
                nextPass();
            else {
                textEsqueciCodigo.setVisibility(View.VISIBLE);
                Services.messageAlert(this, ChooseTeamSelectiveActivity.this.getResources().getString(R.string.message), ChooseTeamSelectiveActivity.this.getResources().getString(R.string.code_entered_invalid), "");
            }
        }
        else
            Services.messageAlert(this, ChooseTeamSelectiveActivity.this.getResources().getString(R.string.message), ChooseTeamSelectiveActivity.this.getResources().getString(R.string.enter_team_code_continue),"");
    }

    private void nextPass(){
        codigo = false;
        constraintCodigoEquipe.setVisibility(View.GONE);
        Intent intent = new Intent(ChooseTeamSelectiveActivity.this, LocalSelectiveActivity.class);
        AllActivities.hashInfoSelective = new HashMap<String, String>();
        AllActivities.hashInfoSelective.put("team", teams.get(getItem(0)).getId());
        AllActivities.hashInfoSelective.put("nameTeam", teams.get(getItem(0)).getName());
        AllActivities.hashInfoSelective.put("urlImageTeam", teams.get(getItem(0)).getUrlImage());
        startActivity(intent);
    }

    private View.OnClickListener clickEsqueciCodigo = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            esqueci = true;
            constraintEsqueci.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener cancelaEsqueci = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            esqueci = false;
            constraintEsqueci.setVisibility(View.GONE);
            editEqueciSenha.setText("");
        }
    };

    private View.OnClickListener enviaEsqueciCodigo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Services.isValidEmail(editEqueciSenha, ChooseTeamSelectiveActivity.this)){
                chamaEsqueciSenha();
            }
        }
    };

    private void chamaEsqueciSenha(){
        showProgress("Verificando Código");
        if(Services.isOnline(this)){
            if(teams.get(getItem(0)).getCode().toString().toUpperCase().equals(editEqueciSenha.getText().toString().toUpperCase()))
            {
                esqueci = false;
                constraintEsqueci.setVisibility(View.GONE);
                editCodigo.setText(teams.get(getItem(0)).getCode());
                hideProgress();
            }
            else {
                String url = Constants.URL+Constants.API_TEAMS+"/"+teams.get(getItem(0)).getId()+"/"+Constants.API_FORGOT_CODE_TEAM;
                PostAsyncTask post = new PostAsyncTask();
                post.setActivity(this);
                post.setWhoCalled("ESQUECEU_CODIGO");
                post.setObjPut(ObjetoEmail(editEqueciSenha.getText().toString()));
                post.execute(url);
            }
        }
        else
            Services.messageAlert(this, ChooseTeamSelectiveActivity.this.getResources().getString(R.string.message), ChooseTeamSelectiveActivity.this.getResources().getString(R.string.need_internet_connection),"");
    }

    private JSONObject ObjetoEmail(String email){
        try{
            JSONObject jsonObject;

            jsonObject = new JSONObject();
            jsonObject.put(Constants.TEAM_EMAIL, email);
            return jsonObject;
        }catch (Exception e){
            Log.i("Exception",e.getMessage());
            return null;
        }
    }

    public static void retornoEsqueciCodigo(Activity act, String response, int status){
        ((ChooseTeamSelectiveActivity)act).retornoEsqueciCodigo(response, status);
    }
    private void retornoEsqueciCodigo(String response, int status){
        hideSoft();
        hideProgress();
        if(status==200){
            Services.messageAlert(this, ChooseTeamSelectiveActivity.this.getResources().getString(R.string.message), ChooseTeamSelectiveActivity.this.getResources().getString(R.string.emailed_representatives),"");
        }
        else
            Services.messageAlert(this,  ChooseTeamSelectiveActivity.this.getResources().getString(R.string.message),  ChooseTeamSelectiveActivity.this.getResources().getString(R.string.error_send_code),"");
    }

    @Override
    public void onBackPressed(){
        if(esqueci){
            esqueci = false;
            constraintEsqueci.setVisibility(View.GONE);
            editCodigo.setText(teams.get(getItem(0)).getCode());
        }
        else if(codigo){
            editCodigo.setText("");
            codigo = false;
            constraintCodigoEquipe.setVisibility(View.GONE);
        }
        else
            this.finish();
    }

}