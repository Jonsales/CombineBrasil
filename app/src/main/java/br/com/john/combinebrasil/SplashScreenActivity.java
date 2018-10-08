package br.com.john.combinebrasil;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        try {
                PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent;
                if(!SharedPreferencesAdapter.getValueStringSharedPreferences(SplashScreenActivity.this, Constants.UPDATE_APP).equals("true")){

                    if(SharedPreferencesAdapter.getLoggedSharedPreferences(SplashScreenActivity.this)) {
                        DatabaseHelper db = new DatabaseHelper(SplashScreenActivity.this);
                        ArrayList<Team> team = db.getTeams();
                        User user = db.getUser();
                        if(SharedPreferencesAdapter.getEnterSelectiveSharedPreferences(SplashScreenActivity.this)) {
                            AllActivities.isSync = false;
                            mainIntent = new Intent(SplashScreenActivity.this, MenuActivity.class);
                            mainIntent.putExtra("entrar_seletiva",SharedPreferencesAdapter.getValueStringSharedPreferences(SplashScreenActivity.this, Constants.SELECTIVES_CODESELECTIVE));

                        }
                        else
                            mainIntent = new Intent(SplashScreenActivity.this, MenuActivity.class);


                        try {
                            db.deleteDatabase();
                            db.createDataBase();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(team!=null)
                            db.addTeam(team);
                        if(user!=null)
                            db.addUser(user);
                    }
                    else {
                        AllActivities.isSync = true;
                        mainIntent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                    }

                    SharedPreferencesAdapter.setValueStringSharedPreferences(SplashScreenActivity.this, Constants.UPDATE_APP, "true");
                    startActivity(mainIntent);
                    finish();
                }

                else {
                    if (SharedPreferencesAdapter.getLoggedSharedPreferences(SplashScreenActivity.this)) {
                        if (SharedPreferencesAdapter.getEnterSelectiveSharedPreferences(SplashScreenActivity.this)) {
                            AllActivities.isSync = false;
                            mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        } else
                            mainIntent = new Intent(SplashScreenActivity.this, MenuActivity.class);
                    } else {
                        AllActivities.isSync = true;
                        mainIntent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                    }
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }
}

