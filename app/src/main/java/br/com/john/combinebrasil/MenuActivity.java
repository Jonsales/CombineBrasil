package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.CreateJSON;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Connection.Posts.PatchTrocaSenha;
import br.com.john.combinebrasil.Connection.Posts.PostCreateSelective;
import br.com.john.combinebrasil.Connection.Posts.PutTests;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.NavigationMenuDrawer;
import br.com.john.combinebrasil.Services.Services;
import br.com.john.combinebrasil.Services.SharedPreferencesAdapter;

public class MenuActivity extends AppCompatActivity {
    private ImageView linearCreateSelective;
    private ImageView linearEnterSelective;
    private ImageView linearHistoricSelective;
    Toolbar toolbar;
    NavigationMenuDrawer navigationDrawer;
    EditText editCode;
    Button btnConfirmCode, btnCloseCode;
    ConstraintLayout constraintDialogEnterSelective, constraintNotConnection, constraintProgress;
    private static Selective selective;
    private static Activity act;
    public static final int METHOD_USER_SELECTIVES = 3, METHOD_VERIFY_USER_SELECTIVES = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setVisibility(View.GONE);
        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.linear_menu_button);
        btnMenu.setVisibility(View.GONE);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);
        TextView textTitle = (TextView) findViewById(R.id.text_title_toolbar);
        textTitle.setText(R.string.menu);

        linearCreateSelective = (ImageView) findViewById(R.id.linear_create_selective);
        linearCreateSelective.setOnClickListener(clickCreateSelective);

        linearEnterSelective = (ImageView) findViewById(R.id.linear_enter_selective);
        linearEnterSelective.setOnClickListener(clickEnterSelective);

        linearHistoricSelective = (ImageView) findViewById(R.id.linear_historic_selective);
        linearHistoricSelective.setOnClickListener(clickHistoricSelective);

        editCode = (EditText) findViewById(R.id.edit_code);
        btnConfirmCode = (Button) findViewById(R.id.btn_confirm_code);
        btnCloseCode = (Button) findViewById(R.id.btn_close_code);
        btnCloseCode.setOnClickListener(clickHideEnterSelective);
        btnConfirmCode.setOnClickListener(clickConfirmEnterSelective);
        btnConfirmCode.setOnLongClickListener(clickLongConfirmSelective);
        constraintDialogEnterSelective = (ConstraintLayout) findViewById(R.id.constraint_dialog_code);
        constraintNotConnection = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintDialogEnterSelective.setOnClickListener(clickHideEnterSelective);
        enabledOrDisabledBtn(btnConfirmCode, false);
        editCode.addTextChangedListener(textWatcher);

        DatabaseHelper db = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        navigationDrawer = new NavigationMenuDrawer(savedInstanceState, this, toolbar, true, db.getUser());
        navigationDrawer.createNavigationAccess();

        act = MenuActivity.this;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        double heightMax = width / 1.86;
        Float.parseFloat(String.format("%.0f", heightMax));
        int height = (int) heightMax;

        linearCreateSelective.getLayoutParams().height = height;
        linearCreateSelective.setMinimumHeight(height);
        linearCreateSelective.setMaxHeight(height);

        linearEnterSelective.getLayoutParams().height = height;
        linearEnterSelective.setMinimumHeight(height);
        linearEnterSelective.setMaxHeight(height);

        linearHistoricSelective.getLayoutParams().height = height;
        linearHistoricSelective.setMinimumHeight(height);
        linearHistoricSelective.setMaxHeight(height);

        String esqueci = SharedPreferencesAdapter.getValueStringSharedPreferences(this, "esqueci_senha");
        if (esqueci != null) {
            if (esqueci.equals("sim"))
                mostraAlterarSenha();
        }

        try{
            String strLanguage = Locale.getDefault().getLanguage();

            if(strLanguage.equals("en")){
                linearCreateSelective.setImageResource(R.drawable.item_menu_criar_avaliacao_ingles);
                linearEnterSelective.setImageResource(R.drawable.item_menu_logar_avaliacao_ingles);
                linearHistoricSelective.setImageResource(R.drawable.item_menu_historico_ingles);
            }
            else if(strLanguage.equals("es")){
                linearCreateSelective.setImageResource(R.drawable.item_menu_criar_avaliacao_espanhol);
                linearEnterSelective.setImageResource(R.drawable.item_menu_logar_avaliacao_espanhol);
                linearHistoricSelective.setImageResource(R.drawable.item_menu_historico_espanhol);
            }
            else if(strLanguage.equals("it")){ //Italiano
                linearCreateSelective.setImageResource(R.drawable.item_menu_criar_avaliacao_espanhol);
                linearEnterSelective.setImageResource(R.drawable.item_menu_logar_avaliacao_espanhol);
                linearHistoricSelective.setImageResource(R.drawable.item_menu_historico_espanhol);
            }
        }catch (Exception ex){

        }

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            String code = extras.getString("entrar_seletiva");
            if(code!=null) {
                if(!code.equals("")) {
                    editCode.setText(code);
                    callEnterSelective();
                }
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 4)
                enabledOrDisabledBtn(btnConfirmCode, true);
            else
                enabledOrDisabledBtn(btnConfirmCode, false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void enabledOrDisabledBtn(Button btn, boolean isEnabled) {
        if (isEnabled) {
            //btn.setEnabled(true);
            btn.setAlpha(1);
        } else {
            //btn.setEnabled(false);
            btn.setAlpha(0.5f);
        }
    }

    private View.OnClickListener clickCreateSelective = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(MenuActivity.this, SelectiveCreatedSuccessActivity.class);
            Intent intent = new Intent(MenuActivity.this, ChooseTeamSelectiveActivity.class);
            //Intent intent = new Intent(MenuActivity.this, TestSelectiveActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener clickEnterSelective = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintDialogEnterSelective.setVisibility(View.VISIBLE);
            showSoft(editCode);
        }
    };

    private View.OnClickListener clickHistoricSelective = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MenuActivity.this, HistoricSelectiveActivity.class);
            MenuHistoricSelectiveActivity.enterSelective = false;
            startActivity(intent);
        }
    };
    private View.OnClickListener clickHideEnterSelective = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideSoft();
            editCode.setText("");
            constraintDialogEnterSelective.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener clickConfirmEnterSelective = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (editCode.getText().length() >= 4)
                callEnterSelective();
        }
    };
    private View.OnLongClickListener clickLongConfirmSelective = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (Constants.debug)
                editCode.setText("SELETIVA_COMBINE");
            return true;
        }
    };

    private void callEnterSelective() {
        if (Services.isOnline(this)) {
            hideNotConnect();
            hideSoft();
            showProgress(getString(R.string.verify_selective));
            String url = Constants.URL + Constants.API_SELECTIVES + "?" + Constants.SELECTIVES_CODESELECTIVE + "=" + editCode.getText().toString().toUpperCase();
            Connection task = new Connection(url, 0, Constants.CALLED_GET_SELECTIVE, false, MenuActivity.this);
            task.callByJsonStringRequest();
        } else
            showToNoConnect();
    }

    public static void returnGetSelectiveByCode(Activity act, String response, int status) {
        ((MenuActivity) act).returnGetSelectiveByCode(response, status);
    }

    private void returnGetSelectiveByCode(String response, int status) {
        hideProgress();
        if (status == 200 || status == 201) {
            DeserializerJsonElements des = new DeserializerJsonElements(response);
            ArrayList<Selective> selectives = des.getSelectives();
            if (selectives != null && selectives.size() > 0) {
                try {
                    Selective selective = des.getSelectives().get(0);
                    if (selective != null) {
                        this.selective = selective;
                        DatabaseHelper db = new DatabaseHelper(MenuActivity.this);
                        db.deleteTable(Constants.TABLE_SELECTIVES);
                        db.addSelective(selective);
                        callCreateUserSelective(selective);
                    } else
                        Services.messageAlert(MenuActivity.this, this.getString(R.string.warning), this.getString(R.string.code_not_exist), "hide");
                } catch (Exception e) {
                    Services.messageAlert(MenuActivity.this, this.getString(R.string.warning), this.getString(R.string.code_not_exist), "hide");
                    Log.i("Exception: ", e.getMessage());
                }
            } else
                Services.messageAlert(MenuActivity.this, this.getString(R.string.warning), this.getString(R.string.code_not_exist), "hide");
        } else
            Services.messageAlert(MenuActivity.this, this.getString(R.string.warning), this.getString(R.string.code_not_exist), "hide");
    }

    private void callCreateUserSelective(Selective selective) {
        if (Services.isOnline(this)) {
            showProgress(this.getString(R.string.configuring_user_selective));
            verifyUserSective();
        } else
            constraintNotConnection.setVisibility(View.VISIBLE);
    }

    private void verifyUserSective() {
        String url = Constants.URL + Constants.API_USER_SELECTIVE_SEARCH;
        PostCreateSelective post = new PostCreateSelective();
        post.setActivity(MenuActivity.this);
        post.setMethod(METHOD_VERIFY_USER_SELECTIVES);
        post.setObjPut(CreateJSON.queryObjectUserSelectives(selective.getId(), Services.getIdUser(this)));
        post.execute(url);
    }

    public static void returnVerifyUserSelective(Activity act, String result, int status) {
        ((MenuActivity) act).returnVerifyUserSelective(result, status);
    }

    private void returnVerifyUserSelective(String result, int status) {
        if (status == 200 || status == 201) {
            if (!result.equals("[]")) {
                try {
                    Services.messageAlert(this, this.getString(R.string.message), this.getString(R.string.congratulations_selective) + selective.getTitle(), "CODE_OK");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                createUserSelective();
        } else
            createUserSelective();
    }

    private void createUserSelective() {
        String url = Constants.URL + Constants.API_USER_SELECTIVE;
        PostCreateSelective post = new PostCreateSelective();
        post.setActivity(MenuActivity.this);
        post.setMethod(METHOD_USER_SELECTIVES);
        post.setObjPut(CreateJSON.createObjectUserSelectives(selective.getId(), Services.getIdUser(this), false));
        post.execute(url);
    }

    public static void returnCreateUserSelective(Activity act, String status, String result) {
        ((MenuActivity) act).returnCreateUserSelective(status, result);
    }

    private void returnCreateUserSelective(String status, String result) {
        constraintProgress.setVisibility(View.GONE);
        if (status.toUpperCase().equals("OK")) {
            try {
                Services.messageAlert(this, this.getString(R.string.message), this.getString(R.string.congratulations_selective) + selective.getTitle(), "CODE_OK");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Services.messageAlert(MenuActivity.this, this.getString(R.string.warning), this.getString(R.string.code_not_exist), "hide");
        }
    }

    private void openSelective(Selective selective) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.addSelective(selective);
        AllActivities.isSync = true;
        Intent i = new Intent(MenuActivity.this, MainActivity.class);
        i.putExtra(Constants.SELECTIVES_ID_ENTER, selective.getId());
        i.putExtra("INIT_SELECTIVE", true);
        SharedPreferencesAdapter.setEnterSelectiveSharedPreferences(this, true);
        SharedPreferencesAdapter.setValueStringSharedPreferences(this, Constants.SELECTIVES_CODESELECTIVE, selective.getCodeSelective());
        startActivity(i);
        this.finish();
    }

    private void hideSoft() {
        try {
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch(Exception e){
            Log.i("hideSoft",e.getMessage());
        }
    }

    private void showSoft(EditText edit) {
        try {
            edit.setFocusable(true);
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }catch(Exception e){
            Log.i("showSoft",e.getMessage());
        }
    }

    private void showToNoConnect() {
        TextView textMessage = (TextView) findViewById(R.id.txt_message_not_connect);
        textMessage.setText(Html.fromHtml(getString(R.string.no_connection)));
        ConstraintLayout constraintNoConnect = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintNoConnect.setVisibility(View.VISIBLE);
        Button btnTryAgain = (Button) findViewById(R.id.btn_try_again_connect);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEnterSelective();
            }
        });
    }

    private void hideNotConnect() {
        ConstraintLayout constraintNoConnect = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        constraintNoConnect.setVisibility(View.GONE);
    }

    private void showProgress(String message) {
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        TextView textProgress = (TextView) findViewById(R.id.text_progress);
        textProgress.setText(message);
        constraintProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        ConstraintLayout constraintProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        constraintProgress.setVisibility(View.GONE);
    }

    public static void returnMessageAlert(Activity act, String whoCalled) {
        if (whoCalled.equals("CODE_OK"))
            ((MenuActivity) act).openSelective(selective);
    }

    @Override
    public void onBackPressed() {
        if (constraintDialogEnterSelective.getVisibility() == View.VISIBLE)
            constraintDialogEnterSelective.setVisibility(View.GONE);
        else this.finish();
    }

    public static void finishActity() {
        ((MenuActivity) act).finish();
    }

    private void mostraAlterarSenha() {
        SharedPreferencesAdapter.clearData(this, "esqueci_senha");
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_mudar_senha);
        constraintLayout.setVisibility(View.VISIBLE);
        final EditText editSenha = (EditText) findViewById(R.id.edit_alterar_senha);
        final EditText editConfirmaSenha = (EditText) findViewById(R.id.edit_confirma_senha);

        Button btnCancela = (Button) findViewById(R.id.btn_cancel_senha);
        Button btnAltera = (Button) findViewById(R.id.btn_alterar_senha);

        btnCancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout.setVisibility(View.GONE);
            }
        });

        btnAltera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaSenhas(editSenha, editConfirmaSenha))
                    alteraSenha(editSenha.getText().toString());
                else
                    Services.messageAlert(MenuActivity.this, getString(R.string.message), getString(R.string.password_change), "");
            }
        });
    }

    private boolean verificaSenhas(EditText editSenha, EditText editConfirma) {
        if (editSenha.getText().toString().trim().length() > 5) {
            if (editSenha.getText().toString().equals(editConfirma.getText().toString()))
                return true;
        }

        return false;
    }

    private void alteraSenha(String senha) {
        DatabaseHelper db = new DatabaseHelper(this);
        String url = Constants.URL + Constants.API_USERS + "/" + db.getUser().getId();
        PatchTrocaSenha patch = new PatchTrocaSenha();
        patch.setActivity(this);
        patch.setObjPut(criarJson(senha));
        patch.execute(url);
    }

    private JSONObject criarJson(String senha) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.USER_PASSWORD, senha);
            return jsonObject;
        } catch (Exception ex) {
            Log.i("Exception", ex.getMessage());
            return null;
        }
    }

    public static void retornoSenhaAlterada(Activity act, String response, String status) {
        ((MenuActivity) act).retornoSenhaAlterada(response, status);
    }

    private void retornoSenhaAlterada(String response, String status) {
        if (status.equals("OK")) {
            final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_mudar_senha);
            constraintLayout.setVisibility(View.GONE);
            Services.messageAlert(this, this.getString(R.string.message), this.getString(R.string.password_changed_successfully), "");
            hideSoft();
        } else
            Services.messageAlert(this, this.getString(R.string.error), this.getString(R.string.password_not_change), "");
    }


}
