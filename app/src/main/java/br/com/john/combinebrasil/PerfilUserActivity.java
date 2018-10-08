package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Connection.Posts.PatchTrocaSenha;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.ImageConverter;
import br.com.john.combinebrasil.Services.ImageLoadedCallback;
import br.com.john.combinebrasil.Services.Services;

public class PerfilUserActivity extends AppCompatActivity {
    Toolbar toolbar;
    boolean isMainUser = true;
    EditText editAlteraSenha, editConfirmaSenha;
    TextView textEmail, textName, textChangePswd;
    ImageView imageUser;
    Button btnAlterarSneha, btnCancelar;
    private static Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_user);

        act = this;

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

        textName = (TextView) findViewById(R.id.text_name_user);
        textEmail = (TextView) findViewById(R.id.text_email_user);
        textChangePswd = (TextView) findViewById(R.id.text_change_pwsd);
        imageUser = (ImageView) findViewById(R.id.image_user);
        editAlteraSenha = (EditText) findViewById(R.id.edit_alterar_senha);
        editConfirmaSenha = (EditText) findViewById(R.id.edit_confirma_senha);

        btnAlterarSneha = (Button) findViewById(R.id.btn_alterar_senha);
        btnCancelar = (Button) findViewById(R.id.btn_cancel_senha);


        btnAlterarSneha.setOnClickListener(clickAlterarSenha);
        btnCancelar.setOnClickListener(clickCancelarSenha);

        textChangePswd.setOnClickListener(clickedTextChangePswd);

        showInfoUserPerfil();
    }

    private View.OnClickListener clickedTextChangePswd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showChangePswd();
        }
    };

    private void showChangePswd(){
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_mudar_senha);
        constraintLayout.setVisibility(View.VISIBLE);
        isMainUser = false;
    }

    private void showInfoUserPerfil(){
        DatabaseHelper db = new DatabaseHelper(this);
        User user = db.getUser();

        textEmail.setText(user.getEmail());
        textName.setText(user.getName());

        String url = user.getEmail().equals("") ? "https://image.ibb.co/c4LEnQ/user.png" : user.getEmail();

        showImageTeam(url);
    }

    private void showImageTeam(String urlImage){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_image);
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(urlImage)
                .fit().centerCrop()
                .into(imageUser, new ImageLoadedCallback(progressBar) {
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
                        imageUser.setImageDrawable(act.getResources().getDrawable(R.drawable.user));
                    }
                });
    }

    private void roundedImage(){
        Bitmap bitmap = ((BitmapDrawable)imageUser.getDrawable()).getBitmap();
        bitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 200);
        imageUser.setImageBitmap(bitmap);
    }
    @Override
    public void onBackPressed() {
        finishAct();
    }


    private View.OnClickListener clickedBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void finishAct(){
        if(isMainUser)
            finish();
        else
            hideChangePswd();
    }
    private void hideChangePswd(){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_mudar_senha);
        constraint.setVisibility(View.GONE);
        editAlteraSenha.setText("");
        editConfirmaSenha.setText("");
        hideSoft();
    }

    public static void retornoSenhaAlterada(Activity act, String response, String status){
        ((PerfilUserActivity)act).retornoSenhaAlterada(response, status);
    }

    private void retornoSenhaAlterada(String response, String status){
        hideSoft();
        if(status.equals("OK")){
            final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_mudar_senha);
            constraintLayout.setVisibility(View.GONE);
            Services.messageAlert(this, this.getResources().getString(R.string.message),this.getResources().getString(R.string.password_changed_successfully),"");
        }
        else
            Services.messageAlert(this, this.getResources().getString(R.string.error),this.getResources().getString(R.string.password_not_change),"");
    }

    private View.OnClickListener clickAlterarSenha = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alterarSenha();
        }
    };

    private void alterarSenha(){
        if(verificaSenhas(editAlteraSenha, editConfirmaSenha))
            alteraSenha(editAlteraSenha.getText().toString());
        else
            Services.messageAlert(PerfilUserActivity.this, this.getResources().getString(R.string.message), this.getResources().getString(R.string.password_change),"");
    }
    private void alteraSenha(String senha){
        DatabaseHelper db = new DatabaseHelper(this);
        String url = Constants.URL+Constants.API_USERS+"/"+db.getUser().getId();
        PatchTrocaSenha patch = new PatchTrocaSenha();
        patch.setActivity(this);
        patch.setObjPut(criarJson(senha));
        patch.execute(url);
    }

    private JSONObject criarJson(String senha){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.USER_PASSWORD, senha);
            return jsonObject;
        }catch(Exception ex){
            Log.i("Exception",ex.getMessage());
            return null;
        }
    }

    private View.OnClickListener clickCancelarSenha = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideChangePswd();
        }
    };

    private boolean verificaSenhas(EditText editSenha, EditText editConfirma){
        if (editSenha.getText().toString().trim().length()>5){
            if(editSenha.getText().toString().equals(editConfirma.getText().toString()))
                return true;
        }

        return false;
    }

    private void hideSoft(){
        try {
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch(Exception e){
            Log.i("Exception", e.getMessage());
        }
    }
}
