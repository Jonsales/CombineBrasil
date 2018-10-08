package br.com.john.combinebrasil;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

public class SelectiveCreatedSuccessActivity extends AppCompatActivity {
    String codeSelective;
    ImageView imageShowCreated;
    TextView  textCode;
    ConstraintLayout constraintCreateSelective;
    Button btnExitShow;
    String id = "";
    public static Selective selective;
    public static Team team;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selective_created_success);

        imageShowCreated = (ImageView) findViewById(R.id.img_created_selective);
        constraintCreateSelective = (ConstraintLayout) findViewById(R.id.constraint_created_selective);
        btnExitShow = (Button) findViewById(R.id.btn_exit_created_selective);
        textCode = (TextView) findViewById(R.id.text_code_selective);
        TextView myTextView=(TextView)findViewById(R.id.text_title_success);
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"Freshman.ttf");
        myTextView.setTypeface(typeFace);
        textCode.setTypeface(typeFace);
        textCode.setOnClickListener(clickedSharedWhatsApp);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            textCode.setText(codeSelective = extras.getString("code"));
            id = extras.getString("id_selective");

            DatabaseHelper db = new DatabaseHelper(this);
            selective = db.getSelective();
            team = db.getTeamByIdTeam(selective.getTeam());
            SharedPreferencesAdapter.setEnterSelectiveSharedPreferences(this, true);
            SharedPreferencesAdapter.setValueStringSharedPreferences(this, Constants.SELECTIVES_CODESELECTIVE, textCode.getText().toString());
            SharedPreferencesAdapter.setValueStringSharedPreferences(this, Constants.SELECTIVES_ID_ENTER, id);
        }
        clockwise(myTextView);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        constraintCreateSelective.startAnimation(anim);
        btnExitShow.setOnClickListener(clickedBackToMenu);
    }

    @Override
    public void onBackPressed(){
    }

    private View.OnClickListener clickedBackToMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickToMain();
        }
    };

    private void clickToMain(){
        InfoSelectiveCreateActivity.finishOhterActivity();
        TestSelectiveActivity.finishOhterActivity();
        CreateSelectiveActivity.finishOhterActivity();
        LocalSelectiveActivity.finishActity();
        ChooseTeamSelectiveActivity.finishActity();
        MenuActivity.finishActity();

        Intent i = new Intent(SelectiveCreatedSuccessActivity.this, MainActivity.class);
        i.putExtra("id_selective",id);
        i.putExtra("INIT_SELECTIVE", true);
        AllActivities.isSync = true;
        startActivity(i);
        finish();
    }

    private void clockwise(TextView text){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        text.startAnimation(animation);
    }

    private View.OnClickListener clickedSharedWhatsApp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharedWhatsApp();
        }
    };

    private void sharedWhatsApp(){
        Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        try {
            String msg = this.getResources().getString(R.string.congratulations_evaluate)
                    .replace("{seletiva}", selective.getTitle())
                    .replace("{date_selective}", Services.convertDate( selective.getDate()))
                    .replace("{CODE}",selective.getCodeSelective().toString().toUpperCase())
                    .replace("{EQUIPE}", team.getName());

            whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg
            );
            startActivity(Intent.createChooser(whatsappIntent, "Share image using"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

}

