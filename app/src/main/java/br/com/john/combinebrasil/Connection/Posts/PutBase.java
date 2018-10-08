package br.com.john.combinebrasil.Connection.Posts;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.toolbox.HttpClientStack;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import br.com.john.combinebrasil.CreateAccountAthleteActivity;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

/**
 * Created by John on 29/08/2017.
 */

public class PutBase extends AsyncTask<String, String, String> {
    protected Activity activity;
    private JSONObject objPut;
    String METHOD= "";
    int statusCode=0;
    String resp = "";
    String result = "";

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        HttpClient client;

        client = new DefaultHttpClient(httpParams);

        HttpClientStack.HttpPatch post = new HttpClientStack.HttpPatch(params[0]); //strings[0] == url

        post.setHeader("content-type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("Accept-Charset","utf-8");
        post.setHeader("app-authorization", Constants.AUTHENTICATION);

        String userToken = SharedPreferencesAdapter.getValueStringSharedPreferences(activity, Constants.USER_TOKEN);

        if (userToken != null && !userToken.equals("")) {
            Log.i("userToken",userToken);
            post.setHeader("authorization", userToken);
        }
        else
            post.setHeader("authorization", Constants.AUTHORIZATHION);

        StringEntity entity = null;
        try {
            entity = new StringEntity(objPut.toString(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        try {
            post.setEntity(entity);
            HttpResponse response = null;
            response = client.execute(post);
            statusCode =response.getStatusLine().getStatusCode();
            if(statusCode==201 || statusCode ==200)
                resp="OK";
            else
                resp="FAIL";

            InputStream inputStream  = response.getEntity().getContent();

            result = convertInputStreamToString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            resp = "FAIL";
        }
        return resp;
    }

    protected void onProgressUpdate(String... progress) { }

    @Override
    protected void onPostExecute(String status) {
    }

    public String getMETHOD() {
        return METHOD;
    }

    public void setMETHOD(String METHOD) {
        this.METHOD = METHOD;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public void setObjPut(JSONObject objPut){
        this.objPut = objPut;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
