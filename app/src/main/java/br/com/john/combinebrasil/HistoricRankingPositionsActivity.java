package br.com.john.combinebrasil;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.com.john.combinebrasil.AdapterList.ExpandableRecycler.ChildItemTests;
import br.com.john.combinebrasil.AdapterList.ExpandableRecyclerPositions.ChildItemPositions;
import br.com.john.combinebrasil.AdapterList.ExpandableRecyclerPositions.ExpandableRecyclerViewAdapterPositions;
import br.com.john.combinebrasil.AdapterList.ExpandableRecyclerPositions.GroupFatherPositions;
import br.com.john.combinebrasil.Classes.RankingPositions;
import br.com.john.combinebrasil.Classes.ResultTest;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAsyncTask;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.Services;

public class HistoricRankingPositionsActivity extends AppCompatActivity {
    Toolbar toolbar;
    ArrayList<RankingPositions> positions;
    ArrayList<GroupFatherPositions> groupFathers;
    ArrayList<ChildItemPositions> childItens;
    ExpandableRecyclerViewAdapterPositions adapterExpandable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_ranking_positions);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(clickedBack);
        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.linear_menu_button);
        btnMenu.setVisibility(View.GONE);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.img_delete);
        imgSearch.setVisibility(View.GONE);
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(HistoricRankingPositionsActivity.this.getResources().getString(R.string.ranking_by_positions));


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            callGetTestTypes(extras.getString("id_selective"));
        }

    }

    private void callGetTestTypes(String idSelective){
        if(Services.isOnline(this)){
            showProgress();
            getPositions(idSelective);
        }
    }

    private void getPositions(String idSelective) {
        String url = Constants.URL + Constants.API_SELECTIVES+"/"+idSelective+"/result";
        Connection con = new Connection(url, 0, Constants.CALLED_GET_POSITIONS_RESULT, false, this);
        con.callByJsonStringRequest();
    }

    public static void returnGetTestTypes(Activity act, String response, int status){
        ((HistoricRankingPositionsActivity)act).returnGetTestTypes(response, status);
    }

    private void returnGetTestTypes(String response, int status){
        hideProgress();
        if(status==200||status==201){
            if(!response.replace("[","").replace("]","").trim().isEmpty())
                createObjectPositions(response);
            else {
                ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_not_data);
                constraint.setVisibility(View.VISIBLE);
            }
        }
    }

    private void createObjectPositions(String response){
        try{
            ArrayList<GroupFatherPositions> groupFather = new ArrayList<>();
            ArrayList<ChildItemPositions> childPosition = new ArrayList<>();
            JSONObject json = new JSONObject(response);
            JSONArray jsonArray = json.getJSONArray("byPositions");
            for(int i=0; i<=jsonArray.length()-1;i++){
                childPosition = new ArrayList<>();
                json = jsonArray.getJSONObject(i);
                String namePos= (json.optString("key"));
                JSONArray jsonAthletes = json.getJSONArray("athletes");
                for(int x = 0;x<=jsonAthletes.length()-1; x++){
                    json = jsonAthletes.getJSONObject(x);
                    String nameAthlete =  json.getString("name");
                    JSONArray jsonPositions = json.getJSONArray("otherOptions");
                    String positions="";
                    for(int p=0; p<=jsonPositions.length()-1;p++){
                        positions  = positions + jsonPositions.getJSONObject(p).getString("name")+", ";
                    }
                    int num = x+1;
                    childPosition.add(new ChildItemPositions(nameAthlete, "","",positions.substring(0,positions.length()-2),"#"+(num<9?"0"+num:num)));
                }
                groupFather.add(new GroupFatherPositions(namePos, childPosition));
            }
            inflateDataTests(groupFather);
        }catch(Exception ex){
            Log.i("Exception", ex.getMessage());

        }
    }

    private void callInflateRecycler(){
        if(positions!=null && positions.size()>0){
            String[] values = new String[positions.size()];
            for(int i=0; i<=positions.size()-1; i++)
                values[i] = positions.get(i).getID_POSITION();

            inflateRecycler(values);
        }
    }

    private void inflateRecycler(String[] values){
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_ranking);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(new GridLayoutManager(this, 2));//numero de colunas
       // AdapterRecyclerTests adapter = new AdapterRecyclerTests(this, testTypes, values);
        recycler.setVisibility(View.VISIBLE);
      // recycler.setAdapter(adapter);
    }

    private ArrayList<RankingPositions> inflateFalseDatasTests(int cont){
        ArrayList<RankingPositions> tests = new ArrayList<RankingPositions>();
        for(int i=0; i<=10; i++){
            RankingPositions position =  new RankingPositions();
            position.setATHLETE(HistoricRankingPositionsActivity.this.getResources().getString(R.string.athlete)+" "+i);
            position.setID_ATHLETE(String.valueOf(cont));
            position.setID_POSITION(String.valueOf(i));
            position.setPOSITION(HistoricRankingPositionsActivity.this.getResources().getString(R.string.posicao)+" "+i);
            position.setDESCRIPTION(String.valueOf(i));
            tests.add(position);
        }
        return tests;
    }

    private void inflateDataTests(ArrayList<GroupFatherPositions> positions){
        groupFathers = positions;
        inflateExpandableRecycler();
    }

    private void inflateExpandableRecycler(){
        adapterExpandable = new ExpandableRecyclerViewAdapterPositions(this, groupFathers);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_ranking);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setVisibility(View.VISIBLE);
        recycler.setAdapter(adapterExpandable);
    }

    private ArrayList<Tests> orderAlphabeticTests(ArrayList<Tests> tests){
        Collections.sort(tests, new Comparator<Tests>() {
            @Override
            public int compare(Tests o1, Tests o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });

        return tests;
    }

    public static void clickItemRecycler(Activity act, int position){
        ((HistoricRankingPositionsActivity)act).clickItemRecycler(position);
    }

    private void clickItemRecycler(int position){
        positions.get(position);

    }

    private JSONObject queryJson(String id){
        JSONObject json = new JSONObject();
        try{
            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put(Constants.SELECTIVEATHLETES_SELECTIVE, id);
            json.put("query", jsonQuery);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("testType");
            json.put("populate", jsonArray);
        }catch(JSONException ex){
            Log.i("JSONException", ex.getMessage());
        }
        return json;
    }

    private void showProgress(){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraint.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraint.setVisibility(View.GONE);
    }

    private View.OnClickListener clickedBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
