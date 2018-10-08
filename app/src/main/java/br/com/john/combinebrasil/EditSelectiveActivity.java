package br.com.john.combinebrasil;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.john.combinebrasil.Classes.CEP;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.CreateJSON;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAsyncTask;
import br.com.john.combinebrasil.Connection.Posts.PostCreateSelective;
import br.com.john.combinebrasil.Connection.Posts.PutEditSelectiveAsyncTask;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Mask;
import br.com.john.combinebrasil.Services.Services;

public class EditSelectiveActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTitle, editNotes, editPrice, editCep, editComplement, editCity, editNeighborhood, editState,editStreet, editNumber;
    TextView textDate, textSecondDate, textThirdDate;
    Button btnEditSelective, btnCancelDate, btnConfirmDate, btnConfirmTime, btnCancelTime;
    ImageView imgAddDate, imgAddSecondDate, imgAddThirdDate;
    NumberPicker nPHour, nPminute;
    Switch switchPay;
    String idSelective="";
    public static Activity act;
    MaterialCalendarView calendarDates;
    ConstraintLayout constraintCalendar, constraintTime, constraintNotConnected;
    private int dateClicked = 0;
    Selective selective;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_selective);

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
        textTitle.setText(R.string.edit_selective);

        act = EditSelectiveActivity.this;

        editState = (EditText)  findViewById(R.id.edit_state);
        editCep = (EditText) findViewById(R.id.edit_cep);
        editComplement = (EditText) findViewById(R.id.editComplement);
        editCity = (EditText) findViewById(R.id.edit_city);
        editNeighborhood = (EditText) findViewById(R.id.edit_neighborhood);
        editStreet = (EditText) findViewById(R.id.edit_address);
        editNumber = (EditText) findViewById(R.id.edit_number);

        textDate = (TextView) findViewById(R.id.txt_date);
        textSecondDate = (TextView) findViewById(R.id.txt_date_second);
        textThirdDate = (TextView) findViewById(R.id.txt_date_third);
        imgAddDate = (ImageView) findViewById(R.id.img_add_date);
        imgAddSecondDate = (ImageView) findViewById(R.id.img_add_second_date);
        imgAddThirdDate = (ImageView) findViewById(R.id.img_add_third_date);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editPrice = (EditText) findViewById(R.id.edit_selective_price);
        switchPay = (Switch) findViewById(R.id.switch_pay);

        editNotes = (EditText) findViewById(R.id.edit_notes);
        btnEditSelective = (Button) findViewById(R.id.btn_create_selective);
        constraintCalendar = (ConstraintLayout) findViewById(R.id.constraint_calendar);
        constraintTime = (ConstraintLayout) findViewById(R.id.constraint_time);
        btnCancelDate = (Button) findViewById(R.id.btn_cancel_date);
        btnConfirmDate = (Button) findViewById(R.id.btn_confirm_date);
        btnConfirmTime = (Button) findViewById(R.id.btn_confirm_time);
        btnCancelTime = (Button) findViewById(R.id.btn_cancel_time);

        nPHour = (NumberPicker) findViewById(R.id.number_picker_hour);
        nPminute = (NumberPicker) findViewById(R.id.number_picker_min);
        nPHour.setFormatter("%02d");
        nPminute.setFormatter("%02d");

        calendarDates = (MaterialCalendarView) findViewById(R.id.calendar_dates);
        textDate.setOnClickListener(clickedDateListener);
        textSecondDate.setOnClickListener(clickedSecondDateListener);
        textThirdDate.setOnClickListener(clickedThirdDateListener);
        imgAddSecondDate.setOnClickListener(clickedAddSecondDate);
        imgAddThirdDate.setOnClickListener(clickedAddThirdDate);
        btnCancelDate.setOnClickListener(clickedCancelDate);
        btnConfirmDate.setOnClickListener(clickedConfirmDate);

        btnCancelTime.setOnClickListener(clickCloseTime);
        btnConfirmTime.setOnClickListener(clickConfirmTime);
        textSecondDate.addTextChangedListener(textChangeSecond);

        switchPay.setOnCheckedChangeListener(changedListenerSwitchPay);

        constraintNotConnected = (ConstraintLayout) findViewById(R.id.constraint_not_connection);

        Mask maskCpf = new Mask("#####-###", editCep);
        editCep.addTextChangedListener(maskCpf);
        editCep.addTextChangedListener(textListenerCep);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            idSelective = extras.getString("id_selective");
            getSelectiveInformations(extras.getString("id_selective"));
        }

        imgAddDate.setOnClickListener(clickedAddDate);

        btnEditSelective.setOnClickListener(clickEditSelective);
    }

    private void getSelectiveInformations(String idSelective){
        if(Services.isOnline(this)) {
            String url = Constants.URL + Constants.API_SELECTIVES + "?" + Constants.SELECTIVES_ID + "=" + idSelective;
            Connection task = new Connection(url, 0, Constants.CALLED_GET_INFORMATIONS_SELECTIVES, false, EditSelectiveActivity.this);
            task.callByJsonStringRequest();
        }else
            constraintNotConnected.setVisibility(View.VISIBLE);
    }

    public static void retornagetSelectiveInformations(Activity act, String result, int status){
        ((EditSelectiveActivity)act).retornagetSelectiveInformations(result, status);
    }
    private void retornagetSelectiveInformations(String result, int status){
        if(status == 200 || status == 201) {
            try{
                DeserializerJsonElements des = new DeserializerJsonElements(result);
                selective = des.getOlnySelective();
                JSONArray jsonDate = new JSONArray(selective.getDate().toString());

                if(jsonDate.length() == 1) {
                    textDate.setText(returnDateTime(jsonDate.get(0).toString()));
                }
                else if(jsonDate.length() == 2){
                    textDate.setText(returnDateTime(jsonDate.get(0).toString()));

                    textSecondDate.setText(returnDateTime(jsonDate.get(1).toString()));
                    textDate.setVisibility(View.VISIBLE);
                    textSecondDate.setVisibility(View.VISIBLE);

                    imgAddDate.setVisibility(View.INVISIBLE);
                    imgAddSecondDate.setVisibility(View.VISIBLE);
                    imgAddDate.setImageDrawable(getDrawable(R.drawable.ic_add));
                }
                else if(jsonDate.length() == 3){
                    textDate.setText(returnDateTime(jsonDate.get(0).toString()));
                    textSecondDate.setText(returnDateTime(jsonDate.get(1).toString()));
                    textThirdDate.setText(returnDateTime(jsonDate.get(2).toString()));

                    textDate.setVisibility(View.VISIBLE);
                    textSecondDate.setVisibility(View.VISIBLE);
                    textThirdDate.setVisibility(View.VISIBLE);

                    imgAddDate.setVisibility(View.INVISIBLE);
                    imgAddSecondDate.setVisibility(View.INVISIBLE);
                    imgAddThirdDate.setImageDrawable(getDrawable(R.drawable.ic_less));
                }

                editState.setText(selective.getState());
                editCep.setText(selective.getPostalCode());
                editComplement.setText("");
                editCity.setText(selective.getCity());
                editNeighborhood.setText(selective.getNeighborhood());
                editStreet.setText(selective.getStreet());

                switchPay.setChecked(selective.isPay());
                if(!selective.isPay())
                    editPrice.setVisibility(View.GONE);

                editTitle.setText(selective.getTitle());
                editPrice.setText(selective.isPay() == true ? selective.getPrice() : "");

                editNotes.setText(selective.getNotes());

                searchAddress(editCep.getText().toString());

                verifyADMSelective(idSelective);
            }catch (Exception e){
                Log.i("Exception: ", e.getMessage());}
        }
        else
            Services.messageAlert(this, "Aviso",result,"hide");

    }

    private void verifyADMSelective(String idSelective){
        if(Services.isOnline(this)) {
            this.idSelective = idSelective;
            String url = Constants.URL + Constants.API_USER_SELECTIVE_SEARCH;
            PostAsyncTask post = new PostAsyncTask();
            post.setActivity(EditSelectiveActivity.this);
            post.setObjPut(querySearchData(idSelective));
            post.setWhoCalled(Constants.CALLED_SELECTIVE_ADM);
            post.execute(url);
        }
    }

    public static void retornaADMSelective(Activity act, String result, int status){
        ((EditSelectiveActivity)act).retornaADMSelective(result, status);
    }
    private void retornaADMSelective(String result, int status){
        if(status == 200 || status == 201) {
            try{
                JSONArray des = new JSONArray(result);
                if (des!=null) {
                    boolean admin = des.getJSONObject(0).optBoolean("isSelectiveAdmin", false);
                    if(!admin)
                        Services.messageAlert(this, EditSelectiveActivity.this.getResources().getString(R.string.message), EditSelectiveActivity.this.getResources().getString(R.string.not_have_permission_edit),"");
                }
            }catch (Exception e){
                Log.i("Exception: ", e.getMessage());}
        }
        else
            Services.messageAlert(this, EditSelectiveActivity.this.getResources().getString(R.string.warning),result,"hide");

    }

    private JSONObject querySearchData(String idSelective) {
        JSONObject object = new JSONObject();
        try {
            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put(Constants.USER_SELECTIVE_USER, Services.getIdUser(this));
            jsonQuery.put(Constants.USER_SELECTIVE_SELECTIVE, idSelective);
            object.put("query", jsonQuery);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private boolean verifyFields(){
        boolean ver = true;
        if(!validaEdit(editCep))
            ver = false;
        if(!validaEdit(editCity))
            ver = false;
        if(!validaEdit(editNeighborhood))
            ver = false;
        if(!validaEdit(editStreet))
            ver = false;
        if(!validaEdit(editState))
            ver = false;
        if(!ver)
            Services.messageAlert(this, EditSelectiveActivity.this.getResources().getString(R.string.alert), EditSelectiveActivity.this.getResources().getString(R.string.invalid_data_check_continue),"");
        return ver;
    }

    public boolean validaEdit(EditText edit) {
        boolean ver = false;
        if(getString(edit).length()>=2) {
            Services.changeColorEditBorder(edit, this);
            ver = true;
        }
        else
            Services.changeColorEditBorderError(edit, this);
        return ver;
    }

    private String getString(EditText edit){
        return edit.getText().toString().trim().equals("") ? "" : edit.getText().toString();
    }


    private TextWatcher textListenerCep = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.toString().length()==9){
                searchAddress(editCep.getText().toString());
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void searchAddress(String cep){
        showLoadingAddress();
        String url = Constants.URLCep+cep+"/json/";
        Connection task = new Connection(url, 0, Constants.CALLED_GET_CEP, false, EditSelectiveActivity.this);
        task.callByJsonStringRequest();
    }

    private void showLoadingAddress(){
        editCity.setText(EditSelectiveActivity.this.getResources().getString(R.string.location_city));
        editState.setText(EditSelectiveActivity.this.getResources().getString(R.string.finding));
        editStreet.setText(EditSelectiveActivity.this.getResources().getString(R.string.locatin_street));
        editNeighborhood.setText(EditSelectiveActivity.this.getResources().getString(R.string.locating_neighborhood));
    }

    public static void returnCEP(Activity act, String response, int statusCode){
        ((EditSelectiveActivity)act).returnCEP(response, statusCode);
    }

    private void returnCEP(String response, int statusCode){
        if(statusCode == 200 || statusCode == 201) {
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            CEP cep = des.getCep();
            showAddress(cep);
        }
        else
            showAddressNotFound();
    }

    private void showAddress(CEP cep){
        editStreet.setText(cep.getStreet());
        editCity.setText(cep.getCity());
        editNeighborhood.setText(cep.getNeighborhood());
        editState.setText(cep.getState());
    }

    private void showAddressNotFound(){
        editCity.setText("");
        editState.setText("");
        editStreet.setText("");
        editNeighborhood.setText("");
    }

    private View.OnClickListener clickedBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener clickedDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDate(1);
        }
    };

    private View.OnClickListener clickedSecondDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDate(2);
        }
    };

    private View.OnClickListener clickedThirdDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDate(3);
        }
    };

    private void showDate(int date){
        dateClicked = date;
        constraintCalendar.setVisibility(View.VISIBLE);
        Calendar c = Calendar.getInstance();
        switch (date){
            case 1 :
                if(textDate.getText().length()==0)
                    calendarDates.setSelectedDate(c);
                else{
                    Date d = Services.convertStringInDate(textDate.getText().toString(), "yyyy/MM/dd",'/');
                    c.setTime(d);
                    calendarDates.setSelectedDate(c);
                }
                break;
            case 2 :
                if(textSecondDate.getText().length()==0)
                    calendarDates.setSelectedDate(c);
                else{
                    Date d = Services.convertStringInDate(textSecondDate.getText().toString(), "yyyy/MM/dd",'/');
                    c.setTime(d);
                    calendarDates.setSelectedDate(c);
                }
                break;
            case 3 :
                if(textThirdDate.getText().length()==0)
                    calendarDates.setSelectedDate(c);
                else{
                    Date d = Services.convertStringInDate(textThirdDate.getText().toString(), "yyyy/MM/dd",'/');
                    c.setTime(d);
                    calendarDates.setSelectedDate(c);
                }
                break;
        }
    }


    private View.OnClickListener clickedAddSecondDate = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            clickAddSecondDate();
        }
    };

    private View.OnClickListener clickedAddThirdDate = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            clickAddSThirdDate();
        }
    };

    private void clickAddSThirdDate(){
        textThirdDate.setText("");
        hideDate(textThirdDate, imgAddThirdDate);
        imgAddSecondDate.setVisibility(View.VISIBLE);
        imgAddSecondDate.setImageDrawable(getDrawable(R.drawable.ic_add));
    }

    private void clickAddSecondDate(){
        String text = textSecondDate.getText().toString();
        if(text.equals("")){
            textSecondDate.setText("");
            hideDate(textSecondDate, imgAddSecondDate);
            showDate(textDate, imgAddDate);
            imgAddDate.setImageDrawable(getDrawable(R.drawable.ic_add));
        }
        else{
            showDate(textThirdDate, imgAddThirdDate);
            imgAddSecondDate.setVisibility(View.INVISIBLE);
            imgAddThirdDate.setImageDrawable(getDrawable(R.drawable.ic_less));
        }
    }

    private View.OnClickListener clickedCancelDate = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            constraintCalendar.setVisibility(View.GONE);
        }
    };

    private TextWatcher textChangeSecond = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>6){
                imgAddSecondDate.setImageDrawable(getDrawable(R.drawable.ic_add));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private View.OnClickListener clickedConfirmDate = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            clickConfirmDate();
        }
    };

    private void clickConfirmDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        CalendarDay calendarSelected = calendarDates.getSelectedDate();
        Date date = calendarSelected.getDate();
        if(dateClicked==1) {
            SimpleDateFormat dtForm = new SimpleDateFormat("dd/MM/yyyy");
            Date current = Calendar.getInstance().getTime();
            String reportDate = dtForm.format(current);
            if(verifyDate(reportDate, convertDateCalendar(formatter.format(date)))) {
                textDate.setText(convertDateCalendar(formatter.format(date)));
                imgAddDate.setVisibility(View.VISIBLE);
                imgAddDate.setOnClickListener(clickedAddDate);

                showTimeChoose();
            }
            else
                Toast.makeText(this, EditSelectiveActivity.this.getResources().getString(R.string.date_greater_current_date), Toast.LENGTH_SHORT).show();
        }
        else if(dateClicked==2) {
            if(verifyDate(textDate.getText().toString(), convertDateCalendar(formatter.format(date)))) {
                textSecondDate.setText(convertDateCalendar(formatter.format(date)));
                constraintTime.setVisibility(View.VISIBLE);
                showTimeChoose();
            }
            else
                Toast.makeText(this, EditSelectiveActivity.this.getResources().getString(R.string.second_date_larger_first_date), Toast.LENGTH_SHORT).show();
        }
        else {
            if(verifyDate(textSecondDate.getText().toString(), convertDateCalendar(formatter.format(date)))) {
                textThirdDate.setText(convertDateCalendar(formatter.format(date)));
                constraintTime.setVisibility(View.VISIBLE);
                showTimeChoose();
            }
            else
                Toast.makeText(this, EditSelectiveActivity.this.getResources().getString(R.string.third_greater_second), Toast.LENGTH_SHORT).show();

        }
    }

    private View.OnClickListener clickedAddDate = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            clickAddDate();
        }
    };

    private void clickAddDate(){
        showDate(textSecondDate, imgAddSecondDate);
        imgAddDate.setVisibility(View.INVISIBLE);
        imgAddSecondDate.setImageDrawable(getDrawable(R.drawable.ic_less));
    }

    private void showTimeChoose(){
        nPHour.setValue(0);
        nPminute.setValue(0);

        constraintTime.setVisibility(View.VISIBLE);
    }

    private String convertDateCalendar(String date){
        String month =  date.substring(0,2);
        String day = date.substring(3,5);
        String year = date.substring(6, 10);
        return day + "/"+month+"/"+year;
    }

    private boolean verifyDate(String dtPrincipal,String dtValid){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateP = sdf.parse(dtPrincipal);

            Date strDate = sdf.parse(dtValid);
            if (strDate.after(dateP)) {
                return true;
            }
            else
                return false;
        }catch (Exception ex){
            ex.getStackTrace();
            return false;
        }
    }

    private View.OnClickListener clickCloseTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintTime.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener clickConfirmTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            confirmTime();
        }
    };

    private void confirmTime(){
        String hour = String.format("%02d", nPHour.getValue());
        String minute = String.format("%02d", nPminute.getValue());

        switch (dateClicked){
            case 1 : textDate.setText(textDate.getText().toString()+" - "+ hour+":"+minute);
                break;
            case 2 : textSecondDate.setText(textSecondDate.getText().toString()+" - "+ hour+":"+minute);
                break;
            case 3 : textThirdDate.setText(textThirdDate.getText().toString()+" - "+ hour+":"+minute);
        }

        constraintTime.setVisibility(View.GONE);
        constraintCalendar.setVisibility(View.GONE);
    }

    private void showDate(TextView text, ImageView img){
        text.setVisibility(View.VISIBLE);
        img.setVisibility(View.VISIBLE);
    }
    private void hideDate(TextView text, ImageView img){
        text.setVisibility(View.GONE);
        img.setVisibility(View.GONE);
    }

    private Switch.OnCheckedChangeListener changedListenerSwitchPay = new Switch.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                editPrice.setVisibility(View.VISIBLE);
            else
                editPrice.setVisibility(View.GONE);
        }
    };

    private String returnDateTime(String dateTime){
        String year = dateTime.substring(0, 4);
        String month = dateTime.substring(5, 7);
        String day = dateTime.substring(8, 10);
        String hour = dateTime.substring(11, 16);

        return day + "/" + month + "/" + year+" - "+hour;
    }

    private View.OnClickListener clickEditSelective = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           callCreateSelective();
        }
    };

    private void callCreateSelective(){
        if(Services.isOnline(this)){
//            constraintProgress.setVisibility(View.VISIBLE);
//            textProgress.setText("Criando sua seletiva");
            editSelective();
        }
        else
            constraintNotConnected.setVisibility(View.VISIBLE);
    }

    private void editSelective(){
        String url = Constants.URL + Constants.API_SELECTIVES+"/"+idSelective;
        PutEditSelectiveAsyncTask post = new PutEditSelectiveAsyncTask();
        post.setActivity(EditSelectiveActivity.this);
        post.setMETHOD(Constants.METHOD_EDIT_SELECTIVE);
        post.setObjPut(CreateJSON.createObjectSelective(createObjectSelective()));
        post.execute(url);
    }

    private JSONArray jsonDates(){
        JSONArray jsonDates = new JSONArray();
        try {
            if (!textDate.getText().toString().isEmpty())
                jsonDates.put(Services.convertStringInDate(textDate.getText().toString()));
            if (!textSecondDate.getText().toString().isEmpty())
                jsonDates.put(Services.convertStringInDate(textSecondDate.getText().toString()));
            if (!textThirdDate.getText().toString().isEmpty())
                jsonDates.put(Services.convertStringInDate(textThirdDate.getText().toString()));
            return jsonDates;

        }catch (Exception e){
            Log.i("ERROR: JsonDates", e.getMessage());
            return null;
        }
    }

    private Selective createObjectSelective() {
        selective.setUser(selective.getUser());
        selective.setTitle(editTitle.getText().toString());
        selective.setCity(editCity.getText().toString());
        selective.setJsonDates(jsonDates());
        selective.setNeighborhood(editNeighborhood.getText().toString());
        selective.setPostalCode(editCep.getText().toString());
        selective.setState(editState.getText().toString());
        selective.setStreet(editStreet.getText().toString());
        selective.setNotes(editNotes.getText().toString());
        selective.setAddress(returnAddress());
        selective.setCanSync(true);
        boolean pay = switchPay.isChecked();
        selective.setPay(pay);
        selective.setPrice(pay==true ? "R$ 0,00": editPrice.getText().toString());
        return selective;
    }

    private String returnAddress(){
        return editCep.getText()+" ("+editNeighborhood.getText().toString()+
                " - "+editCity.getText().toString()+
                ", "+editStreet.getText().toString()+
                " - "+editState.getText().toString()+
                ") - "+editComplement.getText().toString();
    }

    public static void returnPutEditSelective(Activity act, String result, String status){
        ((EditSelectiveActivity)act).returnPutEditSelective(result, status);
    }

    private void returnPutEditSelective(String result, String status){
        if(status.equals("OK")){
            try{
                DeserializerJsonElements des = new DeserializerJsonElements(result);
                Selective selective = des.getSelective();
                DatabaseHelper db = new DatabaseHelper(this);
                db.deleteValue(Constants.TABLE_SELECTIVES, selective.getId().toString());
                db.addSelective(selective);

                Services.messageAlert(this, EditSelectiveActivity.this.getResources().getString(R.string.message), getResources().getString(R.string.edit_selective_save_success),"EXIT_EDIT_SELECTIVE");
            }catch (Exception e){
                Services.messageAlert(this, EditSelectiveActivity.this.getResources().getString(R.string.message), getResources().getString(R.string.edit_selective_save_success),"EXIT_EDIT_SELECTIVE");
            }
        }
    }

    public static void returnAlerts(Activity act, String method){
        if(method.equals("EXIT_EDIT_SELECTIVE"))
            act.finish();
    }

}
