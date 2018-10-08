package br.com.john.combinebrasil;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.SelectiveAthletes;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostAthleteAsyncTask;
import br.com.john.combinebrasil.Connection.Posts.PutAthlete;
import br.com.john.combinebrasil.Connection.Posts.PutBase;
import br.com.john.combinebrasil.Connection.Posts.PutTests;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Mask;
import br.com.john.combinebrasil.Services.MaskHeight;
import br.com.john.combinebrasil.Services.MessageOptions;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;
import br.com.john.combinebrasil.Services.SingleUploadBroadcastReceiver;

public class CreateAccountAthleteActivity extends AppCompatActivity  implements SingleUploadBroadcastReceiver.Delegate {
    MaterialBetterSpinner spinnerDay, spinnerMonth, spinnerYear, spinnerPosition;
    Toolbar toolbar;
    EditText editTextName, editTextCPF, editTextPhone, editTextHeight, editTextWeihgt, editEmail, editAddress;
    private Button buttonAdd;
    Athletes athlete;
    ArrayList<Positions> positions;
    TextView textTerms, bodyTextTerms;
    CheckBox checkTerms;
    Button btnClose, btnCropImage, btnCancelCrop;
    ConstraintLayout constraintTerms, constraintNoConnection, constraintProgress;
    boolean checked = false, editAthlete = false;
    String idAthlete;
    TextView textProgress;
    private ImageView imageAthlete;
    private CropImageView imageCrop;
    private Uri mCropImageUri;
    private boolean isCropImage = false;
    private static String op="";
    private java.io.File File;
    String code="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_athlete);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);

        constraintNoConnection = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintNoConnection.setOnClickListener(clickedVerifyConnection);
        textProgress = (TextView) findViewById(R.id.text_progress);
        editTextName = (EditText) findViewById(R.id.edit_name_add);
        editTextCPF = (EditText) findViewById(R.id.edit_cpf_add);
        editTextPhone = (EditText) findViewById(R.id.edit_phone_add);
        editTextHeight = (EditText) findViewById(R.id.edit_height_add);
        editTextWeihgt = (EditText) findViewById(R.id.edit_weight_add);
        editEmail = (EditText) findViewById(R.id.edit_email_add);
        editAddress = (EditText) findViewById(R.id.edit_address_add);

        btnCropImage = (Button) findViewById(R.id.btn_crop_image);
        btnCancelCrop = (Button) findViewById(R.id.btn_cancel_crop);

