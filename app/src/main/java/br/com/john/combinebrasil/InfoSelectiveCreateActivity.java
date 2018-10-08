package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTestInfo;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Connection.JSONServices.CreateJSON;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostCreateSelective;
import br.com.john.combinebrasil.Connection.Posts.PostTestsSelectives;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

import static br.com.john.combinebrasil.TestSelectiveActivity.messageSelectiveCreate;

public class InfoSelectiveCreateActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView textTeamInfo, textDetailsInfo, textObservationInfo, textTestsInfo, textNameSelective,
            textDateSelective, textLocalSelective, textCityInfoSelective, textNeighBorhoodSelective, textCepInfoSelective,
            textObservationsDetails, textPrivacy, textTermsPrivacy, textProgress, textPrice, textNameTeam;
    ImageView imgTeamChoose, imgshowMoreTeam, imgShowMoreObservations, imgShowMoreDetails, imgShowMoreTests;
    ConstraintLayout constraintDetailsInfo, constraintProgress, constraintNotConnection, constraintPrivacy;
    Button btnFinish, btnTryAgain, btnClosePrivacy;
    RecyclerView recyclerTests;
    AdapterRecyclerTestInfo adapterRecyclerTests;
    boolean isAcceptedPrivacy;
    HashMap<String, String> hashMapSelective;
    public static Activity act;
    String code = "", id="";
    ArrayList<String> testChooses;
    public static final int METHOD_CREATE_SELECTVE = 0, METHOD_TESTS_SELECTIVE =1, METHOD_USER_SELECTIVES=2;
    CheckBox checkBoxTerms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_selective_create);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(R.string.create_selective);

        textNameTeam = (TextView) findViewById(R.id.text_name_team);
        textTeamInfo = (TextView) findViewById(R.id.text_team_info);
        textDetailsInfo = (TextView) findViewById(R.id.text_detalhes_info);
        textObservationInfo = (TextView) findViewById(R.id.text_observacoes_info);
        textTestsInfo = (TextView) findViewById(R.id.text_testes_info);
        textNameSelective = (TextView) findViewById(R.id.text_name_selective_details);
        textDateSelective = (TextView) findViewById(R.id.text_date_selective_details);
        textLocalSelective = (TextView) findViewById(R.id.text_local_selective_details);
        textCityInfoSelective = (TextView) findViewById(R.id.text_city_selective_details);
        textNeighBorhoodSelective = (TextView) findViewById(R.id.text_neighborhood_selective_details);
        textCepInfoSelective = (TextView) findViewById(R.id.text_cep_selective_details);
        textPrivacy = (TextView) findViewById(R.id.text_politica_privacidade);
        textTermsPrivacy = (TextView) findViewById(R.id.text_terms_privacy);
        textObservationsDetails = (TextView) findViewById(R.id.text_observations_details);
        textProgress = (TextView) findViewById(R.id.text_progress);
        textPrice = (TextView) findViewById(R.id.text_price_selective);

        imgTeamChoose = (ImageView) findViewById(R.id.img_team_choose);
        imgshowMoreTeam = (ImageView) findViewById(R.id.img_show_more_team);
        imgShowMoreObservations = (ImageView) findViewById(R.id.img_show_more_observacoes);
        imgShowMoreDetails = (ImageView) findViewById(R.id.img_show_more_detalhes);
        imgShowMoreTests = (ImageView) findViewById(R.id.img_show_more_testes);

        constraintDetailsInfo = (ConstraintLayout) findViewById(R.id.constraint_details);
        constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintPrivacy = (ConstraintLayout) findViewById(R.id.constraint_privacy);
        constraintNotConnection = (ConstraintLayout) findViewById(R.id.constraint_not_connection);

        btnFinish = (Button) findViewById(R.id.btn_create_selective);
        btnTryAgain = (Button) findViewById(R.id.btn_try_again_connect);
        btnClosePrivacy = (Button) findViewById(R.id.btn_close_privacy);

        recyclerTests=(RecyclerView) findViewById(R.id.recycler_tests_info);

        checkBoxTerms = (CheckBox) findViewById(R.id.check_box_terms);
        textTeamInfo.setOnClickListener(btnShowMoteTeam);
        textDetailsInfo.setOnClickListener(btnShowMoteDetails);
        textObservationInfo.setOnClickListener(btnShowMoteObservation);
        textTestsInfo.setOnClickListener(btnShowMoteTests);
        String strTermos = this.getResources().getString(R.string.terms_of_use).toString().replace("{BREAKLINE}","<br>");
        textTermsPrivacy.setText(Html.fromHtml(strTermos));

        btnTryAgain.setOnClickListener(btnClickedTryAgain);
        btnFinish.setOnClickListener(btnCreateSelectiveClickListener);
        btnClosePrivacy.setOnClickListener(clickedClosePrivacy);
        textPrivacy.setOnClickListener(textClickedPrivacy);

        checkBoxTerms.setOnCheckedChangeListener(checkChangedListener);

        hashMapSelective = AllActivities.hashInfoSelective;
         act = InfoSelectiveCreateActivity.this;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            testChooses = extras.getStringArrayList("testsChoose");
            getTestsChoosesinBD(testChooses);
        }
        showInfosSelective();
    }

    private void showInfosSelective(){
        String urlImage = hashMapSelective.get("urlImageTeam").equals("") || hashMapSelective.get("urlImageTeam").isEmpty()?
                "https://image.ibb.co/ev8e1a/combine_brasil.png":
                hashMapSelective.get("urlImageTeam");

        Picasso.with(this)
                .load(urlImage)
                .fit()
                .centerCrop()
                .error(this.getDrawable(R.drawable.combine_brasil_azul))
                .into(imgTeamChoose);

        textNameTeam.setText(hashMapSelective.get("nameTeam"));
        textNameSelective.setText(hashMapSelective.get("title"));
        String data = "";

        if(!hashMapSelective.get("date").isEmpty())
            data = hashMapSelective.get("date");
        if(!hashMapSelective.get("dateSecond").isEmpty())
            data = data+ " - "+ hashMapSelective.get("dateSecond");
        if(!hashMapSelective.get("dateThird").isEmpty())
            data = data+ " - "+ hashMapSelective.get("dateThird");

        textDateSelective.setText(data);
        String number = hashMapSelective.get("number")!=null && hashMapSelective.get("number").equals("") ? "" : ", "+hashMapSelective.get("number").toString();
        String address = hashMapSelective.get("street").toString()+number;
        textLocalSelective.setText(address);
        textCityInfoSelective.setText(hashMapSelective.get("city") +"-"+hashMapSelective.get("state"));
        textNeighBorhoodSelective.setText(hashMapSelective.get("neighborhood"));
        textCepInfoSelective.setText(hashMapSelective.get("cep"));
        textObservationsDetails.setText(hashMapSelective.get("notes") != null ? hashMapSelective.get("notes") : " ");
        boolean pay = hashMapSelective.get("pay").equals("Sim") ? true : false;
        textPrice.setText(pay ? "R$ "+hashMapSelective.get("price") : "R$ 0,00");
    }

    private void getTestsChoosesinBD(ArrayList<String> testChooses){
        String strLanguage = Locale.getDefault().getLanguage();
        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<TestTypes> tests = new ArrayList<TestTypes>();
        for (String id : testChooses ) {
            db.openDataBase();
            TestTypes test = db.getTestTypeFromId(id);
            if (strLanguage.equals("en")) {
                test.setName(test.getName().toString()
                        .replace("jardas", "yards")
                        .replace("Salto", "Jump")
                        .replace("Flexão de Braço", "Arm flexion")
                        .replace("Supino", "Supine")
                        .replace("Agachamento", "Squad"));
            } else if (strLanguage.equals("es")) {
                test.setName(test.getName().toString()
                        .replace("jardas", "yardas")
                        .replace("Salto", "Saltar")
                        .replace("Horizontal", "Jump")
                        .replace("Flexão de Braço", "Flexion de Brazo")
                        .replace("Agachamento", "Rechoncho"));
            }
            else if (strLanguage.equals("it")) {
                test.setName(test.getName().toString()
                        .replace("jardas", "cantieri")
                        .replace("Salto", "Saltare")
                        .replace("Horizontal", "Orizzontale")
                        .replace("Flexão de Braço", "Braccio piegato")
                        .replace("Agachamento", "Tozzo"));
            }
            tests.add(test);
            db.close();
        }
        textTestsInfo.setText("Testes ("+tests.size() +")");
        inflateListTests(tests);
    }

    private void inflateListTests(ArrayList<TestTypes> tests){
        String[] values = new String[tests.size()];
        for(int i=0; i<=tests.size()-1; i++)
            values[i] = tests.get(i).getId();

        recyclerTests.setHasFixedSize(true);
        recyclerTests.setItemAnimator(new DefaultItemAnimator());
        recyclerTests.setLayoutManager(new GridLayoutManager(this, 1));
        adapterRecyclerTests = new AdapterRecyclerTestInfo(InfoSelectiveCreateActivity.this, tests, values);
        recyclerTests.setAdapter(adapterRecyclerTests);
    }

    private View.OnClickListener btnShowMoteTeam = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTeamInfo();
        }
    };
    private View.OnClickListener btnShowMoteDetails = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDetails();
        }
    };
    private View.OnClickListener btnShowMoteObservation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {showObservations();
        }
    };
    private View.OnClickListener btnShowMoteTests = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTestsInfo();
        }
    };

    private void showTeamInfo(){
        if(imgTeamChoose.getVisibility()==View.GONE) {
            imgshowMoreTeam.setImageDrawable(this.getDrawable(R.drawable.ic_show_less_line));
            imgTeamChoose.setVisibility(View.VISIBLE);
            textNameTeam.setVisibility(View.VISIBLE);
        }
        else{
            imgshowMoreTeam.setImageDrawable(this.getDrawable(R.drawable.ic_show_more_line));
            textNameTeam.setVisibility(View.GONE);
            imgTeamChoose.setVisibility(View.GONE);
        }
    }

    private void showDetails(){
        if(constraintDetailsInfo.getVisibility()==View.GONE) {
            imgShowMoreDetails.setImageDrawable(this.getDrawable(R.drawable.ic_show_less_line));
            constraintDetailsInfo.setVisibility(View.VISIBLE);
        }
        else{
            imgShowMoreDetails.setImageDrawable(this.getDrawable(R.drawable.ic_show_more_line));
            constraintDetailsInfo.setVisibility(View.GONE);
        }
    }

    private void showObservations(){
        if(textObservationsDetails.getVisibility()==View.GONE) {
            imgShowMoreObservations.setImageDrawable(this.getDrawable(R.drawable.ic_show_less_line));
            textObservationsDetails.setVisibility(View.VISIBLE);
        }
        else{
            imgShowMoreObservations.setImageDrawable(this.getDrawable(R.drawable.ic_show_more_line));
            textObservationsDetails.setVisibility(View.GONE);
        }
    }

    private void showTestsInfo(){
        if(recyclerTests.getVisibility()==View.GONE) {
            imgShowMoreTests.setImageDrawable(this.getDrawable(R.drawable.ic_show_less_line));
            recyclerTests.setVisibility(View.VISIBLE);
        }
        else{
            imgShowMoreTests.setImageDrawable(this.getDrawable(R.drawable.ic_show_more_line));
            recyclerTests.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {InfoSelectiveCreateActivity.this.finish();}
    };

    private View.OnClickListener btnCreateSelectiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callCreateSelective();
        }
    };

    private CheckBox.OnCheckedChangeListener checkChangedListener = new  CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            isAcceptedPrivacy = isChecked;
        }
    };

    private View.OnClickListener textClickedPrivacy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintPrivacy.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener clickedClosePrivacy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintPrivacy.setVisibility(View.GONE);
        }
    };


    /************************* CRIAÇÃO DE SELETIVA ***********/
    private void callCreateSelective(){
        if(isAcceptedPrivacy){
            if(Services.isOnline(this)){
                constraintProgress.setVisibility(View.VISIBLE);
                textProgress.setText(this.getResources().getString(R.string.creating_selective));
                createSelective();
            }
            else
                constraintNotConnection.setVisibility(View.VISIBLE);
        }
        else
            Services.messageAlert(this, this.getResources().getString(R.string.message),this.getResources().getString(R.string.accept_terms_selective),"hide");
    }

    private void createSelective(){
        String url = Constants.URL + Constants.API_SELECTIVES;
        PostCreateSelective post = new PostCreateSelective();
        post.setActivity(InfoSelectiveCreateActivity.this);
        post.setMethod(METHOD_CREATE_SELECTVE);
        post.setObjPut(CreateJSON.createObjectSelective(createObjectSelective()));
        post.execute(url);
    }

     public static void returnCreateSelective(Activity act, String response, String result){
        ((InfoSelectiveCreateActivity)act).returnCreateSelective(response, result);
    }

    private void returnCreateSelective(String response, String result){
        constraintProgress.setVisibility(View.GONE);
        if(response.toUpperCase().equals("OK")){
            DatabaseHelper db = new DatabaseHelper(this);
            DeserializerJsonElements des = new DeserializerJsonElements(result);
            Selective selective = des.getSelective();
            db.deleteTable(Constants.TABLE_SELECTIVES);
            db.addSelective(selective);
            //SelectiveCreatedSuccessActivity.selective = selective;
            //SelectiveCreatedSuccessActivity.team = db.getTeamByIdTeam(selective.getTeam());
            code = selective.getCodeSelective();
            id = selective.getId();
            callPostTestsSelective(selective);
        }
    }

    private void callPostTestsSelective(Selective selective){
        if(Services.isOnline(this)){
            constraintProgress.setVisibility(View.VISIBLE);
            textProgress.setText(this.getResources().getString(R.string.creating_tests_selective));
            createTestsSelective(selective.getId());
        }
        else
            constraintNotConnection.setVisibility(View.VISIBLE);
    }

    private void createTestsSelective(String id){
        String url = Constants.URL + Constants.API_SELECTIVE_TESTTYPES;
        PostCreateSelective post = new PostCreateSelective();
        post.setActivity(InfoSelectiveCreateActivity.this);
        post.setMethod(METHOD_TESTS_SELECTIVE);
        post.setObjPut(CreateJSON.createObjectTestsSelectives(id, testChooses));
        post.execute(url);
    }

    public static void returnCreateTestsSelective(Activity act, String response, String result){
        ((InfoSelectiveCreateActivity)act).returnCreateTestsSelective(response, result);
    }

    private void returnCreateTestsSelective(String response, String result){
        constraintProgress.setVisibility(View.GONE);
        if(response.toUpperCase().equals("OK")){
            try {
                callCreateUserSelective();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callCreateUserSelective(){
        if(Services.isOnline(this)){
            constraintProgress.setVisibility(View.VISIBLE);
            textProgress.setText(this.getResources().getString(R.string.confinguring_using_selective));
            createUserSelective();
        }
        else
            constraintNotConnection.setVisibility(View.VISIBLE);
    }

    private void createUserSelective(){
        String url = Constants.URL + Constants.API_USER_SELECTIVE;
        PostCreateSelective post = new PostCreateSelective();
        post.setActivity(InfoSelectiveCreateActivity.this);
        post.setMethod(METHOD_USER_SELECTIVES);
        post.setObjPut(CreateJSON.createObjectUserSelectives(id, Services.getIdUser(this), true));
        post.execute(url);
    }

    public static void returnCreateUserSelective(Activity act, String status, String result){
        ((InfoSelectiveCreateActivity)act).returnCreateUserSelective(status, result);
    }

    private void returnCreateUserSelective(String status, String result){
        constraintProgress.setVisibility(View.GONE);
        if(status.toUpperCase().equals("OK")){
            try {
                Intent i = new Intent(InfoSelectiveCreateActivity.this, SelectiveCreatedSuccessActivity.class);
                i.putExtra("code", code);
                i.putExtra("id_selective", id);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void returnClickableAlert(Activity act, String whoCalled){
        ((InfoSelectiveCreateActivity)act).returnClickableAlert(whoCalled);
    }

    private void returnClickableAlert(String whoCalled){
        if(whoCalled.equals(messageSelectiveCreate))
            finishAfterCreateSelective();
    }

    private void finishAfterCreateSelective(){
        CreateSelectiveActivity.finishActity();
        ChooseTeamSelectiveActivity.finishActity();
        this.finish();
    }

    private Selective createObjectSelective() {
        String date = Services.convertDate(hashMapSelective.get("date"));
        Selective selective = new Selective();
        selective.setUser(Services.getIdUser(this));
        selective.setTeam(hashMapSelective.get("team"));
        selective.setTitle(hashMapSelective.get("title"));
        selective.setCity(hashMapSelective.get("city"));
        selective.setDate(date);
        selective.setNeighborhood(hashMapSelective.get("neighborhood"));
        selective.setPostalCode(hashMapSelective.get("cep"));
        selective.setState(hashMapSelective.get("state"));
        selective.setStreet(hashMapSelective.get("street"));
        selective.setNotes(hashMapSelective.get("notes").trim().equals("") ? "-":hashMapSelective.get("notes"));
        selective.setAddress(returnAddress());
        selective.setCanSync(true);
        selective.setCodeSelective("");
        boolean pay = hashMapSelective.get("pay").equals(this.getResources().getString(R.string.yes)) ? true : false;
        selective.setPay(pay);
        selective.setPrice(hashMapSelective.get("price"));
        return selective;
    }

    private String returnAddress(){
        return hashMapSelective.get("cep")+" ("+hashMapSelective.get("neighborhood")+
                " - "+hashMapSelective.get("city")+
                ", "+hashMapSelective.get("street")+
                ", "+hashMapSelective.get("number")+
                " - "+hashMapSelective.get("state")+
                ") "+hashMapSelective.get("complement");
    }

      private View.OnClickListener btnClickedTryAgain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
                callCreateSelective();
        }
    };

    private View.OnClickListener clickedCheckBoxTerms = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    public static void finishOhterActivity(){
        ((InfoSelectiveCreateActivity)act).finish();
    }
}
