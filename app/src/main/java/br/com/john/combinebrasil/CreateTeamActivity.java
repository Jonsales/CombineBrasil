package br.com.john.combinebrasil;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import br.com.john.combinebrasil.Classes.CEP;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.CreateJSON;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PostTeamAsyncTask;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.Mask;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;
import br.com.john.combinebrasil.Services.SingleUploadBroadcastReceiver;

public class CreateTeamActivity extends AppCompatActivity  implements SingleUploadBroadcastReceiver.Delegate{
    Toolbar toolbar;
    private EditText editTeam, editEmail, editNamePresident, editSocialLink, editCep, editCity, editStreet, editNumber, editState, editComplement, editNeighborhood;
    private MaterialBetterSpinner spinnerModality;
    private ConstraintLayout constraintProgress;
    private TextView textProgress, textAddress;
    private Button btnCreate, btnDoneAddress, btnCancelAddress, btnCropImage, btnCancelCrop;
    private static Activity act;
    private ImageView imageTeam;
    private CropImageView imageCrop;
    private Uri mCropImageUri;
    private boolean isCropImage = false;
    private java.io.File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);

        textAddress = (TextView) findViewById(R.id.text_address_team);
        editTeam = (EditText) findViewById(R.id.edit_name_team);
        editEmail = (EditText) findViewById(R.id.edit_email_director);
        editNamePresident = (EditText) findViewById(R.id.edit_name_director);
        editSocialLink = (EditText) findViewById(R.id.edit_social_link);
        editCep = (EditText) findViewById(R.id.edit_cep_add_team);
        editCity = (EditText) findViewById(R.id.edit_add_team_city);
        editStreet = (EditText) findViewById(R.id.edit_add_team_street);
        editNumber = (EditText) findViewById(R.id.edit_add_team_number);
        editState = (EditText) findViewById(R.id.edit_add_team_state);
        editComplement = (EditText) findViewById(R.id.edit_complement);
        editNeighborhood = (EditText) findViewById(R.id.edit_add_team_neighborhood);

        imageTeam = (ImageView) findViewById(R.id.img_team);
        imageCrop = (CropImageView) findViewById(R.id.cropimage);

        spinnerModality = (MaterialBetterSpinner) findViewById(R.id.spinner_modality);

        btnCancelAddress = (Button) findViewById(R.id.btn_cancel_local_add);
        btnDoneAddress = (Button) findViewById(R.id.btn_close_local_add);
        btnCreate = (Button) findViewById(R.id.btn_create_team);
        btnCropImage = (Button) findViewById(R.id.btn_crop_image);
        btnCancelCrop = (Button) findViewById(R.id.btn_cancel_crop);

        textAddress.setOnClickListener(clickedTextAddress);

        btnCancelAddress.setOnClickListener(clickedCancelLocal);
        btnDoneAddress.setOnClickListener(clickedDoneLocal);
        btnCreate.setOnClickListener(clickCreateTeam);
        btnCreate.setOnLongClickListener(longClickCreate);
        btnCropImage.setOnClickListener(clickedCropImageOk);
        btnCancelCrop.setOnClickListener(clickedImageCropCancel);
        imageTeam.setOnClickListener(clickedChangeImage);

        constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        textProgress = (TextView) findViewById(R.id.text_progress);

        imageCrop.setAspectRatio(1200,1200);
        imageCrop.setFixedAspectRatio(true);
        imageCrop.setAutoZoomEnabled(true);

        spinnerModality.setAdapter(inflateModality());

        Mask maskCpf = new Mask("#####-###", editCep);
        editCep.addTextChangedListener(maskCpf);
        editCep.addTextChangedListener(textListenerCep);

        act = CreateTeamActivity.this;
        final View activityRootView = findViewById(R.id.activity_create_team);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(act, 200)) { // if more than 200 dp, it's probably a keyboard...
                    btnCreate.setVisibility(View.GONE);
                    ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraintLayout3);
                    constraint.setVisibility(View.GONE);
                }
                else
                    btnCreate.setVisibility(View.VISIBLE);
                ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraintLayout3);
                constraint.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onLoadImageClick(View view) {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }
    /**
     * Crop the image and set it back to the  cropping view.
     */
    public void onCropImageClick(View view) {
        isCropImage = true;
        Bitmap cropped =  imageCrop.getCroppedImage(500, 500);
        if (cropped != null)
            imageTeam.setImageBitmap(cropped);
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

    private View.OnClickListener clickedCropImageOk = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ConstraintLayout contraintLayout = (ConstraintLayout) findViewById(R.id.constraint_crop_image);
            contraintLayout.setVisibility(View.GONE);
            onCropImageClick(imageTeam);
        }
    };

    private View.OnClickListener clickedImageCropCancel = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ConstraintLayout contraintLayout = (ConstraintLayout) findViewById(R.id.constraint_crop_image);
            contraintLayout.setVisibility(View.GONE);
        }
    };
    private static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreateTeamActivity.this.finish();
        }
    };

    private View.OnClickListener clickCreateTeam = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(verifyFields()){
                if(!spinnerModality.getText().toString().equals("")){
                    createTeam();
                }
                else
                    Services.messageAlert(CreateTeamActivity.this, CreateTeamActivity.this.getResources().getString(R.string.alert), CreateTeamActivity.this.getResources().getString(R.string.select_mode),"");
            }
        }
    };

    private void createTeam(){
        if(Services.isOnline(CreateTeamActivity.this)){
            constraintProgress.setVisibility(View.VISIBLE);
            textProgress.setText(CreateTeamActivity.this.getResources().getString(R.string.creating_team));

            String url = Constants.URL + Constants.API_TEAMS;

            PostTeamAsyncTask post = new PostTeamAsyncTask();
            post.setActivity(CreateTeamActivity.this);
            post.setObjPut(CreateJSON.createObjectTeam(createObjectTeam(), false));
            post.execute(url);
        }
    }

    public static void returnCreateTeam(Activity act, String resp, String result){
        ((CreateTeamActivity)act).returnCreateTeam(resp, result);
    }

    private void returnCreateTeam(String resp, String result){
        constraintProgress.setVisibility(View.GONE);
        if(resp.toUpperCase().equals("OK")){
            try {
                if (isCropImage) {
                    JSONObject json = new JSONObject(result);
                    String id = json.getString(Constants.TEAM_ID);
                    String team = json.getString(Constants.TEAM_NAME);
                    uploadImageTeam(id, team);
                }else{
                    Services.messageAlert(CreateTeamActivity.this, CreateTeamActivity.this.getResources().getString(R.string.message), CreateTeamActivity.this.getResources().getString(R.string.team_created_successfully),"exitTeam");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            verifyError(result);
    }

    private void uploadImageTeam(String id, String team){
        String url = Constants.URL+Constants.API_TEAMS+"/"+id+"/upload";
        Uri uri = saveImage(team);
        if(uri!=null) {
            uploadMultipart(uri, url);
        }

    }

    private Uri saveImage(String nameImage){
        Bitmap bm = ((BitmapDrawable)imageTeam.getDrawable()).getBitmap();
        OutputStream fOut = null;
        Uri outputFileUri = null;
        try {
            java.io.File file = new java.io.File((this
                    .getApplicationContext().getFileStreamPath(nameImage+".jpg")
                    .getPath()));

            outputFileUri = Uri.fromFile(file);
            fOut = new FileOutputStream(file);
            this.file = file;
        } catch (Exception e) {
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
        }

        return outputFileUri;
    }

    public static void returnMessage(Activity act, String methodCalled){
        ((CreateTeamActivity)act).returnMessage(methodCalled);
    }

    private void returnMessage(String methodCalled){
        if(methodCalled.toLowerCase().equals("exitteam"))
            CreateTeamActivity.this.finish();
    }

    private void verifyError(String result){
        try{
            if(result.contains("11000")){



                Services.messageAlert(this, CreateTeamActivity.this.getResources().getString(R.string.message), CreateTeamActivity.this.getResources().getString(R.string.team_already_registered).replace("{TEAM}", editTeam.getText().toString()),"");
            }
            else

                Services.messageAlert(this,CreateTeamActivity.this.getResources().getString(R.string.message),CreateTeamActivity.this.getResources().getString(R.string.error_creating_team),"");

        }catch (Exception e){
            Log.i("Excepiton ",e.getMessage());
        }
    }

    private Team createObjectTeam() {
        Team team = new Team();
        team.setName(editTeam.getText().toString());
        team.setEmail(editEmail.getText().toString());
        team.setPresidentName(editNamePresident.getText().toString());
        team.setAddres(textAddress.getText().toString());
        team.setCity(editCity.getText().toString());
        team.setSocialLink(editSocialLink.getText().toString());
        team.setModality(spinnerModality.getText().toString());
        team.setUser(Services.getIdUser(this));
        String code = editTeam.getText().toString().substring(0,2).toUpperCase()+"00"+new Random().nextInt(9-1) +
                Services.convertDate(Services.getCurrentDateTime()).substring(0,2)+editCity.getText().toString().substring(0,1).toUpperCase();
        team.setCode(Services.removeAccents(code));

        return team;
    }

    private boolean verifyFields(){
        boolean ver = true;
        if(!validaEdit(editTeam))
            ver = false;
        if(!Services.isValidEmail(editEmail, this))
            ver = false;
        if(!validaEdit(editCity))
            ver = false;
        if(!validaEdit(editSocialLink))
            ver = false;
        return ver;
    }

    public boolean validaEdit(EditText edit) {
        boolean ver = false;
        if(getString(edit).length()>=5) {
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

    private ArrayAdapter<String> inflateModality(){
        String[] spinnerMonthValues  = {"Flag X5", "Flag X8", "Full Pad", "No Pad", "Beach Football"};
        ArrayAdapter<String> arrayModality = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerMonthValues);
        return arrayModality;
    }

    private View.OnClickListener clickedTextAddress = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_local_add_team);
            constraintLayout.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener clickedDoneLocal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!editCity.getText().toString().isEmpty()){
                ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_local_add_team);
                constraintLayout.setVisibility(View.GONE);

                String address= editCity.getText().toString();

                if((!editCep.getText().toString().isEmpty())
                        &&(!editNeighborhood.getText().toString().isEmpty())
                        &&(!editStreet.getText().toString().isEmpty())
                        &&(!editNumber.getText().toString().isEmpty())
                        &&(!editState.getText().toString().isEmpty()))
                    address = editCep.getText().toString()+" ("+editNeighborhood.getText().toString()+" - "+editCity.getText().toString()
                            +", "+editStreet.getText().toString()+" "+editNumber.getText().toString()+" - "+editState.getText().toString()+")";

                textAddress.setText(address);
            }
            else
                Toast.makeText(CreateTeamActivity.this, CreateTeamActivity.this.getResources().getString(R.string.must_fill_least_city), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener clickedCancelLocal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_local_add_team);
            constraintLayout.setVisibility(View.GONE);
            clearAddress();
        }
    };
    private void clearAddress(){
        editCep.setText("");
        editCity.setText("");
        editStreet.setText("");
        editNumber.setText("");
        editState.setText("");
        editComplement.setText("");
        editNeighborhood.setText("");
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
        Connection task = new Connection(url, 0, Constants.CALLED_GET_CEP, false, CreateTeamActivity.this);
        task.callByJsonStringRequest();
    }

    private void showLoadingAddress(){
        editCity.setText(CreateTeamActivity.this.getResources().getString(R.string.location_city));
        editState.setText(CreateTeamActivity.this.getResources().getString(R.string.finding));
        editStreet.setText(CreateTeamActivity.this.getResources().getString(R.string.locatin_street));
        editNeighborhood.setText(CreateTeamActivity.this.getResources().getString(R.string.locating_neighborhood));
    }

    public static void returnCEP(Activity act, String response, int statusCode){
        ((CreateTeamActivity)act).returnCEP(response, statusCode);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imageCrop.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
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

    private View.OnLongClickListener longClickCreate = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(Constants.debug){
                editCity.setText("São Jose dos Campos");
                textAddress.setText("São Jose dos Campos");
                editTeam.setText("São Jose dos Campos - Teste");
                editEmail.setText("sjcTeste@combine.com");
                editSocialLink.setText("www.facebook.com/sjcTeste");
                editNamePresident.setText("John");
            }
            return true;
        }
    };

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
        //getting the actual path of the image
        //String path = getPath(filePath);

        //Uploading code


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
        textProgress.setText(CreateTeamActivity.this.getResources().getString(R.string.uploading_image));
    }

    @Override
    public void onError(Exception exception) {
        if(file!=null && file.exists())
            file.delete();
        constraintProgress.setVisibility(View.GONE);
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        constraintProgress.setVisibility(View.GONE);
        Log.i("serverResponseBody", serverResponseBody.toString());
        Log.i("serverResponseBody", serverResponseCode+"");
        if(file!=null && file.exists())
            file.delete();
        Services.messageAlert(CreateTeamActivity.this, CreateTeamActivity.this.getResources().getString(R.string.message), CreateTeamActivity.this.getResources().getString(R.string.uploading_image),"exitTeam");
    }

    @Override
    public void onCancelled() {

    }
}