        buttonAdd = (Button) findViewById(R.id.button_add_athlete);
        btnCropImage.setOnClickListener(clickedCropImageOk);
        btnCancelCrop.setOnClickListener(clickedImageCropCancel);
        spinnerDay  = (MaterialBetterSpinner) findViewById(R.id.spinner_day_birthday_add);
        spinnerMonth = (MaterialBetterSpinner) findViewById(R.id.spinner_month_birthday_add);
        spinnerYear = (MaterialBetterSpinner) findViewById(R.id.spinner_year_birthday_add);
        spinnerPosition = (MaterialBetterSpinner) findViewById(R.id.spinner_positions_add);
        spinnerPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPosition.setFocusable(true);
                spinnerPosition.setSelectAllOnFocus(true);
                spinnerPosition.setShowSoftInputOnFocus(false);
                spinnerPosition.setNestedScrollingEnabled(true);
            }
        });

        imageAthlete = (ImageView) findViewById(R.id.img_team);
        imageCrop = (CropImageView) findViewById(R.id.cropimage);

        imageCrop.setAspectRatio(1200,1200);
        imageCrop.setFixedAspectRatio(true);
        imageCrop.setAutoZoomEnabled(true);

        imageAthlete.setOnClickListener(clickedChangeImage);

        textTerms = (TextView) findViewById(R.id.text_terms);
        checkTerms = (CheckBox) findViewById(R.id.check_terms);
        btnClose = (Button) findViewById(R.id.button_close);
        constraintTerms = (ConstraintLayout) findViewById(R.id.constraint_temrs);

        textTerms.setOnClickListener(clickOpenTerms);
        btnClose.setOnClickListener(closeTerms);

        bodyTextTerms = (TextView) findViewById(R.id.text_terms_body);
        String strTermos = this.getResources().getString(R.string.terms_of_use).toString().replace("{BREAKLINE}","<br>");
        bodyTextTerms.setText(Html.fromHtml(strTermos));

        verifyConnection();

        textTerms.setVisibility(View.VISIBLE);

        checkTerms.setChecked(false);
        checkTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkTerms.isChecked())
                    checked = true;

                else
                    checked = false;

            }
        });

        inflateSpinnerDay();
        getAllPositions();

        try{
            String strLanguage = Locale.getDefault().getLanguage();

            if(strLanguage.equals("pt")) {
                Mask maskCpf = new Mask("###.###.###-##", editTextCPF);
                editTextCPF.addTextChangedListener(maskCpf);

                Mask maskPhone = new Mask("(##)#####-####", editTextPhone);
                editTextPhone.addTextChangedListener(maskPhone);

            }
        }catch (Exception ex){

        }


        TextWatcher mask = MaskHeight.insert("#,##", editTextHeight);
        editTextHeight.addTextChangedListener(mask);

        mask = MaskHeight.insert("###",editTextWeihgt);
        editTextWeihgt.addTextChangedListener(mask);

        buttonAdd.setOnClickListener(addAthleteClicked);

        if(Constants.debug)
            buttonAdd.setOnLongClickListener(longClick);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            editAthlete = extras.getBoolean("EditAthlete");
            if(editAthlete) {
                idAthlete = extras.getString("id_player");
                editAthlete();
                checkTerms.setChecked(true);
                checkTerms.setVisibility(View.GONE);
                checked = true;

            }
        }
    }
    private View.OnClickListener clickedVerifyConnection = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            verifyConnection();
        }
    };

    private void verifyConnection(){
        if(Services.isOnline(CreateAccountAthleteActivity.this)){
            constraintNoConnection.setVisibility(View.GONE);
        }
        else{
            constraintNoConnection.setVisibility(View.VISIBLE);
        }
    }

    private void editAthlete(){
        DatabaseHelper db = new DatabaseHelper(CreateAccountAthleteActivity.this);
        Athletes athlete = db.getAthleteById(idAthlete);
        code = athlete.getCode();
        checked = true;
        textTerms.setVisibility(View.GONE);
        editTextName.setText(athlete.getName());
        editTextCPF.setText(athlete.getCPF());
        editEmail.setText(athlete.getEmail().trim());
        editTextPhone.setText(athlete.getPhoneNumber());
        editTextHeight.setText(String.format("%.2f", athlete.getHeight()).replace(".",","));
        editTextWeihgt.setText(String.format("%.0f",athlete.getWeight()).replace(".",","));

        String urlImage = athlete.getURLImage();
        if(!urlImage.isEmpty()) {
            Picasso.with(this)
                    .load(urlImage)
                    .fit()
                    .centerCrop()
                    .error(this.getDrawable(R.drawable.icon_picture))
                    .into(imageAthlete);
        }
        Positions position = db.getPositiomById(athlete.getDesirablePosition());
        if(position!=null)
            spinnerPosition.setText(position.getNAME());
        else
            spinnerPosition.setText("");
        String birthday = Services.convertDate(athlete.getBirthday());
        if(!birthday.isEmpty()){
            String day = birthday.substring(0,2);
            String month =Services.chooseMonth(birthday.substring(3,5), CreateAccountAthleteActivity.this);
            String year = birthday.substring(6);

            spinnerDay.setText(day);
            spinnerMonth.setText(month);
            spinnerYear.setText(year);
        }
        else{
            spinnerDay.setText("");
            spinnerMonth.setText("");
            spinnerYear.setText("");
        }
    }

    private View.OnLongClickListener longClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            editTextName.setText("Atleta");
            editTextCPF.setText("43242343243");
            editEmail.setText("atleta@atletinha.com");
            editTextPhone.setText("12988888888");

            editTextHeight.setText("190");
            editTextWeihgt.setText("90");

            return true;
        }
    };

    private View.OnClickListener addAthleteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callAddAthlete();
        }
    };

    private void callAddAthlete(){
       if(verifyForm()) {
           if(validaBirthday()) {
               if (checked == true) {
                   if (Services.isOnline(this)) {
                       ConstraintLayout progress = (ConstraintLayout) findViewById(R.id.constraint_progress);
                       progress.setVisibility(View.VISIBLE);
                       TextView textProgress = (TextView) findViewById(R.id.text_progress);
                       if(editAthlete){

                           textProgress.setText(getResources().getString(R.string.editing_athlete));
                           String url = Constants.URL + Constants.API_ATHLETES+"/"+idAthlete;
                           PutTests post = new PutTests();
                           post.setActivity(CreateAccountAthleteActivity.this);
                           post.setObjPut(createObject());
                           post.execute(url);
                       }else {
                           textProgress.setText(getResources().getString(R.string.add_account));
                           String url = Constants.URL + Constants.API_ATHLETES;
                           PostAthleteAsyncTask post = new PostAthleteAsyncTask();
                           post.setActivity(CreateAccountAthleteActivity.this);
                           post.setObjPut(createObject());
                           post.setPlay(true);
                           post.execute(url);
                       }
                   } else
                       new MessageOptions(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.are_you_offline),"createUserOff");

               }
           }
           else
               Services.messageAlert(CreateAccountAthleteActivity.this,  CreateAccountAthleteActivity.this.getResources().getString(R.string.warning), CreateAccountAthleteActivity.this.getResources().getString(R.string.accept_terms_use), "");
       }
    }

    public static void createAthleteOff (Activity act){
        ((CreateAccountAthleteActivity)act).createAthleteOff();
    }

    private void createAthleteOff(){
        try{
            Athletes athlete = createAthlete();
            SelectiveAthletes selectiveAthlete = createSelectiveAthletes(athlete);
            athlete.setCode(selectiveAthlete.getInscriptionNumber());

            DatabaseHelper db = new DatabaseHelper(CreateAccountAthleteActivity.this);
            long res = db.addAthlete(athlete);
            if (res == 0) {
                Athletes obj = db.getAthleteByValue(Constants.ATHLETES_CPF, athlete.getCPF());
                if(obj != null)
                    Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.user_cpf_registered), "");
                else{
                    obj = db.getAthleteByValue(Constants.ATHLETES_EMAIL, athlete.getEmail());
                    if(obj != null)
                        Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.email_alreadt_registered), "");
                    else{
                        obj = db.getAthleteByValue(Constants.ATHLETES_CODE, athlete.getCode());
                        if(obj != null)
                            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.code_already_registered), "");
                    }
                    Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message),  CreateAccountAthleteActivity.this.getResources().getString(R.string.erro_register), "");
                }
            }

            db.addSelectiveAthlete(selectiveAthlete);
            String msg  = CreateAccountAthleteActivity.this.getResources().getString(R.string.athlete_created).replace("{CODE_INSCRIPTION}",selectiveAthlete.getInscriptionNumber());

            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.completed), msg, "");
            clearForm();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private SelectiveAthletes createSelectiveAthletes(Athletes athlete){
        SelectiveAthletes selectiveAthlete;
        try {
            DatabaseHelper db = new DatabaseHelper(CreateAccountAthleteActivity.this);
            Selective selective = db.getSelective();
            ArrayList<SelectiveAthletes> selectivesAthletes = db.getSelectivesAthletes();
            String numCode = "";
            if (selectivesAthletes == null)
                numCode = "01";
            else {
                int num;
                try {
                    num = selectivesAthletes.size() + 1;
                } catch (NullPointerException e) {
                    num = 1;
                }
                numCode = String.valueOf(num);
                if (num < 9)
                    numCode = "0" + num;
            }

            String nameSelective = selective.getTitle().toString().toUpperCase().substring(0, 2) + "-" + numCode;


            selectiveAthlete = new SelectiveAthletes(
                    UUID.randomUUID().toString(),
                    athlete.getId(),
                    selective.getId(),
                    nameSelective,
                    true
            );
        }catch (Exception e){
            return  null;
        }

        return selectiveAthlete;
    }

    private Athletes createAthlete(){
        Athletes athletes;
        try {
            String address = " ";
            String birthday = spinnerYear.getText().toString() + "-" + chooseMonth(spinnerMonth.getText()
                    .toString(), CreateAccountAthleteActivity.this) + "-" + spinnerDay.getText().toString();

            double height = Double.parseDouble(editTextHeight.getText().toString().replaceAll(",", "."));
            double weight = Double.parseDouble(editTextWeihgt.getText().toString().replaceAll(",", "."));

            //if (!(editAddress.getText().toString().trim().isEmpty()))
              //  address = editAddress.getText().toString();

            athletes = new Athletes(
                    UUID.randomUUID().toString(),
                    editTextName.getText().toString(),
                    birthday,
                    editTextCPF.getText().toString(),
                    address,
                    getIdPosition(spinnerPosition),
                    height,
                    weight,
                    " ",
                    " ",
                    " ",
                    editEmail.getText().toString().trim(),
                    editTextPhone.getText().toString(),
                    false,
                    true,
                    " "
                    );
        }
        catch (Exception e){
            return null;
        }
        return athletes;

    }

    private JSONObject createObject() {
        JSONObject object = new JSONObject();
        try {

            try {
                double height = Double.parseDouble(editTextHeight.getText().toString().replaceAll(",", "."));
                double weight = Double.parseDouble(editTextWeihgt.getText().toString().replaceAll(",", "."));
                String birthday = spinnerYear.getText().toString() + "-" + chooseMonth(spinnerMonth.getText().toString(), CreateAccountAthleteActivity.this) + "-" + spinnerDay.getText().toString();
                object.put(Constants.ATHLETES_NAME, editTextName.getText().toString());
                object.put(Constants.ATHLETES_CPF, editTextCPF.getText().toString());
                object.put(Constants.ATHLETES_PHONE, editTextPhone.getText().toString());
                object.put(Constants.ATHLETES_DESIRABLE_POSITION, getIdPosition(spinnerPosition));
                object.put(Constants.ATHLETES_HEIGHT, height);
                object.put(Constants.ATHLETES_WEIGHT, weight);
                object.put(Constants.ATHLETES_BIRTHDAY, birthday);
                object.put(Constants.ATHLETES_TERMSACCEPTED, true);
                object.put(Constants.ATHLETES_EMAIL, editEmail.getText().toString().trim());
                if(editAddress.getText().toString().trim().isEmpty())
                    object.put(Constants.ATHLETES_ADDRESS, " ");
                else
                    object.put(Constants.ATHLETES_ADDRESS, editAddress.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return object;
    }

    private void crateCode(String idAthlete){
        String url = Constants.URL + Constants.API_SELECTIVEATHLETES;
        PostAthleteAsyncTask post = new PostAthleteAsyncTask();
        post.setActivity(CreateAccountAthleteActivity.this);
        post.setObjPut(createJsonSelectiveAthlete(idAthlete));
        post.setPlay(false);
        post.execute(url);
    }

    private JSONObject createJsonSelectiveAthlete(String id){
        try{
            JSONObject jsonObject = new JSONObject();

            DatabaseHelper db = new DatabaseHelper(this);
            Selective selective = db.getSelective();
            jsonObject.put(Constants.SELECTIVEATHLETES_ATHLETE, id);
            jsonObject.put(Constants.SELECTIVEATHLETES_SELECTIVE, selective.getId());
            jsonObject.put(Constants.SELECTIVEATHLETES_PRESENCE, true);

            return jsonObject;

        }catch(Exception e){
            Log.i("Exception", e.getMessage());
        }
        return null;
    }

    private String getIdPosition(MaterialBetterSpinner spinner) {
        String positionSelected = "";
        try {
            positionSelected = spinner.getText().toString();
            if(!positionSelected.trim().equals("")){
                for(Positions position : positions){
                    if(positionSelected.trim().equals(position.getNAME())){
                        positionSelected = position.getID();
                        return position.getID();
                    }
                }
            }
        }catch (Exception e){
            return "";
        }
        return positionSelected;
    }

    private String getString(EditText edit){
        return edit.getText().toString().trim().equals("") ? "" : edit.getText().toString();
    }

    public static void afterSendAthlete(Activity act, String ret, String response){
        ((CreateAccountAthleteActivity)act).afterPost(ret, response);
    }

    private void afterPost(String ret, String response){
        ConstraintLayout linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        linearProgress.setVisibility(View.GONE);
            if(ret.equals("FAIL"))
                verifyErrorCreate(response);

            else if(ret.equals("OK")) {
                if (isCropImage) {
                    try {
                        op = response;
                        JSONObject json = new JSONObject(response);
                        String id = json.getString(Constants.ATHLETES_ID);
                        String team = json.getString(Constants.ATHLETES_NAME);
                        uploadImageTeam(id, team);
                    }catch(JSONException jex){
                        Log.i("Json exception", jex.getMessage());
                    }
                }else{
                    saveAthlete(response);
                }
            }
    }

    private void uploadImageTeam(String id, String team){
        String url = Constants.URL+Constants.API_ATHLETES+"/"+id+"/upload";
        Uri uri = saveImage(team);
        if(uri!=null) {
            uploadMultipart(uri, url);
        }

    }

    private Uri saveImage(String nameImage){
        Bitmap bm = ((BitmapDrawable)imageAthlete.getDrawable()).getBitmap();
        OutputStream fOut = null;
        Uri outputFileUri = null;
        try {
            java.io.File file = new java.io.File((this
                    .getApplicationContext().getFileStreamPath(nameImage+".jpg")
                    .getPath()));

            outputFileUri = Uri.fromFile(file);
            fOut = new FileOutputStream(file);
            this.File = file;
        } catch (Exception e) {
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.i("Excepsion", e.getMessage());
        }
        return outputFileUri;
    }

    private void verifyErrorCreate(String result){
        JSONObject json;
        try{
            json = new JSONObject(result);
            String detail = json.getString("detail");
            json = new JSONObject(detail);
            if(json.getInt("code") ==  11000)
                saveAthlete(json.getString("op"));
            else
                Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.not_registered_athlete)+"\n" + result, "");

        }catch(JSONException e){
            e.printStackTrace();
            try {
                json = new JSONObject(result);
                Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.not_registered_athlete)+"\n" + json.getString("message"), "");
            } catch (JSONException e1) {
                Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.not_registered_athlete)+"\n" + result, "");
                e1.printStackTrace();
            }
        }
    }

    private void updateAthlete(String response){
        String url = Constants.URL + Constants.API_ATHLETES+"?"+Constants.ATHLETES_ID+"="+idAthlete;
        Connection task = new Connection(url, 0, "updateAthleteAccount",false, CreateAccountAthleteActivity.this);
        task.callByJsonStringRequest();

        ConstraintLayout linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        linearProgress.setVisibility(View.VISIBLE);
    }
    public static void returnAccountAthlete(Activity act, String response){
        ((CreateAccountAthleteActivity)act).returnAccountAthlete(response);
    }

    private void returnAccountAthlete(String response){
        ConstraintLayout linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        linearProgress.setVisibility(View.GONE);

        DeserializerJsonElements des = new DeserializerJsonElements(response);
        athlete = des.getAthlete();
        if(athlete!=null){
            DatabaseHelper db = new DatabaseHelper(CreateAccountAthleteActivity.this);
            SelectiveAthletes seletive = db.getSelectiveAthletesFromAthlete(athlete.getId());
            athlete.setCode(seletive.getInscriptionNumber());
            db.updateAthlete(athlete);
        }

        Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.updated_athlete),"updateAthelete");
    }

    private void saveAthlete(String response){
        DeserializerJsonElements des = new DeserializerJsonElements(response);
        athlete = des.getObjAthlete();
        if(!editAthlete)
            crateCode(athlete.getId());
        else
            callGetAthleteDetails(athlete.getId());
    }

    private void callGetAthleteDetails(String id){
        ConstraintLayout linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        linearProgress.setVisibility(View.VISIBLE);

        String url = Constants.URL+Constants.API_ATHLETES+"?"+Constants.ATHLETES_ID+"="+id;
        Connection con = new Connection(url, 0, "GET_DETAILS_ATHLETES", false, this);
        con.callByJsonStringRequest();
    }

    public static void returnDetailsAthletes(String response, Activity act, int status){
        ((CreateAccountAthleteActivity)act).returnDetailsAthletes(response, status);
    }
    private void returnDetailsAthletes(String response, int status){
        ConstraintLayout linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        linearProgress.setVisibility(View.GONE);
        if(status==200 || status == 201){
            DatabaseHelper db = new DatabaseHelper(this);
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            Athletes athlete = des.getAthlete();
            athlete.setCode(code);
            if(editAthlete) {
                db.updateAthlete(athlete);
                Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.updated_athlete),"updateAthelete");
            }
            else {
                Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.saved), CreateAccountAthleteActivity.this.getResources().getString(R.string.athlete_successfully_registered).replace("{CODE_ATHLETE}", athlete.getCode()), "POSTATHLETE");
            }
        }
        else
            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.not_registered_athlete)+"\n" + response, "");
    }

    public static void afterSendSelectiveAthlete(Activity act, String response, String result){
        ((CreateAccountAthleteActivity)act).afterSendSelectiveAthlete(response, result);
    }

    private void afterSendSelectiveAthlete(String response, String result){
        ConstraintLayout linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        linearProgress.setVisibility(View.GONE);
        if(response.equals("FAIL")) {
            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.message), CreateAccountAthleteActivity.this.getResources().getString(R.string.not_registered_athlete)+"\n" + result, "");
        }
        else if(response.equals("OK")) {
            DeserializerJsonElements des = new DeserializerJsonElements(result);
            SelectiveAthletes item = des.getSelectiveAthlete();
            if(item==null)
                item = des.getObjSelectiveAthlete();

            DatabaseHelper db = new DatabaseHelper(CreateAccountAthleteActivity.this);
            db.addSelectiveAthlete(item);

            code=item.getInscriptionNumber();

            callGetAthleteDetails(athlete.getId());
        }
    }

    private void clearForm(){
        imageAthlete.setImageDrawable(this.getDrawable(R.drawable.icon_picture));
        editTextName.setText("");
        editTextCPF.setText("");
        editAddress.setText("");
        editEmail.setText("");
        editTextPhone.setText("");
        editTextWeihgt.setText("");
        editTextHeight.setText("");
        spinnerPosition.setText("");
        spinnerMonth.setText("");
        spinnerYear.setText("");
        spinnerDay.setText("");
        textTerms.setVisibility(View.VISIBLE);
        checkTerms.setChecked(false);
        checked = false;
    }

    public static void update(Activity act){
        ((CreateAccountAthleteActivity)act).update();
    }

    private void update(){
        this.finish();
    }

    public static void finished(Activity act){
        ((CreateAccountAthleteActivity)act).finished();
    }

    public void finished(){
        clearForm();
    }

    private String chooseMonth(String month, Activity activity){
        if(month.equalsIgnoreCase(activity.getString(R.string.january)))
            return "01";
        else if(month.equalsIgnoreCase(activity.getString(R.string.february)))
            return "02";
        else if(month.equalsIgnoreCase(activity.getString(R.string.march)))
            return "03";
        else if(month.equalsIgnoreCase(activity.getString(R.string.april)))
            return "04";
        else if(month.equalsIgnoreCase(activity.getString(R.string.may)))
            return "05";
        else if(month.equalsIgnoreCase(activity.getString(R.string.june)))
            return "06";
        else if(month.equalsIgnoreCase(activity.getString(R.string.july)))
            return "07";
        else if(month.equalsIgnoreCase(activity.getString(R.string.august)))
            return "08";
        else if(month.equalsIgnoreCase(activity.getString(R.string.september)))
            return "09";
        else if(month.equalsIgnoreCase(activity.getString(R.string.octomber)))
            return "10";
        else if(month.equalsIgnoreCase(activity.getString(R.string.november)))
            return "11";
        else if(month.equalsIgnoreCase(activity.getString(R.string.december)))
            return "12";
        else
            return "";
    }

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreateAccountAthleteActivity.this.finish();
        }
    };

    private void getAllPositions(){
        String positions = SharedPreferencesAdapter.getValueStringSharedPreferences(this, "POSITIONS");
        if(positions==null||!positions.equals("OK")||positions.equals("")) {
            if (Services.isOnline(this)) {
                ConstraintLayout progress = (ConstraintLayout) findViewById(R.id.constraint_progress);
                progress.setVisibility(View.VISIBLE);
                TextView textProgress = (TextView) findViewById(R.id.text_progress);
                textProgress.setText("Configurando posições");
                String url = Constants.URL + Constants.API_POSITIONS;
                Connection con = new Connection(url, 0, Constants.CALLED_GET_POSITIONS, false, this);
                con.callByJsonStringRequest();
            } else
                constraintNoConnection.setVisibility(View.VISIBLE);
        }
        else
            inflateSpinnerPosition();
    }

    public static void returnGetAllPositions(Activity act, String response, int status){
        ((CreateAccountAthleteActivity)act).returnGetAllPositions(response, status);
    }

    private void returnGetAllPositions(String response, int status){
        ConstraintLayout progress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        progress.setVisibility(View.GONE);
        if(status == 200 || status == 201){
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            DatabaseHelper db = new DatabaseHelper(this);
            db.addPositions(des.getPositions());
            SharedPreferencesAdapter.setValueStringSharedPreferences(this, "POSITIONS", "OK");
            inflateSpinnerPosition();
        }
        else
            Services.messageAlert(this, CreateAccountAthleteActivity.this.getResources().getString(R.string.error), CreateAccountAthleteActivity.this.getResources().getString(R.string.error_trying_positions), "");
    }

    private void inflateSpinnerPosition(){
        DatabaseHelper db = new DatabaseHelper(CreateAccountAthleteActivity.this);
        positions = new ArrayList<Positions>();
        positions = db.getPositions();

        if(positions!=null){
            String [] adapter = new String[positions.size()];
            for (int i=0; i<=positions.size()-1;i++)
                adapter[i] = positions.get(i).getNAME();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, adapter);
            spinnerPosition.setAdapter(arrayAdapter);
        }
    }

    private void inflateSpinnerDay(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        String[] spinnerDayValues = new String[31];
        String[] spinnerMonthValues  = {
                CreateAccountAthleteActivity.this.getResources().getString(R.string.january),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.february),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.march),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.april),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.may),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.june),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.july),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.august),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.september),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.octomber),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.november),
                CreateAccountAthleteActivity.this.getResources().getString(R.string.december),
        };
        String[] spinnerYearsValues = new String[200];
        for(int i=0; i<=30;i++){
            int num = i+1;
            if(num<10)
                spinnerDayValues[i] = String.valueOf("0"+num);
            else
                spinnerDayValues[i] = String.valueOf(num);
        }
        for(int x=199; x>=0; x--){
            spinnerYearsValues[x]=String.valueOf(year-x);
        }

        ArrayAdapter<String> arrayAdapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerDayValues);
        ArrayAdapter<String> arrayAdapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerMonthValues);
        ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerYearsValues);

        spinnerDay.setAdapter(arrayAdapterDay);
        spinnerMonth.setAdapter(arrayAdapterMonth);
        spinnerYear.setAdapter(arrayAdapterYear);
    }

    private boolean verifyForm(){
        boolean ver = true;
        if(!validaName(editTextName))
            ver = false;
        if(!Services.isValidEmail(editEmail, this))
            ver = false;
        if (!validaCPF(editTextCPF))
            ver = false;
        if (!validaPhone(editTextPhone))
            ver = false;
        if(!validateHeight(editTextHeight))
            ver = false;
        if(!validateWeight(editTextWeihgt))
            ver = false;
        if(!ver)
            Services.messageAlert(this, CreateAccountAthleteActivity.this.getResources().getString(R.string.alert), CreateAccountAthleteActivity.this.getResources().getString(R.string.invalid_data_continue),"");
        return ver;
    }

    public boolean validaName(EditText edit) {
        boolean ver = false;
        if(getString(edit).length()>=5) {
            Services.changeColorEditBorder(edit, this);
            ver = true;
        }
        else
            Services.changeColorEditBorderError(edit, this);
        return ver;
    }

    public boolean validaPhone(EditText edit) {
        boolean ver = false;

        String strLanguage = Locale.getDefault().getLanguage();
        if(strLanguage.equals("pt")) {
            if(getString(edit).length()>=12) {
                Services.changeColorEditBorder(edit, this);
                ver = true;
            }
            else
                Services.changeColorEditBorderError(edit, this);
        }
        else ver = true;

        return ver;
    }

    public boolean validaCPF(EditText edit){

        boolean ver = false;
        try{
            String strLanguage = Locale.getDefault().getLanguage();
            if(strLanguage.equals("pt")) {
                if(getString(edit).length()>=14) {
                    Services.changeColorEditBorder(edit, this);
                    ver = true;
                }
                else
                    Services.changeColorEditBorderError(edit, this);
            }
            else ver = true;
        }catch (Exception ex){

        }

        return ver;
    }

    private boolean validaBirthday(){
        boolean ver = false;
        if(spinnerDay.getText().toString().equals(""))
            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.alert), CreateAccountAthleteActivity.this.getResources().getString(R.string.invalid_birth_day),"");
        else if(spinnerMonth.getText().toString().equals(""))
            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.alert), CreateAccountAthleteActivity.this.getResources().getString(R.string.invalid_birth_month),"");
        else if(spinnerYear.getText().toString().equals(""))
            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.alert), CreateAccountAthleteActivity.this.getResources().getString(R.string.invalid_birth_month),"");
        else if(spinnerPosition.getText().toString().equals(""))
            Services.messageAlert(CreateAccountAthleteActivity.this, CreateAccountAthleteActivity.this.getResources().getString(R.string.alert),CreateAccountAthleteActivity.this.getResources().getString(R.string.desired_position),"");
        else
            ver=true;
        return ver;
    }

    private boolean validateHeight(EditText edit){
        boolean ver = false;
        if(getString(edit).length()>=3){
            int first = Integer.parseInt(edit.getText().toString().substring(0,1));
            if(first>=3){
                Services.changeColorEditBorderError(edit, this);
            }
            else {
                Services.changeColorEditBorder(edit, this);
                ver = true;
            }
        }
        else
            Services.changeColorEditBorderError(edit, this);
        return ver;
    }

    private boolean validateWeight(EditText edit){
        boolean ver = false;
        if(getString(edit).length()>=2){
            int height = Integer.parseInt(getString(edit));
            if(height>300)
                Services.changeColorEditBorderError(edit, this);
            else {
                Services.changeColorEditBorder(edit, this);
                ver = true;
            }
        }
        else
            Services.changeColorEditBorderError(edit, this);
        return ver;
    }

    private View.OnClickListener clickOpenTerms = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintTerms.setVisibility(View.VISIBLE);
        }
    };

    private  View.OnClickListener closeTerms = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintTerms.setVisibility(View.GONE);
        }
    };


    @Override
    protected void onActivityResult(int  requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri =  getPickImageResultUri(data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                imageCrop.setImageUriAsync(imageUri);
                mCropImageUri = imageUri;
            }
        }
    }

    private View.OnClickListener clickedChangeImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeImage();
        }
    };

    private void changeImage(){
        ConstraintLayout contraintLayout = (ConstraintLayout) findViewById(R.id.constraint_crop_image);
        contraintLayout.setVisibility(View.VISIBLE);
        onLoadImageClick(imageCrop);
    }

    public void onLoadImageClick(View view) {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }

    public Intent getPickImageChooserIntent() {
        // Determine Uri of camera image to  save.
        Uri outputFileUri =  getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager =  getPackageManager();

        // collect all camera intents
        Intent captureIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam =  packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new  Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        //collect all gallery intents
        Intent galleryIntent = new  Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery =  packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new  Intent(galleryIntent);
            intent.setComponent(new  ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        // the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent =  allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if  (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))  {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

// Create a chooser from the main  intent
        Intent chooserIntent =  Intent.createChooser(mainIntent, "Select source");

// Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,  allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new  File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from  {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera  and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private View.OnClickListener clickedCropImageOk = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ConstraintLayout contraintLayout = (ConstraintLayout) findViewById(R.id.constraint_crop_image);
            contraintLayout.setVisibility(View.GONE);
            onCropImageClick(imageAthlete);
        }
    };

    private View.OnClickListener clickedImageCropCancel = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ConstraintLayout contraintLayout = (ConstraintLayout) findViewById(R.id.constraint_crop_image);
            contraintLayout.setVisibility(View.GONE);
        }
    };


    public void onCropImageClick(View view) {
        isCropImage = true;
        Bitmap cropped =  imageCrop.getCroppedImage(1200, 1200);
        if (cropped != null)
            imageAthlete.setImageBitmap(cropped);
    }


    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    public void uploadMultipart(Uri filePath, String url ) {
        try {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);

            String userToken = SharedPreferencesAdapter.getValueStringSharedPreferences(this, Constants.USER_TOKEN);
            //Creating a multi part request
            String multi = new MultipartUploadRequest(this, uploadId, url)
                    .addFileToUpload(filePath.getPath(), "file") //Adding file
                    .addHeader("app-authorization", Constants.AUTHENTICATION)
                    .addHeader("authorization", userToken)
                    .setUtf8Charset()
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
            constraintProgress.setVisibility(View.VISIBLE);
            Log.i("Create Team", "init upload");

            multi.toString();
            Log.i("Create Team", multi);
            Log.i("Create Team", "finish upload");
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        textProgress.setText("Fazendo upload de imagem.");
    }

    @Override
    public void onError(Exception exception) {
        if(this.File.exists())
            this.File.delete();
        constraintProgress.setVisibility(View.GONE);
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        if(this.File.exists())
            this.File.delete();
        constraintProgress.setVisibility(View.GONE);

        saveAthlete(op);

        Log.i("serverResponseBody", serverResponseBody.toString());
        Log.i("serverResponseBody", serverResponseCode+"");
    }

    @Override
    public void onCancelled() {

    }
}
