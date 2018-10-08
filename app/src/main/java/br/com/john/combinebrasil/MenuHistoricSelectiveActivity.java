package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.ImageConverter;
import br.com.john.combinebrasil.Services.ImageLoadedCallback;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

public class MenuHistoricSelectiveActivity extends AppCompatActivity {
    public static Selective SELECTIVE_CLICKED;
    public static boolean enterSelective=false;
    Toolbar toolbar;
    ImageView imageItemPlayers, imageItemRanking,imageTeam;
    Selective selective;
    Team team;
    TextView textNameTeam, textNameSelective, textPriceSelective, textDateSelective, textAddressSelective,
            textObservationSelective, textSubscribers, textReport, textUrlInscription, txtCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_historic_selective);

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
        imgSearch.setVisibility(View.GONE);
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(R.string.historic);

        imageItemPlayers = (ImageView) findViewById(R.id.image_item_jogadores);
        imageItemRanking = (ImageView) findViewById(R.id.image_item_ranking);
        imageTeam = (ImageView) findViewById(R.id.image_team);

        textNameTeam = (TextView) findViewById(R.id.text_team);
        textNameSelective = (TextView) findViewById(R.id.text_name_selective_details);
        textPriceSelective = (TextView) findViewById(R.id.text_price_selective);
        textDateSelective = (TextView) findViewById(R.id.text_date_selective_details);
        textAddressSelective = (TextView) findViewById(R.id.text_local_selective_details);
        textObservationSelective = (TextView) findViewById(R.id.text_observation_selective_details);
        textSubscribers = (TextView) findViewById(R.id.text_subscribers_selective_details);
        textReport = (TextView) findViewById(R.id.text_donwload_report);
        textUrlInscription = (TextView) findViewById(R.id.text_url_inscription);
        txtCode = (TextView) findViewById(R.id.text_code_selective);


        textReport.setOnClickListener(clickTextReport);
        textUrlInscription.setOnClickListener(clickTextInscrptionURL);
        textUrlInscription.setOnLongClickListener(longListenerCopyURL);

        imageItemPlayers.setOnClickListener(clickedPlayers);
        imageItemRanking.setOnClickListener(clickedGrade);

        callInfoSelective();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        double heightMax = width/2.85;
        Float.parseFloat(String.format("%.0f",heightMax));
        int height = (int) heightMax;

        imageItemPlayers.getLayoutParams().height = height;
        imageItemPlayers.setMinimumHeight(height);
        imageItemPlayers.setMaxHeight(height);

        imageItemRanking.getLayoutParams().height = height;
        imageItemRanking.setMinimumHeight(height);
        imageItemRanking.setMaxHeight(height);

        try{
            String strLanguage = Locale.getDefault().getLanguage();

            if(strLanguage.equals("en")){
                imageItemPlayers.setImageResource(R.drawable.item_menu_jogadores_ingles);
               // imageItemRanking.setImageResource(R.drawable.item_menu_notas_ingles);
            }
            else if(strLanguage.equals("es")){
                imageItemPlayers.setImageResource(R.drawable.item_menu_jogadores_espanhol);
               // imageItemRanking.setImageResource(R.drawable.item_menu_notas_espanhol);
            }
            else if(strLanguage.equals("it")){ //Italiano
                imageItemPlayers.setImageResource(R.drawable.item_menu_jogadores_espanhol);
            }
        }catch (Exception ex){

        }
    }

    private void callInfoSelective(){
        if(Services.isOnline(this)){
            hideNotConnect();
            this.selective = SELECTIVE_CLICKED;
            getTeamSelective();
        }
        else
            showToNoConnect();
    }

    private void getTeamSelective(){
        showProgress(getString(R.string.verify_selective));
        if(Services.isOnline(this)){
            hideNotConnect();
            String url = Constants.URL + Constants.API_TEAMS+"?"+Constants.TEAM_ID+"="+this.selective.getTeam();
            Connection task = new Connection(url, 0, Constants.CALLED_GET_TEAM, false, MenuHistoricSelectiveActivity.this);
            task.callByJsonStringRequest();
        }
        else
            showToNoConnect();
    }

    public static void returnGetTeamSelective(Activity act, String resp, int status){
        ((MenuHistoricSelectiveActivity)act).returnGetTeamSelective(resp, status);
    }

    private void returnGetTeamSelective(String resp, int status){
        hideProgress();
        if(status == 200 || status == 201){
            DeserializerJsonElements des = new DeserializerJsonElements(resp);
            ArrayList<Team> teams = des.getTeam();
            if(teams!= null && teams.size()>0) {
                team = teams.get(0);
                showDetailsSelective();
                getNumberSubscribers();
            }
        }
    }

    private void getNumberSubscribers(){
        showProgress(getString(R.string.verify_selective));
        if(Services.isOnline(this)){
            hideNotConnect();
            String url = Constants.URL + Constants.API_SELECTIVEATHLETES+"?selective="+this.selective.getId();
            Connection task = new Connection(url, 0, Constants.CALLED_GET_SUBSCRIBERS, false, MenuHistoricSelectiveActivity.this);
            task.callByJsonStringRequest();
        }
        else
            showToNoConnect();
    }

    public static void returnGetSubscriber(Activity act, String response, int status){
        ((MenuHistoricSelectiveActivity)act).returnGetSubscriber(response, status);
    }

    private void returnGetSubscriber(String result, int status){
        hideProgress();
        if(status == 200 || status == 201){
            try {
                JSONArray json = new JSONArray(result);
                textSubscribers.setText(json.length()+" "+this.getResources().getString(R.string.athletes_enrolled));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    private void showToNoConnect(){
        TextView textMessage = (TextView) findViewById(R.id.txt_message_not_connect);
        textMessage.setText(Html.fromHtml(getString(R.string.no_connection)));
        ConstraintLayout constraintNoConnect = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintNoConnect.setVisibility(View.VISIBLE);
        Button btnTryAgain =(Button) findViewById(R.id.btn_try_again_connect);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callInfoSelective();
            }
        });
    }

    private View.OnClickListener clickedPlayers = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            showNextScreen(0);
        }
    };

    private View.OnClickListener clickedGrade = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            showNextScreen(1);
        }
    };

    private void showNextScreen(int choose){
        Intent intent = null;
        switch (choose){
            case 0 :
                intent = new Intent(this, HistoricPlayersSelectiveActivity.class);
                intent.putExtra("id_selective", selective.getId());
                intent.putExtra("id_team", team.getId());
                startActivity(intent);
                break;
            case 1 :
                showChooseRanking();
                break;
        }
    }

    private void showChooseRanking(){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_choose_ranking);
        Button button = (Button)findViewById(R.id.button_cancel_ranking);
        constraint.setVisibility(View.VISIBLE);
        button.setOnClickListener(hideChooseRaking);

        ConstraintLayout constraintTests = (ConstraintLayout) findViewById(R.id.constraint_ranking_tests);
        ConstraintLayout constraintPosition = (ConstraintLayout) findViewById(R.id.constraint_ranking_positions);
        constraintTests.setOnClickListener(clickedRankingTests);
        constraintPosition.setOnClickListener(clickedRankingPositions);

    }

    private View.OnClickListener hideChooseRaking = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_choose_ranking);
            constraint.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener clickedRankingTests = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickChooseRanking(0);
        }
    };
    private View.OnClickListener clickedRankingPositions = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickChooseRanking(1);
        }
    };

    private void clickChooseRanking(int choose){
        Intent intent = new Intent();
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_choose_ranking);
        constraint.setVisibility(View.GONE);
        switch (choose){
            case 0 :
                intent = new Intent(this, HistoricRankingTestsActivity.class);
                intent.putExtra("id_selective", selective.getId());
                intent.putExtra("id_team", team.getId());
                startActivity(intent);
                break;
            case 1 :
                intent = new Intent(this, HistoricRankingPositionsActivity.class);
                intent.putExtra("id_selective", selective.getId());
                intent.putExtra("id_team", team.getId());
                startActivity(intent);
                break;
        }
    }

    private View.OnClickListener clickedBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void showDetailsSelective(){
        textNameTeam.setText(team.getName());
        textNameSelective.setText(selective.getTitle());
        textPriceSelective.setText(selective.isPay() == true? this.getResources().getString(R.string.free_subscriptions).replace("{quantidade}", String.valueOf(selective.getPrice()).replace(".",",")): this.getResources().getString(R.string.free_subscriptions));
        textAddressSelective.setText(selective.getCity());
        textDateSelective.setText(returnDateTime(selective.getDate()));
        textObservationSelective.setText(selective.getNotes());

        //String codeSeletive = SharedPreferencesAdapter.getValueStringSharedPreferences(this, Constants.SELECTIVES_CODESELECTIVE);

        txtCode.setText(this.getResources().getString(R.string.your_code) + ": "+selective.getCodeSelective()+" ("+this.getResources().getString(R.string.click_to_share)+")");
        txtCode.setOnClickListener(clickCodeShared);
        txtCode.setOnLongClickListener(locnLinstenerCode);
        showTeam(team);
    }
    private View.OnClickListener clickCodeShared = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sharedWhatsApp();
        }
    };

    private void sharedWhatsApp(){
        Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        try {
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, this.getResources().getString(R.string.congratulations_evaluate)
                                .replace("{seletiva}", selective.getTitle())
                                .replace("{date_selective}", Services.convertDate( selective.getDate()))
                                .replace("{CODE}",selective.getCodeSelective().toString().toUpperCase())
                                .replace("{EQUIPE}", team.getName()));

//            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Parabéns, você foi convocado para avaliar na "+selective.getTitle()+", na(s) data(s) "
//                    +Services.convertDate( selective.getDate())+". Baixe o aplicativo Combine Brasil Analytics na GooglePlay e logue na seletiva utilizando o código: "+selective.getCodeSelective().toString().toUpperCase()+"\n"
//                    +"Para maiores informações, entre em contato com o representante do "+team.getName()+"\n\n  "
//                    +"Combine Brasil - Descobrindo o melhor do atleta \n\nhttp://combinebrasil.com/"
//            );
            startActivity(Intent.createChooser(whatsappIntent, "Share image using"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String returnDateTime(String dateTime){
        dateTime = dateTime.replace("[","").replace("]","");
        String year = dateTime.substring(1, 5);
        String month = dateTime.substring(6, 8);
        String day = dateTime.substring(9, 11);
        String hour = dateTime.substring(12, 17);

        return day + "/" + month + "/" + year+" às "+hour+" horas";
    }

    private void showTeam(Team team){
        String urlImage = "http://www.combinebrasil.com/img/og-ima.png";
        if(team.getUrlImage() != null && !(team.getUrlImage().equals("")))
            urlImage = team.getUrlImage();
        showImageTeam(urlImage);
    }

    private void showImageTeam(String urlImage){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_image_team);
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(urlImage)
                .into(imageTeam,  new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                            roundedImage();
                        }
                    }
                    @Override
                    public void onError(){
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void roundedImage(){
        Bitmap bitmap = ((BitmapDrawable)imageTeam.getDrawable()).getBitmap();
        bitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 200);
        imageTeam.setImageBitmap(bitmap);
    }

    private View.OnClickListener clickTextReport = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = Constants.URL+"selectives/"+selective.getId()+"/renderResult";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };

    private View.OnClickListener clickTextInscrptionURL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = Constants.URL_INSCRPITION_ATHLETES_SELECTIVE+selective.getId();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };

    private View.OnLongClickListener longListenerCopyURL = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            String url = Constants.URL_INSCRPITION_ATHLETES_SELECTIVE+selective.getId();
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(url);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", url);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(MenuHistoricSelectiveActivity.this, MenuHistoricSelectiveActivity.this.getResources().getString(R.string.link_copied), Toast.LENGTH_SHORT).show();
            return true;
        }
    };
    private View.OnLongClickListener locnLinstenerCode = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            String code = selective.getCodeSelective();
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(code);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", code);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(MenuHistoricSelectiveActivity.this, MenuHistoricSelectiveActivity.this.getResources().getString(R.string.code_copied), Toast.LENGTH_SHORT).show();
            return true;
        }
    };
}
