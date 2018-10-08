package br.com.john.combinebrasil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerPositions;
import br.com.john.combinebrasil.AdapterList.AdapterRecyclerResultsTest;
import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTestInfo;
import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTests;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.CreateJSON;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAsyncTask;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.ImageConverter;
import br.com.john.combinebrasil.Services.ImageLoadedCallback;
import br.com.john.combinebrasil.Services.Services;

public class HistoricPlayerActivity extends AppCompatActivity {
    Toolbar toolbar;
    private String idAthlete, idSelective;
    TextView textNamePlayer, textEmailPlayer, textPhonePlayer, textBirthdayPlayer, textAddressPlayer, textHeightPlayer, textWeightPlayer;
    ImageView imagePlayer;
    RecyclerView recyclerTests, recyclerPositions;
    public static Athletes athlete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_player);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(clickedBack);
        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.linear_menu_button);
        btnMenu.setVisibility(View.GONE);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setImageDrawable(this.getDrawable(R.drawable.ic_filter_list_white_24dp));
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(R.string.players);

        imagePlayer = (ImageView) findViewById(R.id.image_player);

        textNamePlayer=(TextView)findViewById(R.id.text_player);
        textEmailPlayer=(TextView)findViewById(R.id.text_email_player);
        textPhonePlayer=(TextView)findViewById(R.id.text_phone_player);
        textBirthdayPlayer=(TextView)findViewById(R.id.text_birthday_player);
        textAddressPlayer=(TextView)findViewById(R.id.text_local_player);
        textHeightPlayer=(TextView)findViewById(R.id.text_player_height);
        textWeightPlayer=(TextView)findViewById(R.id.text_player_weight);

        recyclerTests=(RecyclerView)findViewById(R.id.recycler_player_tests);
        recyclerPositions=(RecyclerView)findViewById(R.id.recycler_position);

        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            idAthlete = athlete.getId();
            idSelective = extras.getString("id_selective");
            callInfoSelectivePlayer();
            //callResultsAthletes();
        }
    }

    private void callInfoSelectivePlayer(){
        showProgress("Baixando resultados dos atletas");
        String url = Constants.URL+Constants.API_SELECTIVES+"/"+idSelective+"/result/athlete/"+idAthlete;
        Connection con = new Connection(url, 0, Constants.CALLED_RESULTS_ATHLETE, false, this);
        con.callByJsonStringRequest();

        showInfoAthlete(this.athlete);
    }

    private void showInfoAthlete(Athletes athlete){
        if(athlete!=null) {
            textNamePlayer.setText(athlete.getName());
            textAddressPlayer.setText(HistoricPlayerActivity.this.getResources().getString(R.string.live)+" " + athlete.getAddress());
            textEmailPlayer.setText(athlete.getEmail());
            textPhonePlayer.setText(athlete.getPhoneNumber());
            textBirthdayPlayer.setText(HistoricPlayerActivity.this.getResources().getString(R.string.born_in)+" " + returnDateTime(athlete.getBirthday()));
            textHeightPlayer.setText(String.valueOf(athlete.getHeight()).replace(".",",") + "0 metros");
            textWeightPlayer.setText(String.valueOf(athlete.getWeight()).substring(0, String.valueOf(athlete.getWeight()).indexOf("."))+ " Kg");
            String url = !athlete.getURLImage().equals("")?athlete.getURLImage():"https://images.vexels.com/media/users/3/131988/isolated/lists/bdddce6b399e0b4b6a09aed763849530-rugby-player-celebrating-silhouette.png";

            showImageAthlete(url);
        }
    }

    private String returnDateTime(String dateTime){
        dateTime = dateTime.replace("[","").replace("]","");
        String year = dateTime.substring(0, 4);
        String month = dateTime.substring(5, 7);
        String day = dateTime.substring(8, 10);

        return day + "/" + month + "/" + year;
    }
    private static Activity act;
    private void showImageAthlete(String urlImage){
        act = this;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_image);
        progressBar.setVisibility(View.VISIBLE);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(5)
                .cornerRadiusDp(10)
                .oval(true)
                .build();

        Picasso.with(this)
                .load(urlImage)
                .transform(transformation)
                .into(imagePlayer,  new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onError(){
                        progressBar.setVisibility(View.GONE);
                        imagePlayer.setImageDrawable(act.getResources().getDrawable(R.drawable.athlete_icon));
                    }
                });
    }

    private void roundedImage(){
        Bitmap bitmap = ((BitmapDrawable)imagePlayer.getDrawable()).getBitmap();
        bitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 200);
        imagePlayer.setImageBitmap(bitmap);
    }

    private void callResultsAthletes(){
        if(Services.isOnline(this)){
            showProgress(HistoricPlayerActivity.this.getResources().getString(R.string.checking_athlete_results));
            PostAsyncTask postAsyncTask = new PostAsyncTask();
            String url = Constants.URL+Constants.API_RESULTS_SELECTIVE_ATHLETES_SEARCH;
            postAsyncTask.setActivity(this);
            postAsyncTask.setObjPut(querySearchData());
            postAsyncTask.setWhoCalled(Constants.CALLED_RESULTS_ATHLETE);
            postAsyncTask.execute(url);
        }
        else
            showNotConnection();
    }

    public static void returnGetResultsSelectiveAthlete(Activity act, String result, int status){
        ((HistoricPlayerActivity)act).returnGetResultsSelectiveAthlete(result, status);
    }

    private void returnGetResultsSelectiveAthlete(String result, int status){
        hideProgress();
        if(status == 200 || status == 201){
            desesrializerJson(result);
        }
    }

    private void desesrializerJson(String response){
        try {
            JSONObject json = new JSONObject(response);
            JSONArray jsonTests = json.getJSONArray("tests");
            ArrayList<Tests> tests = new ArrayList<>();
            for(int i=0;i<=jsonTests.length()-1;i++) {
                try {
                    DeserializerJsonElements des = new DeserializerJsonElements(jsonTests.getString(i));
                    Tests test = des.getTestReportObject();

                    test.setAthlete(jsonTests.getJSONObject(i).getJSONObject("athlete").getString(Constants.ATHLETES_NAME));
                    test.setType(jsonTests.getJSONObject(i).getJSONObject("type").getString(Constants.TESTTYPES_NAME));
                    test.setUser(jsonTests.getJSONObject(i).getJSONObject("type").getString(Constants.TESTTYPES_VALUETYPES));

                    test.setImageIconUrl(jsonTests.getJSONObject(i).getJSONObject("type").getString(Constants.TESTTYPES_ICONIMAGEURL));
                    tests.add(test);
                }catch(Exception ex){
                    Log.i("Exception", ex.getMessage());
                }
            }


            JSONArray jsonPositions = json.getJSONArray("positions");
            ArrayList<Positions> arrayPositions=new ArrayList<>();
            String positions = "";
            for(int i=0;i<=jsonPositions.length()-1;i++){
                try {
                    Positions position = new Positions();
                    position.setID("");
                    position.setNAME(jsonPositions.getJSONObject(i).getString("name"));
                    position.setDESCRIPTION(jsonPositions.getJSONObject(i).getString("point"));
                    arrayPositions.add(position);

                    positions = positions + jsonPositions.getJSONObject(i).getString("name") + ", ";
                }catch(Exception ex) {
                    Log.i("Exception", ex.getMessage());
                }
            }

            initInflatePositions(arrayPositions);
            initInflateTests(tests);
        }catch (Exception ex){
            Log.i("Exception", ex.getMessage());
        }
    }

    public class FishNameComparator implements Comparator<Positions>
    {
        public int compare(Positions left, Positions right) {
            return left.getDESCRIPTION().compareTo(right.getDESCRIPTION());
        }
    }

    private void initInflateTests(ArrayList<Tests> tests){
        if(tests!=null && tests.size()>0){
            String[] values = new String[tests.size()];
            for(int i=0; i<=tests.size()-1; i++){
                values[i] = tests.get(i).getId();
            }
            inflateTests(tests, values);
        }
        else
            recyclerTests.setVisibility(View.GONE);
    }

    private void inflateTests(ArrayList<Tests> tests, String[] values){
        AdapterRecyclerResultsTest adapter = new AdapterRecyclerResultsTest(this, tests, values);
        recyclerTests.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerTests.setVisibility(View.VISIBLE);
        recyclerTests.setAdapter(adapter);
    }

    private void initInflatePositions(ArrayList<Positions> positions){
        if(positions!=null && positions.size()>0){
            String[] values = new String[positions.size()];
            for(int i=0; i<=positions.size()-1; i++){
                values[i] = positions.get(i).getDESCRIPTION();
            }
            inflatePositions(positions, values);
        }
        else
            recyclerPositions.setVisibility(View.GONE);
    }

    private void inflatePositions(ArrayList<Positions> positions, String[] values){
        AdapterRecyclerPositions adapter = new AdapterRecyclerPositions(this, positions, values);
        recyclerPositions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerPositions.setVisibility(View.VISIBLE);
        recyclerPositions.setAdapter(adapter);
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

    private void showProgress(String message){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        TextView textProgress = (TextView) findViewById(R.id.text_progress);
        textProgress.setText(message);
        constraintProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintProgress.setVisibility(View.GONE);
    }

    private void showNotConnection(){
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        Button button = (Button) findViewById(R.id.btn_try_again_connect);
        constraintLayout.setVisibility(View.VISIBLE);
        button.setOnClickListener(clickTryAgain);
    }

    private View.OnClickListener clickTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callResultsAthletes();
            hideNotConnection();
        }
    };
    private void hideNotConnection(){
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintLayout.setVisibility(View.GONE);
    }

    private View.OnClickListener clickedBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
