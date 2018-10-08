package br.com.john.combinebrasil;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import br.com.john.combinebrasil.AdapterList.AdapterRecyclerChooseTestSelective;
import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTestAdditional;
import br.com.john.combinebrasil.AdapterList.AdapterRecyclerTestRecommended;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Connection.Connection;
import br.com.john.combinebrasil.Connection.JSONServices.DeserializerJsonElements;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

public class TestSelectiveActivity extends AppCompatActivity {
    public static final String messageSelectiveCreate = "SELECTIVE_CREATE_OK";
    ConstraintLayout linearProgress, linearNotConnection;
    public static ConstraintLayout constraintInfoTest;
    TextView textProgress, textTestRequired, textTestRecommended, textTestAdditional, textStatusTests;
    Toolbar toolbar;
    RecyclerView recyclerViewTestsRequired, recyclerViewRecommended, recyclerViewAdditional;
    public static Button btnNextPass, btnCloseMessage;
    Button btnTryAgain;
    public static ArrayList<TestTypes> tests;
    ArrayList<TestTypes> testsRequireds;
    ArrayList<TestTypes> testsRecommended;
    ArrayList<TestTypes> testsAdditional;
    AdapterRecyclerChooseTestSelective adapterRecyclerTestsRequired;
    AdapterRecyclerTestRecommended adapterRecyclerTestsRecommended;
    AdapterRecyclerTestAdditional adapterRecyclerTestsAdditional;
    public static LinearLayout linearDelete;
    private static Activity act;
    HashMap<String, String> hashMapSelective;
    int count =0 , totReq=0, totRecom=0, totAdd=0;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selective);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout linearAddAccount = (LinearLayout) findViewById(R.id.linear_add_account);
        linearAddAccount.setVisibility(View.GONE);
        linearDelete = (LinearLayout)findViewById(R.id.linear_delete);
        //linearDelete.setOnClickListener(clickedRemoveTests);
        ImageView imgSearch = (ImageView) findViewById(R.id.imagePesquisarToolbar);
        imgSearch.setVisibility(View.GONE);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.linear_back_button);
        btnBack.setOnClickListener(btnBackClickListener);

        recyclerViewTestsRequired = (RecyclerView) findViewById(R.id.recycler_tests_selective);
        recyclerViewRecommended = (RecyclerView) findViewById(R.id.recycler_tests_recommended);
        recyclerViewAdditional = (RecyclerView) findViewById(R.id.recycler_tests_additional);

        linearNotConnection = (ConstraintLayout) findViewById(R.id.constraint_not_connection);
        linearProgress = (ConstraintLayout) findViewById(R.id.constraint_progress);
        textProgress = (TextView) findViewById(R.id.text_progress);
        textTestRequired = (TextView) findViewById(R.id.text_tests_required);
        textTestRecommended = (TextView) findViewById(R.id.text_tests_recommended);
        textTestAdditional = (TextView) findViewById(R.id.text_tests_additional);
        textStatusTests = (TextView) findViewById(R.id.text_status_tests);
        constraintInfoTest = (ConstraintLayout) findViewById(R.id.constraint_info_test);
        btnTryAgain = (Button) findViewById(R.id.btn_try_again_connect);
        btnTryAgain.setOnClickListener(clickListenerTryAgain);
        btnNextPass = (Button) findViewById(R.id.button_next_pass);
        btnNextPass.setOnClickListener(clickDoneTests);
        btnNextPass.setVisibility(View.GONE);
        btnCloseMessage=(Button) findViewById(R.id.btn_close_message_tests);
        btnCloseMessage.setOnClickListener(clickMessageDefault);

        textTestRequired.setOnClickListener(clickTestsRequireds);
        textTestRecommended.setOnClickListener(clickTestsRecommended);
        textTestAdditional.setOnClickListener(clickTestsAdditional);

        hashMapSelective = AllActivities.hashInfoSelective;

        tests = new ArrayList<TestTypes>();

        act = TestSelectiveActivity.this;
        callUpdateTests();

        linearDelete.setVisibility(View.GONE);
        btnNextPass.setVisibility(View.GONE);
    }

    private void callUpdateTests(){
        if(Services.isOnline(this)){
            updateTests();
        }
        else
            linearNotConnection.setVisibility(View.VISIBLE);
    }

    private void updateTests(){
        linearProgress.setVisibility(View.VISIBLE);
        String url = Constants.URL + Constants.API_TESTTYPES;
        Connection task = new Connection(url, 0, Constants.CALLED_GET_TESTTYPES, false, TestSelectiveActivity.this);
        task.callByJsonStringRequest();
    }

    public static void returnUpdateTests(Activity act, int status, String result){
        ((TestSelectiveActivity)act).returnUpdateTests(status, result);
    }

    private void returnUpdateTests(int status, String result){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_message_test_default);
        constraint.setVisibility(View.VISIBLE);
        TextView textInfo = (TextView) findViewById(R.id.text_info_default);
        textInfo.setText((this.getResources().getString(R.string.customize_your_tests)).toString().replace("{BREAK}","\n"));
        linearProgress.setVisibility(View.GONE);
        if(status == 200 || status == 201) {
            verifyResult(result);
        }
    }

    private void verifyResult(String result){
        DeserializerJsonElements des = new DeserializerJsonElements(result);
        tests = des.getTestTypes();
        try{
            if (tests!=null){
                recordingTests();
            }

        }catch (Exception e){}
    }

    private void recordingTests(){
        DatabaseHelper db = new DatabaseHelper(TestSelectiveActivity.this);
        try {
            db.createDataBase();
            db.openDataBase();
            db.deleteTable(Constants.TABLE_TESTTYPES);
            db.addTestTypes(tests);
            inflateTests();
        } catch (Exception e) {}
    }

    private void inflateTests(){
        String strLanguage = Locale.getDefault().getLanguage();
        testsRequireds = new ArrayList<>();
        testsRecommended = new ArrayList<>();
        testsAdditional = new ArrayList<>();

        if(!(tests == null || tests.size()==0)){
            for(int i=0; i <=tests.size()-1; i++){
                if(tests.get(i).isRequiredToReport() == true && tests.get(i).isMainTest()==  false && tests.get(i).getSiblingTestType().isEmpty()) {
                    count = count +1;
                    tests.get(i).setSelected(true);
                    tests.get(i).setDefaultTest(true);
                    testsRequireds.add(tests.get(i));
                }
                else if(tests.get(i).isRequiredToReport() && !tests.get(i).getSiblingTestType().isEmpty()) {
                    testsRecommended.add(tests.get(i));

                    if(tests.get(i).isMainTest() == true){
                        count = count +1;
                        tests.get(i).setSelected(true);
                        tests.get(i).setDefaultTest(true);
                    }
                    else{
                        tests.get(i).setSelected(false);
                        tests.get(i).setDefaultTest(false);
                    }
                }
                else {
                    tests.get(i).setSelected(false);
                    tests.get(i).setDefaultTest(false);
                    testsAdditional.add(tests.get(i));
                }
            }

            testsRecommended = reorderTestsRecommended();

            String valuesRequireds[] = new String[testsRequireds.size()];
            for(int i=0; i<=testsRequireds.size()-1;i++){
                if(strLanguage.equals("en")) {
                    testsRequireds.get(i).setName(testsRequireds.get(i).getName().toString()
                            .replace("jardas", "yards")
                            .replace("Salto", "Jump")
                    );
                }
                else if(strLanguage.equals("es")) {
                    testsRequireds.get(i).setName(testsRequireds.get(i).getName().toString()
                            .replace("jardas", "yardas")
                            .replace("Salto", "Saltar")
                            .replace("Horizontal", "Jump")
                    );
                }
                else if(strLanguage.equals("es")) {
                    testsRequireds.get(i).setName(testsRequireds.get(i).getName().toString()
                            .replace("jardas", "Cantieri")
                            .replace("Salto", "Saltare")
                            .replace("Horizontal", "Orizzontale")
                    );
                }
                valuesRequireds[i] = testsRequireds.get(i).getId();
            }

            String valuesRecommended[] = new String[testsRecommended.size()];
            for(int i=0; i<=testsRecommended.size()-1;i++)
                valuesRecommended[i] = testsRecommended.get(i).getId();
            String valuesAdditional[] = new String[testsAdditional.size()];
            for(int i=0; i<=testsAdditional.size()-1;i++){
                if(strLanguage.equals("en")) {
                    testsAdditional.get(i).setName(testsAdditional.get(i).getName().toString()
                            .replace("Flexão de Braço", "Arm flexion")
                            .replace("Supino", "Supine")
                            .replace("Agachamento", "Squad")
                    );
                }
                else if(strLanguage.equals("es")) {
                    testsAdditional.get(i).setName(testsAdditional.get(i).getName().toString()
                            .replace("Flexão de Braço", "Flexion de Brazo")
                            .replace("Agachamento", "Rechoncho")
                    );
                }
                valuesAdditional[i] = testsAdditional.get(i).getId();
            }

            totReq = valuesRequireds.length;
            totRecom = valuesRecommended.length;
            totAdd = valuesAdditional.length;

            changeTextTests(textTestRecommended, this.getResources().getString(R.string.test_recommended), totRecom, totRecom/2);
            changeTextTests(textTestRequired,  this.getResources().getString(R.string.required_tests), totReq, totReq);
            changeTextTests(textTestAdditional, this.getResources().getString(R.string.additional_tets), totAdd, 0);

            inflateRecyclerViewRequired(valuesRequireds);
            inflateRecyclerViewRecommended(valuesRecommended);
            inflateRecyclerViewAdditional(valuesAdditional);
            textStatusTests.setText(this.getResources().getString(R.string.reports_complete_results));
        }
    }

    private void changeTextTests(TextView textview, String text, int total, int select){
        textview.setText(text+" ("+select+"/"+total+")");
    }

    private ArrayList<TestTypes> reorderTestsRecommended(){
        ArrayList<TestTypes> tests = new ArrayList<>();
        for(TestTypes test : testsRecommended){
            if(test.isMainTest()) {
                TestTypes testBrother = findBrotherTest(test.getSiblingTestType());
                test.setValueType(testBrother.getName());
                testBrother.setValueType(test.getName());
                tests.add(test);
                tests.add(testBrother);
            }
        }
        return tests;
    }

    private TestTypes findBrotherTest(String id){
        for(TestTypes test : testsRecommended){
            if(test.isMainTest() == false) {
                if (test.getId().equals(id))
                    return test;
            }
        }
        return null;
    }

    private void inflateRecyclerViewRequired(String[] values){
        if(values.length>0) {
            recyclerViewTestsRequired.setHasFixedSize(true);
            recyclerViewTestsRequired.setItemAnimator(new DefaultItemAnimator());
            recyclerViewTestsRequired.setLayoutManager(new GridLayoutManager(this, 1));
            adapterRecyclerTestsRequired = new AdapterRecyclerChooseTestSelective(TestSelectiveActivity.this, testsRequireds, values);
            adapterRecyclerTestsRequired.valuesID = new String[values.length];
            recyclerViewTestsRequired.setAdapter(adapterRecyclerTestsRequired);
        }
    }

    private void inflateRecyclerViewRecommended(String[] values){
        if(values.length>0) {
            recyclerViewRecommended.setHasFixedSize(true);
            recyclerViewRecommended.setItemAnimator(new DefaultItemAnimator());
            recyclerViewRecommended.setLayoutManager(new GridLayoutManager(this, 2));
            adapterRecyclerTestsRecommended = new AdapterRecyclerTestRecommended(TestSelectiveActivity.this, testsRecommended, values);
            adapterRecyclerTestsRecommended.valuesID = new String[values.length];
            recyclerViewRecommended.setAdapter(adapterRecyclerTestsRecommended);
        }
    }

    private void inflateRecyclerViewAdditional(String[] values){
        if(values.length>0) {
            recyclerViewAdditional.setHasFixedSize(true);
            recyclerViewAdditional.setItemAnimator(new DefaultItemAnimator());
            recyclerViewAdditional.setLayoutManager(new GridLayoutManager(this, 1));
            adapterRecyclerTestsAdditional = new AdapterRecyclerTestAdditional(TestSelectiveActivity.this, testsAdditional, values);
            adapterRecyclerTestsAdditional.valuesID = new String[values.length];
            recyclerViewAdditional.setAdapter(adapterRecyclerTestsAdditional);
        }
    }

    private View.OnClickListener clickedRemoveTests= new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            removeAllTests();
        }
    };

    private void removeAllTests(){
        int count =0;
        for(int i=0; i<=tests.size()-1;i++)
            tests.get(i).setSelected(false);


        for (TestTypes test : adapterRecyclerTestsRequired.list){
            if(test.isSelected()) {
                test.setSelected(false);
                adapterRecyclerTestsRequired.notifyItemChanged(count);
            }
            count++;
        }
        count =0;
        for (TestTypes test : adapterRecyclerTestsRecommended.list){
            if(test.isSelected()) {
                test.setSelected(false);
                adapterRecyclerTestsRecommended.notifyItemChanged(count);
            }
            count++;
        }
        count =0;
        for (TestTypes test : adapterRecyclerTestsAdditional.list){
            if(test.isSelected()) {
                test.setSelected(false);
                adapterRecyclerTestsAdditional.notifyItemChanged(count);
            }
            count++;
        }
        //TestSelectiveActivity.linearDelete.setVisibility(View.GONE);
        TestSelectiveActivity.btnNextPass.setVisibility(View.GONE);
    }

    public static void showOrHideRemove(){
       int count = 0;
        for (TestTypes testType : tests){
            if(testType.isSelected()){
                count = count + 1;
                break;
            }
        }
        if(count > 0){
            //TestSelectiveActivity.linearDelete.setVisibility(View.VISIBLE);
            TestSelectiveActivity.btnNextPass.setVisibility(View.VISIBLE);
        }
        else{
            //TestSelectiveActivity.linearDelete.setVisibility(View.GONE);
            TestSelectiveActivity.btnNextPass.setVisibility(View.GONE);
        }
    }

    public static void checkClick(Activity act, String id){
        ((TestSelectiveActivity)act).checkClick(id);
    }

    private void checkClick(String id){
        boolean verify = false;
        int count=0, countReq=0, countRecom=0, countAdd=0;;
        for (TestTypes testType : tests) {
            if (!verify)
                verify = verifyCheck(testType, id);

            //Testes Obrigatórios
            if (testType.isMainTest() == false && testType.isRequiredToReport() && testType.getSiblingTestType().isEmpty() && testType.isSelected()) {
                count = count + 1;
                countReq = countReq+1;
            }
                //Testes Recomendados
            else if (testType.isRequiredToReport() && !testType.getSiblingTestType().isEmpty()&&testType.isSelected()) {
                count = count + 1;
                countRecom = countRecom+1;
            }
            else if(!testType.isMainTest() && !testType.isRequiredToReport() && testType.isSelected())
                countAdd = countAdd+1;
        }

        changeTextTests(textTestRecommended, this.getResources().getString(R.string.test_recommended), totRecom, countRecom);
        changeTextTests(textTestRequired, this.getResources().getString(R.string.test_required), totReq, countReq);
        changeTextTests(textTestAdditional, this.getResources().getString(R.string.test_additional), totAdd, countAdd);

         if(count == 0)
            textStatusTests.setText(this.getResources().getString(R.string.no_test_selected));
        else if(count >0 && count < this.count)
            textStatusTests.setText(this.getResources().getString(R.string.report_partial_results));
        else if(count >=this.count)
            textStatusTests.setText(this.getResources().getString(R.string.reports_complete_results));


    }

    private void verifyTexts(){
        int countReq=0, countRecom=0, countAdd=0;
        for(TestTypes testType : tests){
            if(testType.isMainTest() == false && testType.isRequiredToReport() && testType.getSiblingTestType().isEmpty() && testType.isSelected())
                countReq = countReq+1;
            else if(testType.isRequiredToReport() && !testType.getSiblingTestType().isEmpty()&&testType.isSelected())
                countRecom = countRecom+1;
            else if(!testType.isMainTest() == false && !testType.isRequiredToReport()&& !testType.getSiblingTestType().isEmpty()&&testType.isSelected())
                countAdd = countAdd+1;
        }

        changeTextTests(textTestRecommended, this.getResources().getString(R.string.test_recommended), totRecom, countRecom);
        changeTextTests(textTestRequired, this.getResources().getString(R.string.test_required), totReq, countReq);
        changeTextTests(textTestAdditional, this.getResources().getString(R.string.test_additional), totAdd, countAdd);



    }

    private static boolean verifyCheck(TestTypes testType, String id){
        if (testType.getId().equals(id)){
            if (!testType.isSelected())
                testType.setSelected(false);
            else
                testType.setSelected(true);
            return true;
        }
        return false;
    }

    private View.OnClickListener clickDoneTests = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            doneTests();
        }
    };

    private void doneTests() {
        createSelective();
    }

    private void createSelective(){
        Intent i = new Intent(TestSelectiveActivity.this, InfoSelectiveCreateActivity.class);

        ArrayList<String> testsChoose = new ArrayList<>();

        for (TestTypes test : tests) {
            if(test.isSelected())
                testsChoose.add(test.getId());
        }
        i.putStringArrayListExtra("testsChoose", testsChoose);
        startActivity(i);
    }

    private View.OnClickListener clickListenerTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callUpdateTests();
        }
    };

    private View.OnClickListener btnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TestSelectiveActivity.this.finish();
        }
    };

    public static void returnClickableAlert(Activity act, String whoCalled){
        ((TestSelectiveActivity)act).returnClickableAlert(whoCalled);
    }

    private void returnClickableAlert(String whoCalled){
        if(whoCalled.equals(messageSelectiveCreate))
            finishAfterCreateSelective();
    }

    private void finishAfterCreateSelective(){
        CreateSelectiveActivity.finishActity();
        ChooseTeamSelectiveActivity.finishActity();
        this.finish();
    }

    public static void finishOhterActivity(){
        ((TestSelectiveActivity)act).finish();
    }

    public static void showInfoTest(Activity act, TestTypes test){
        ((TestSelectiveActivity)act).showInfoTest(test);
    }

    private void showInfoTest(TestTypes test){
        ConstraintLayout constraintInfoTest = (ConstraintLayout) findViewById(R.id.constraint_info_test);
        TextView textTitle = (TextView) findViewById(R.id.text_title_test);
        WebView web = (WebView) findViewById(R.id.webview);
        //TextView textDescription = (TextView) findViewById(R.id.text_description_test);
        Button btnClose = (Button) findViewById(R.id.btn_close);
        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView5);
        scroll.setScrollY(0);

        constraintInfoTest.setVisibility(View.VISIBLE);
        textTitle.setText(test.getName());

        web.loadData(Services.convertHtml(test.getDescription(), test.getTutorialImageURL()), "text/html; charset=utf-8", "UTF-8");

        btnClose.setOnClickListener(clickedBtnClose);
    }

    private View.OnClickListener clickedBtnClose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConstraintLayout constraintInfoTest = (ConstraintLayout) findViewById(R.id.constraint_info_test);
            constraintInfoTest.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener clickMessageDefault = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeMessageTestDefault();
        }
    };

    private void closeMessageTestDefault(){
        ConstraintLayout constraint = (ConstraintLayout) findViewById(R.id.constraint_message_test_default);
        constraint.setVisibility(View.GONE);

       // linearDelete.setVisibility(View.VISIBLE);
        btnNextPass.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener clickTestsRequireds = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recyclerViewTestsRequired.getVisibility() == View.VISIBLE) {
                recyclerViewTestsRequired.setVisibility(View.GONE);
                changeDrawableText(textTestRequired, true);
            }
            else {
                recyclerViewTestsRequired.setVisibility(View.VISIBLE);
                changeDrawableText(textTestRequired, false);
            }
        }
    };

    private View.OnClickListener clickTestsRecommended = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recyclerViewRecommended.getVisibility() == View.VISIBLE) {
                recyclerViewRecommended.setVisibility(View.GONE);
                changeDrawableText(textTestRecommended, false);
            }
            else {
                recyclerViewRecommended.setVisibility(View.VISIBLE);
                changeDrawableText(textTestRecommended, true);
            }
        }
    };
    private View.OnClickListener clickTestsAdditional = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recyclerViewAdditional.getVisibility() == View.VISIBLE) {
                recyclerViewAdditional.setVisibility(View.GONE);
                changeDrawableText(textTestAdditional, false);
            }
            else {
                recyclerViewAdditional.setVisibility(View.VISIBLE);
                changeDrawableText(textTestAdditional, true);
            }
        }
    };

    private void changeDrawableText(TextView textView, boolean isMore){
        if(!isMore)
            textView.setCompoundDrawablesWithIntrinsicBounds(this.getResources().getDrawable(R.drawable.ic_add), null, null, null);
        else
            textView.setCompoundDrawablesWithIntrinsicBounds(this.getResources().getDrawable(R.drawable.ic_less), null, null, null);
    }
}
