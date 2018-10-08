package br.com.john.combinebrasil.Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.CreateAccountAthleteActivity;
import br.com.john.combinebrasil.CreateTeamActivity;
import br.com.john.combinebrasil.CronometerOnlyOneActivity;
import br.com.john.combinebrasil.EditSelectiveActivity;
import br.com.john.combinebrasil.EditTeamActivity;
import br.com.john.combinebrasil.InfoSelectiveCreateActivity;
import br.com.john.combinebrasil.LoginActivity;
import br.com.john.combinebrasil.MainActivity;
import br.com.john.combinebrasil.MenuActivity;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.RegisterActivity;
import br.com.john.combinebrasil.ResultsOnlyOneActivity;
import br.com.john.combinebrasil.TestSelectiveActivity;
import br.com.john.combinebrasil.TimerActivity;

/**
 * Created by GTAC on 18/10/2016.
 */
public class Services {
    private static Activity activity = null;
    private static AlertDialog alerta;
    private static String whoCalled="";

    public static void messageAlert(Activity act, String title, String message, String whoCalled){
        Services.whoCalled = whoCalled;
        Services.activity=act;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        View view = act.getLayoutInflater().inflate(R.layout.alert_message, null);
        builder.setView(view);

        TextView textTitle = (TextView) view.findViewById(R.id.text_title_message);
        textTitle.setText(title);

        TextView textAlert = (TextView) view.findViewById(R.id.text_alert_message);
        textAlert.setText(message);
        Button nbutton = (Button) view.findViewById(R.id.button_ok_alert_message);
        LinearLayout linear = (LinearLayout) view.findViewById(R.id.linear_message_ok);

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.hide();
            }
        });
        nbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedOkAlert();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    private static void clickedOkAlert(){
        alerta.hide();
        if(whoCalled.toUpperCase().equals("HIDE") || whoCalled.equals(""))
            alerta.hide();
        else if(whoCalled.toUpperCase().equals("DIALOGSAVECRONOMETER")){
            if(activity.getClass().getSimpleName().equals("CronometerOnlyOneActivity"))
                CronometerOnlyOneActivity.finished(activity);
        }
        else if(whoCalled.toUpperCase().equals("DIALOGSAVERESULTS")){
            if(activity.getClass().getSimpleName().equals("ResultsOnlyOneActivity"))
                ResultsOnlyOneActivity.finished(activity);
            else if(activity.getClass().getSimpleName().equals("TimerActivity"))
                TimerActivity.finished(activity);
        }
        else if(whoCalled.toUpperCase().equals("POSTATHLETE")){
            CreateAccountAthleteActivity.finished(activity);
        }
        else if (whoCalled.equals("updateAthelete"))
            CreateAccountAthleteActivity.update(activity);
        else if(activity.getClass().getSimpleName().equals(Constants.TIMER_ACTIVITY))
            TimerActivity.returnOption(activity, whoCalled);
        else if (whoCalled.equals("exit")){
            MainActivity.returnMessageOptions(activity, whoCalled);
        }
        else if (activity.getClass().getSimpleName().equals("LoginActivity"))
            LoginActivity.returnMessage(activity, whoCalled);
        else if (activity.getClass().getSimpleName().equals("RegisterActivity"))
            RegisterActivity.returnMessage(activity, whoCalled);

        else if(activity.getClass().getSimpleName().equals("CreateTeamActivity"))
            CreateTeamActivity.returnMessage(activity, whoCalled);
        else if(activity.getClass().getSimpleName().equals("EditTeamActivity"))
            EditTeamActivity.returnMessage(activity, whoCalled);
        else if(activity.getClass().getSimpleName().equals("TestSelectiveActivity")){
            TestSelectiveActivity.returnClickableAlert(activity, whoCalled);
        }
        else if(activity.getClass().getSimpleName().equals("InfoSelectiveCreateActivity")){
            InfoSelectiveCreateActivity.returnClickableAlert(activity, whoCalled);
        }

        else if(activity.getClass().getSimpleName().equals("MenuActivity")){
            if(whoCalled.equals("CODE_OK"))
                MenuActivity.returnMessageAlert(activity, whoCalled);
        }
        else if (activity.getClass().getSimpleName().equals("EditSelectiveActivity")){
            EditSelectiveActivity.returnAlerts(activity, whoCalled);
        }
    }

    public static boolean isOnline(Activity act) {
        ConnectivityManager cm =
                (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void changeColorEdit(EditText edit, String title, String mensagem, Activity act){
        edit.setBackground(act.getResources().getDrawable(R.drawable.background_edit_error));
        messageAlert(act, title, mensagem, "hide");
    }
    public static void changeColorEditBorderError(EditText edit, Activity act){
        edit.setBackground(act.getResources().getDrawable(R.drawable.background_edit_border_error));
    }
    public static void changeColorEditBorder(EditText edit, Activity act){
        edit.setBackground(act.getResources().getDrawable(R.drawable.background_edit));
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap pBitmap) {

        int width = 1000;
        int height = pBitmap.getHeight() * 1000 / pBitmap.getWidth();

        Bitmap bitmap = Bitmap.createScaledBitmap(pBitmap, width, height, true);

        int heightDiff = (width - height) / 4;
        height = width;
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = Constants.colorWhite;
        final Paint paint = new Paint();
        final Rect rect = new Rect(2, 2, width, height);

        final RectF rectF = new RectF(rect);
        final float roundPx = width / 4;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, 2, heightDiff, paint);

        return output;
    }

    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }



    public static String mask(String format, String text){
        String maskedText="";
        int i =0;
        for (char m : format.toCharArray()) {
            if (m != '#') {
                maskedText += m;
                continue;
            }
            try {
                maskedText += text.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }
        return maskedText;
    }

    public static String verifyQualification(float rating){
        int num = (int)rating;
        String ret = "";
        switch(num){
            case 1: ret = "Muito baixo";
                break;
            case 2: ret = "Baixo";
                break;
            case 3: ret = "Medio";
                break;
            case 4: ret = "Alto";
                break;
            case 5: ret = "Muito Alto";
                break;
        }
        return ret;
    }

    public static boolean convertIntInBool(int value){
        return (value == 1) ? true : false;
    }

    public  static int convertBoolInInt(boolean value){
        return (value == true) ? 1 : 0;
    }

    public static boolean isConnectectInWifi(Activity act){
        boolean ret = false;
        ConnectivityManager connManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            ret = true;
        }
        return ret;
    }

    private static String fixEncoding(String response) {
        try {
            byte[] u = response.toString().getBytes(
                    "ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return " ";
        }
        try {
            return response.replace("�", "").replace("?", "").replace("~", "").replace("'", "");
        }catch (Exception e){
            return " ";
        }
    }

    public static String convertDate(String date){
        String dateNow = "";
        try {
            for (int i = 0; i <= date.length() - 1; i++) {
                String c = date.substring(i, i + 1);
                if (c.equals("T")) {
                    break;
                } else {
                    c = removeCaracteresSpecial(c);
                    dateNow = dateNow.concat(c);
                }
            }

            String year = dateNow.substring(0, 4);
            String month = dateNow.substring(5, 7);
            String day = dateNow.substring(8, 10);
            return day + "/" + month + "/" + year;
        }catch (Exception e){
            return date;
        }
    }

    public static String convertStringInDate(String dateStrg){
        try {
            String day = dateStrg.substring(0, 2);
            String month = dateStrg.substring(3, 5);
            String year = dateStrg.substring(6, 10);

            String hour = dateStrg.substring(13, 15);
            String minut = dateStrg.substring(16, dateStrg.length());
            return year + "-" + month + '-' + day +" "+hour+":"+minut;
        }catch (Exception e){
            return  "";
        }
    }

    private static String removeCaracteresSpecial(String text){
        text = text.replace(",","").replace(".","").replace("]","").replace("[","").replace("\"","").replace(" ","");
        return text;
    }

    public static long convertMetersinCentimeters(String meters){
        return Long.parseLong(meters.replace(",",""));
    }

    public static String convertCentimetersinMeters(long centimeters){
        String ret = "";
        try{
            String value = String.valueOf(centimeters);

            if(value.length()>=3){
                String met="", cent="";
                met = value.substring(0,1);
                cent = value.substring(1);
                ret = met+","+cent;
            }
            else{
                ret = "0,"+value;
            }

        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }

        return ret;
    }

    public static String convertWeight(double value){
        String height="";
        if(value>0){
            String strValue = String.valueOf(value).substring(0, String.valueOf(value).indexOf("."));
            if(strValue.length()>2){
                height = strValue.substring(0,1)+","+strValue.substring(1,strValue.length());
            }
        }
        return height;
    }

    public static String convertInTime(long millis){
        String time="";
        try {
       SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SS");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        time = formatter.format(calendar.getTime()).toString();


            String min = time.substring(0,2);
            if(min.equals("00")){
                time = time.substring(3);
            }
        }catch(Exception e){
            e.printStackTrace();
            return "00:00";
        }

        return time;
        //return minutes+":"+seconds+":"+mil;
    }

    public static long convertInMinute(long mili ){
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mili);
        String time = formatter.format(calendar.getTime()).toString();
        String min = time.substring(0,2);
        return Long.parseLong(min);
    }

    public static long convertInSecond(long mili){

        return (mili / 1000) % 60;
    }

    public static long convertInMilliSeconds(String time){
        long milisseconds = 0;
        int cont =0;
        try {
            for (int i = 0; i <= time.length() - 1; i++) {
                if (time.charAt(i) == ':')
                    cont = cont + 1;
            }
        }catch(Exception e){
            return milisseconds;
        }
        if(cont == 1) {
            long sec = Integer.parseInt(time.substring(0, 2));
            long mil = Integer.parseInt(time.substring(3));

            long t = (sec * 1000) + (mil*10);

            milisseconds = t;
        }

        if(cont == 2) {
            try {
                long min = Integer.parseInt(time.substring(0, 2));
                String ti = time.substring(6);
                long sec = Integer.parseInt(time.substring(3, 5));
                long mil = Integer.parseInt(time.substring(6));

                long t = ((min * 60L)*1000) + (sec * 1000) + (mil * 10);

                milisseconds = t;
            }catch (Exception e){
                return milisseconds;
            }
        }

        return milisseconds;
    }

    public static NotificationCompat.Builder buildNotificationCommon(final Activity _context) {
        Log.i("NOTIFICATION","SOUNDS");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(_context)
                .setWhen(System.currentTimeMillis());
        //Vibration
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Ton
        builder.setSound(notification);

        return builder;
    }

    public static ArrayAdapter<String> inflateSpinnerDay(Activity activity ){
        String[] spinnerDayValues = new String[31];
        for(int i=0; i<=30;i++){
            int num = i+1;
            if(num<10)
                spinnerDayValues[i] = String.valueOf("0"+num);
            else
                spinnerDayValues[i] = String.valueOf(num);
        }
        ArrayAdapter<String> arrayAdapterDay = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, spinnerDayValues);
        return arrayAdapterDay;
    }

    public static ArrayAdapter<String> inflateSpinnerMonth(Activity activity){
        String[] spinnerMonthValues  = {
                activity.getResources().getString(R.string.january),
                activity.getResources().getString(R.string.february),
                activity.getResources().getString(R.string.march),
                activity.getResources().getString(R.string.april),
                activity.getResources().getString(R.string.may),
                activity.getResources().getString(R.string.june),
                activity.getResources().getString(R.string.july),
                activity.getResources().getString(R.string.august),
                activity.getResources().getString(R.string.september),
                activity.getResources().getString(R.string.octomber),
                activity.getResources().getString(R.string.november),
                activity.getResources().getString(R.string.december),
        };
        ArrayAdapter<String> arrayAdapterMonth = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, spinnerMonthValues);
        return arrayAdapterMonth;
    }

    public static ArrayAdapter<String> inflateSpinnerYear(Activity activity){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        String[] spinnerYearsValues = new String[200];

        for(int x=199; x>=0; x--){
            spinnerYearsValues[x]=String.valueOf(year-x);
        }

         ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, spinnerYearsValues);

        return arrayAdapterYear;
    }

    public static String chooseMonth(String month, Activity mActivity){
        if(month.equalsIgnoreCase("01") ||month.equalsIgnoreCase("1"))
            return mActivity.getResources().getString(R.string.january);
        else if(month.equalsIgnoreCase("02")||month.equalsIgnoreCase("2"))
            return mActivity.getResources().getString(R.string.february);
        else if(month.equalsIgnoreCase("03")||month.equalsIgnoreCase("3"))
            return mActivity.getResources().getString(R.string.march);
        else if(month.equalsIgnoreCase("04")||month.equalsIgnoreCase("4"))
            return mActivity.getResources().getString(R.string.april);
        else if(month.equalsIgnoreCase("05")||month.equalsIgnoreCase("5"))
            return mActivity.getResources().getString(R.string.may);
        else if(month.equalsIgnoreCase("06")||month.equalsIgnoreCase("6"))
            return mActivity.getResources().getString(R.string.june);
        else if(month.equalsIgnoreCase("07")||month.equalsIgnoreCase("7"))
            return mActivity.getResources().getString(R.string.july);
        else if(month.equalsIgnoreCase("08")||month.equalsIgnoreCase("8"))
            return mActivity.getResources().getString(R.string.august);
        else if(month.equalsIgnoreCase("09")||month.equalsIgnoreCase("9"))
            return mActivity.getResources().getString(R.string.september);
        else if(month.equalsIgnoreCase("10"))
            return mActivity.getResources().getString(R.string.octomber);
        else if(month.equalsIgnoreCase("11"))
            return mActivity.getResources().getString(R.string.november);
        else if(month.equalsIgnoreCase("12"))
            return mActivity.getResources().getString(R.string.december);
        else
            return "";
    }

    public static boolean isValidEmail(EditText edit, Activity act) {
        CharSequence target = edit.getText().toString().trim();
        boolean ret = false;
        if (TextUtils.isEmpty(target)) {
            Services.changeColorEditBorderError(edit, act);
            ret =  false;
        } else {
            ret =  android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            if(!ret)
                Services.changeColorEditBorderError(edit, act);
            else
                Services.changeColorEditBorder(edit, act);
        }
        return ret;
    }

    public static Date convertStringInDate(String dateStrg, String strFormat, char divide){
        try {
            String day =  dateStrg.substring(0,2);
            String month = dateStrg.substring(3,5);
            String year = dateStrg.substring(6,10);
            String date =  year + divide+month+divide+day;

            SimpleDateFormat formatter = new SimpleDateFormat(strFormat);

            Date d = (Date) formatter.parse(date);
            return d;

        } catch (ParseException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static String getIdUser(Activity act){
        DatabaseHelper db = new DatabaseHelper(act);
        User user = db.getUserByToken(SharedPreferencesAdapter.getValueStringSharedPreferences(act, Constants.USER_TOKEN));
        return user.getId();
    }

    public static String getCurrentDateTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT0:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        // you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));

        return date.format(currentLocalTime);
    }

    public static String addTestsValues(Activity act, String id, String value){
        String values = "";

        DatabaseHelper db = new DatabaseHelper(act);
        Tests test = db.getTestsFromId(id);

        //returnValues(test.getValues());

        return test.getValues()+value+"/";
    }

    public static String[] returnValues(String values){

        String[] separated = values.split("/");

        return separated;
    }

    public static String convertHtml(String body, String imgDesc){
        String css = "<!DOCTYPE HTML><html lang=\"pt-br\"><style>body{text-align:center;background-color:rgba(0,74,131,255);color:white;}img{height:100%;width:100%;}</style><body><div id=\"div-text\">*text-desc*</div></body></html>";
        String img = "<img src=\"*img-url*\"/><br>";
        img=img.replace("*img-url*",imgDesc);
        if(body.contains("*img-desc*"))
            body = body.replace("*img-desc*", img);
        return css.replace("*text-desc*",body);
    }

    public static String removeAccents(String text){
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        return text.replaceAll("[^\\p{ASCII}]", "");
    }
}
