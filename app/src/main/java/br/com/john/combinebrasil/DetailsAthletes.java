package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

public class DetailsAthletes extends AppCompatActivity {
    Toolbar toolbar;
    TextView textName;
    Button buttonEdit;
    Bundle extras;
    String idAthlete;
    Athletes athlete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_athletes);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);

        textName = (TextView) findViewById(R.id.text_name_details);
        buttonEdit = (Button) findViewById(R.id.button_edit);
        buttonEdit.setOnClickListener(clickEditAthlete);

        DatabaseHelper db = new DatabaseHelper(DetailsAthletes.this);
        User user = db.getUser();
        if(user!=null){
            if(user.getIsAdmin())
                buttonEdit.setVisibility(View.VISIBLE);
            else
                buttonEdit.setVisibility(View.GONE);
        }
        extras = getIntent().getExtras();
        if(extras != null) {
            idAthlete = extras.getString("id_player");
            showInfoAthlete(idAthlete);
        }


    }
    @Override
    public void onRestart(){
        super.onRestart();
        showInfoAthlete(idAthlete);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void showInfoAthlete(String id){
        DatabaseHelper db  = new DatabaseHelper(DetailsAthletes.this);
        db.openDataBase();
        athlete = db.getAthleteById(id);

        Positions position = db.getPositiomById(athlete.getDesirablePosition());

        String pos = "";

        if(position!=null)
            pos = position.getDESCRIPTION();
        else
            procuraPosicao(athlete.getDesirablePosition());

        mostraDetalhes(athlete, pos);
    }
    private void mostraDetalhes(Athletes athlete, String pos){
        TextView text = (TextView) findViewById(R.id.text_details_athlete);
        textName.setText(athlete.getName());
        text.setText(DetailsAthletes.this.getResources().getString(R.string.nascimento)+ " "+ Services.convertDate(athlete.getBirthday())+ "\n"+
                DetailsAthletes.this.getResources().getString(R.string.cpf) +" "+athlete.getCPF() +"\n"+
                DetailsAthletes.this.getResources().getString(R.string.address) +" "+athlete.getAddress() +"\n"+
                DetailsAthletes.this.getResources().getString(R.string.position) +" "+pos +"\n"+
                DetailsAthletes.this.getResources().getString(R.string.altura) +" "+String.format("%.2f", athlete.getHeight()).replace(".",",") +"\n"+
                DetailsAthletes.this.getResources().getString(R.string.peso) +" "+String.format("%.0f",athlete.getWeight()).replace(".",",")+" Kg");
    }

    private View.OnClickListener clickEditAthlete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editAthlete();
        }
    };

    private void editAthlete(){
        Intent intent = new Intent(DetailsAthletes.this, CreateAccountAthleteActivity.class);
        intent.putExtra("EditAthlete", true);
        intent.putExtra("id_player",extras.getString("id_player"));
        startActivity(intent);

    }

    private void procuraPosicao(String id){
        String url = Constants.URL+Constants.API_POSITIONS+"?"+Constants.POSITIONS_ID+"="+id;
        Connection con = new Connection(url, 0, "PROCURA_POSICAO", false, this);
        con.callByJsonStringRequest();
    }
    public static void retornaProcuraPosicao(Activity act, String response, int status){
        ((DetailsAthletes)act).retornaProcuraPosicao(response, status);
    }
    private void retornaProcuraPosicao(String response, int status){
        DeserializerJsonElements des = new DeserializerJsonElements(response);
        Positions posicao = des.getPosicao();
        if(posicao==null)
            posicao = des.getPositions().get(0);
        mostraDetalhes(athlete, posicao.getDESCRIPTION());
    }
}
