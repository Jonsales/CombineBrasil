package br.com.john.combinebrasil.Connection;

/**
 * Created by GTAC on 18/10/2016.
 */


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;


public class Connection extends AppCompatActivity {

    private String login;
    private String password;
    private Activity activity;
    private RequestQueue rq; // precisa add o volley como modulo, e marca-lo como dependencia, para isso precisa baixa-lo
    private HashMap<String, String> params;
    public String url;
    public View view;
    private int METHOD;
    private String whoCalled;
    private boolean isList;

    public Connection(String url, int METHOD, String whoCalled, boolean isList, Activity activity){
        this.url = url;
        this.METHOD = METHOD;
        this.whoCalled = whoCalled;
        this.isList = isList;
        this.activity = activity;

        rq = Volley.newRequestQueue(activity);
    }
    public Connection(String url, int METHOD, String whoCalled, boolean isList, Activity activity, HashMap<String, String> params){
        this.url = url;
        this.METHOD = METHOD;
        this.whoCalled = whoCalled;
        this.activity = activity;
        this.params = params;
        this.isList = isList;

        rq = Volley.newRequestQueue(activity);
    }

    public void callByJsonStringRequest(){
        Log.i("URL", url);
        StringRequest request = new StringRequest(METHOD, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ReturnResponse.getInstance().goTo(whoCalled, response, isList, getActivity(), 200);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            ReturnError.getInstance().goTo(whoCalled, getActivity(), getMessage(error), error.networkResponse.statusCode);
                        }catch (Exception e){
                            Services.messageAlert(activity, getActivity().getResources().getString(R.string.api_unavailable),  getActivity().getResources().getString(R.string.try_again_later), "" );
                        }
                    }
                    public String getMessage(VolleyError error){
                        try{
                            String mensagem = new String(error.networkResponse.data);
                            return mensagem;
                        }catch (Exception e){
                            return  getActivity().getResources().getString(R.string.api_unavailable)+". "+getActivity().getResources().getString(R.string.try_again_later);
                        }
                    }
                }){


            //parametros mandados no body, normalmente quando usa POST
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                return params;
            }


            //authorizações necessárias
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                String base64EncodedCredentials = "Basic " + Base64.encodeToString(((
//                                getLogin() != null ? getLogin() : Preferences.getPreferece("login", getActivity()))
//                                + ":" +
//                                (getPassword() != null ? getPassword() : Preferences.getPreferece("password", getActivity()))).getBytes(),
//                        Base64.NO_WRAP);
                HashMap<String, String> header = new HashMap<String, String>();
                header.put("Content-Type", "application/json; charset=utf-8");
                //header.put("Content-Type","application/x-www-form-urlencoded");
                header.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
                header.put("app-authorization", Constants.AUTHENTICATION);

                String userToken = SharedPreferencesAdapter.getValueStringSharedPreferences(getActivity(), Constants.USER_TOKEN);

                if (userToken != null) {
                    Log.i("userToken",userToken);
                    header.put("authorization", userToken);
                }

                return header;
            }

        };

        //tempo para enviar outra requisição
        //se deixar normal, ele irá enviar várias requisições caso falhe a primeira
        //o problema é ao debugar ou em demora ele tenta fazer a mesma coisa mais de uma vez
       /* request.setRetryPolicy(
                new DefaultRetryPolicy(
                        999999999,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        request.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("tag");
        rq.add(request);


    }



    //impedir multiplas requisições desnecessarias
    public void onStop(){
        super.onStop();
        rq.cancelAll("tag");
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMETHOD() {
        return METHOD;
    }

    public void setMETHOD(int METHOD) {
        this.METHOD = METHOD;
    }

    public String getWhoCalled() {
        return whoCalled;
    }

    public void setWhoCalled(String whoCalled) {
        this.whoCalled = whoCalled;
    }
}