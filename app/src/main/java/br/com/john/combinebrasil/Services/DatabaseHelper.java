package br.com.john.combinebrasil.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.Results;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.SelectiveAthletes;
import br.com.john.combinebrasil.Classes.SelectiveUsers;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Classes.TeamUsers;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Classes.User;

/**
 * Created by GTAC on 17/10/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    String DB_PATH = null;
    private static String DB_NAME = "Combine.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public DatabaseHelper(Context myContext) {
        super(myContext, DB_NAME, null, DATABASE_VERSION);
        this.myContext = myContext;
        this.DB_PATH = "/data/data/" + myContext.getPackageName() + "/" + "databases/";
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public boolean deleteDatabase(){
        File fdelete = new File(myDataBase.getPath());
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + myDataBase.getPath());
                return true;
            } else {
                System.out.println("file not Deleted :" + myDataBase.getPath());
                return false;
            }
        }
        return false;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
        }

        if (checkDB != null)
            myDataBase = checkDB;

        return checkDB != null ? true : false;
    }

    public long delete(String table, String id){
        this.openDataBase();
        long ret = 0;
        ret = this.myDataBase.delete(table, "id = ?", new String[] { id });
        this.myDataBase.close();
        return  ret;
    }

    public int deleteValue(String table, String id){
        this.openDataBase();
        int ret = 0;
        try {
            String selectQuery = "DELETE FROM "+table+" WHERE _id='"+id+"'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            ret = c.getCount();

        } catch (Exception e) {
            Log.i("ERROR DELETE", e.getMessage());
        }
        return ret;

    }

    private static boolean ret;

    public boolean checkID(String table, String id){
        SQLiteDatabase db = getWritableDatabase();
        try {
            String selectQuery = "SELECT id FROM " + table + " WHERE id = '" + id + "'";
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                c.getString(c.getColumnIndex(Constants.ID));
                ret = true;
            } else {
                ret = false;
            }
            c.close();
            db.close();
        }
        catch(SQLiteException e){
            ret = false;
        }
        return ret == true ? true : false;
    }

    public void dropDatabase(){
        myContext.deleteDatabase(DB_NAME);
    }

    /*****************************************INSERT IN DATABASE*****************************************************/

    public void addAthletes(ArrayList<Athletes> listAthletes) {
        long ret = 0;
        this.openDataBase();
        try{
            for (Athletes obj : listAthletes) {
                Athletes athlete = this.getAthleteByValue(Constants.ATHLETES_CODE, obj.getDesirablePosition());
                if(athlete==null){
                    ContentValues values = new ContentValues();

                    values.put(Constants.ATHLETES_ID, obj.getId());
                    values.put(Constants.ATHLETES_NAME, obj.getName());
                    values.put(Constants.ATHLETES_BIRTHDAY, obj.getBirthday());
                    values.put(Constants.ATHLETES_CPF, obj.getCPF());
                    values.put(Constants.ATHLETES_ADDRESS, obj.getAddress());
                    values.put(Constants.ATHLETES_DESIRABLE_POSITION, obj.getDesirablePosition());
                    values.put(Constants.ATHLETES_HEIGHT, obj.getHeight());
                    values.put(Constants.ATHLETES_WEIGHT, obj.getWeight());
                    values.put(Constants.ATHLETES_CREATEDAT, obj.getCreatedAt());
                    values.put(Constants.ATHLETES_UPDATEAT, obj.getUpdateAt());
                    values.put(Constants.ATHLETES_CODE, obj.getCode());
                    values.put(Constants.ATHLETES_EMAIL, obj.getEmail());
                    values.put(Constants.ATHLETES_PHONE, obj.getPhoneNumber());
                    values.put(Constants.ATHLETES_SYNC, Services.convertBoolInInt(obj.getSync()));
                    values.put(Constants.ATHLETES_TERMSACCEPTED, Services.convertBoolInInt(obj.getTermsAccepted()));
                    values.put(Constants.ATHLETES_IMAGE_URL, obj.getURLImage());

                    ret = myDataBase.insert(Constants.TABLE_ATHLETES, null, values);
                }
                else if(!athlete.getSync()){
                    this.updateAthlete(athlete);
                }
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public long addAthlete(Athletes athlete) {
        long ret = 0;
        this.openDataBase();
        try{
                ContentValues values = new ContentValues();

                values.put(Constants.ATHLETES_ID, athlete.getId());
                values.put(Constants.ATHLETES_NAME, athlete.getName());
                values.put(Constants.ATHLETES_BIRTHDAY, athlete.getBirthday());
                values.put(Constants.ATHLETES_CPF, athlete.getCPF());
                values.put(Constants.ATHLETES_ADDRESS, athlete.getAddress());
                values.put(Constants.ATHLETES_DESIRABLE_POSITION, athlete.getDesirablePosition());
                values.put(Constants.ATHLETES_HEIGHT, athlete.getHeight());
                values.put(Constants.ATHLETES_WEIGHT, athlete.getWeight());
                values.put(Constants.ATHLETES_CREATEDAT, athlete.getCreatedAt());
                values.put(Constants.ATHLETES_UPDATEAT, athlete.getUpdateAt());
                values.put(Constants.ATHLETES_CODE, athlete.getCode());
                values.put(Constants.ATHLETES_EMAIL, athlete.getEmail());
                values.put(Constants.ATHLETES_PHONE, athlete.getPhoneNumber());
                values.put(Constants.ATHLETES_SYNC, Services.convertBoolInInt(athlete.getSync()));
                values.put(Constants.ATHLETES_TERMSACCEPTED, Services.convertBoolInInt(athlete.getTermsAccepted()));
                values.put(Constants.ATHLETES_IMAGE_URL, athlete.getURLImage());

                return ret = myDataBase.insert(Constants.TABLE_ATHLETES, null, values);

        }catch (Exception e){
            Log.i("Error", e.getMessage());
            return 0;
        }
    }

    public void addPositions(ArrayList<Positions> positions) {
        long ret = 0;
        this.openDataBase();
        try{
            for (Positions obj : positions) {
                ContentValues values = new ContentValues();

                values.put(Constants.POSITIONS_ID, obj.getID());
                values.put(Constants.POSITIONS_NAME, obj.getNAME());
                values.put(Constants.POSITIONS_DESCRIPTIONS, obj.getDESCRIPTION());

                ret = myDataBase.insert(Constants.TABLE_POSITIONS, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addSelectivesAthletes(ArrayList<SelectiveAthletes> selectiveAthletes) {
        long ret = 0;
        this.openDataBase();
        try{
            for (SelectiveAthletes obj : selectiveAthletes) {
                SelectiveAthletes sel = this.getSelectiveAthletesFromId(obj.getInscriptionNumber());
                if(sel==null) {
                    ContentValues values = new ContentValues();

                    values.put(Constants.SELECTIVEATHLETES_ID, obj.getId());
                    values.put(Constants.SELECTIVEATHLETES_ATHLETE, obj.getAthlete());
                    values.put(Constants.SELECTIVEATHLETES_SELECTIVE, obj.getSelective());
                    values.put(Constants.SELECTIVEATHLETES_PRESENCE, Services.convertBoolInInt(obj.getPresence()));
                    values.put(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER, obj.getInscriptionNumber().toUpperCase());

                    ret = myDataBase.insert(Constants.TABLE_SELECTIVEATHLETES, null, values);
                }else if(sel.getId().equals(obj.getId())){
                    this.updateSelectiveAthlete(obj);
                }
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addSelectiveAthlete(SelectiveAthletes selectiveAthlete) {
        long ret = 0;
        this.openDataBase();
        try{
                ContentValues values = new ContentValues();

                values.put(Constants.SELECTIVEATHLETES_ID, selectiveAthlete.getId());
                values.put(Constants.SELECTIVEATHLETES_ATHLETE, selectiveAthlete.getAthlete());
                values.put(Constants.SELECTIVEATHLETES_SELECTIVE, selectiveAthlete.getSelective());
                values.put(Constants.SELECTIVEATHLETES_PRESENCE, Services.convertBoolInInt(selectiveAthlete.getPresence()));
                values.put(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER, selectiveAthlete.getInscriptionNumber().toUpperCase());

                ret = myDataBase.insert(Constants.TABLE_SELECTIVEATHLETES, null, values);
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addSelectives(ArrayList<Selective> selectives) {
        long ret = 0;
        this.openDataBase();
        try{
            for (Selective obj : selectives) {
                ContentValues values = new ContentValues();
                values.put(Constants.SELECTIVES_ID, obj.getId());
                values.put(Constants.SELECTIVES_TITLE, obj.getTitle());
                values.put(Constants.SELECTIVES_TEAM, obj.getTeam());
                values.put(Constants.SELECTIVES_DATE, obj.getDate());
                values.put(Constants.SELECTIVES_CODESELECTIVE, obj.getCodeSelective().toString());
                values.put(Constants.SELECTIVES_CANSYNC, obj.getCanSync());
                values.put(Constants.SELECTIVES_ADDRESS,obj.getAddress());
                values.put(Constants.SELECTIVES_CITY, obj.getCity());
                values.put(Constants.SELECTIVES_NEIGHBORHOOD, obj.getNeighborhood());
                values.put(Constants.SELECTIVES_STATE, obj.getState());
                values.put(Constants.SELECTIVES_STREET, obj.getStreet());
                values.put(Constants.SELECTIVES_POSTALCODE, obj.getPostalCode());
                values.put(Constants.SELECTIVES_NOTE, obj.getNotes());
                values.put(Constants.SELECTIVES_PRICE, obj.getPrice());
                values.put(Constants.SELECTIVES_PAY, obj.isPay());

                ret = myDataBase.insert(Constants.TABLE_SELECTIVES, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addSelectivesUsers(ArrayList<SelectiveUsers> selectives) {
        long ret = 0;
        this.openDataBase();
        try{
            for (SelectiveUsers obj : selectives) {
                ContentValues values = new ContentValues();
                Selective selective = new Selective(
                        obj.getId(),
                        obj.getTitle(),
                        obj.getTeam(),
                        obj.getUser(),
                        obj.getDate(),
                        obj.getCodeSelective(),
                        obj.getCanSync(),
                        obj.getAddress(),
                        obj.getCity(),
                        obj.getNeighborhood(),
                        obj.getState(),
                        obj.getStreet(),
                        obj.getPostalCode(),
                        obj.getNotes(),
                        obj.isPay(),
                        obj.getPrice()

                );

                this.addSelective(selective);
                values.put("Selective", obj.getId());
                values.put(Constants.USER_SELECTIVE_IS_ADMIN, Services.convertBoolInInt(obj.isSelectiveAdmin()));
                values.put(Constants.USER_NAME, obj.getNameAdmin());
                values.put(Constants.TEAM_URL_IMAGE, obj.getUrlImage());

                ret = myDataBase.insert(Constants.TABLE_SELECTIVES, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public long addSelective(Selective obj) {
        long ret = 0;
        this.openDataBase();
        try{
            String selectQuery =
                            "INSERT INTO "+Constants.TABLE_SELECTIVES+" ("
                            +Constants.SELECTIVES_ID+", "
                            +Constants.SELECTIVES_TITLE+", "
                            +Constants.SELECTIVES_TEAM+", "
                            +Constants.SELECTIVES_DATE+", "
                            +Constants.SELECTIVES_CODESELECTIVE+", "
                            +Constants.SELECTIVES_CANSYNC+", "
                            +Constants.SELECTIVES_ADDRESS+", "
                            +Constants.SELECTIVES_CITY+", "
                            +Constants.SELECTIVES_NEIGHBORHOOD+", "
                            +Constants.SELECTIVES_STATE+", "
                            +Constants.SELECTIVES_STREET+", "
                            +Constants.SELECTIVES_POSTALCODE+", "
                            +Constants.SELECTIVES_NOTE+", "
                            +Constants.SELECTIVES_USER+", "
                            +Constants.SELECTIVES_PRICE+", "
                            +Constants.SELECTIVES_PAY+") values ("
                            +"'"+obj.getId()+"', "
                            +"'"+obj.getTitle()+"', "
                            +"'"+obj.getTeam()+"', "
                            +"'"+obj.getDate().toString()+"', "
                            +"'"+obj.getCodeSelective().toString()+"', "
                            +Services.convertBoolInInt(obj.getCanSync())+", "
                            +"'"+obj.getAddress()+"', "
                            +"'"+obj.getCity()+"', "
                            +"'"+obj.getNeighborhood()+"', "
                            +"'"+obj.getState()+"', "
                            +"'"+obj.getStreet()+"', "
                            +"'"+obj.getPostalCode()+"', "
                            +"'"+obj.getNotes()+"', "
                            +"'"+obj.getUser()+"', "
                            +"'"+obj.getPrice()+"', "
                            +Services.convertBoolInInt(obj.isPay())+") ";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            ret = c.getCount();

           /* ContentValues values = new ContentValues();
            values.put(Constants.SELECTIVES_ID, obj.getId());
            values.put(Constants.SELECTIVES_TITLE, obj.getTitle());
            values.put(Constants.SELECTIVES_TEAM, obj.getTeam());
            values.put(Constants.SELECTIVES_DATE, obj.getDate());
            values.put(Constants.SELECTIVES_CODESELECTIVE, obj.getCodeSelective().toString());
            values.put(Constants.SELECTIVES_CANSYNC, obj.isCanSync());
            values.put(Constants.SELECTIVES_ADDRESS,obj.getAddress());
            values.put(Constants.SELECTIVES_CITY, obj.getCity());
            values.put(Constants.SELECTIVES_NEIGHBORHOOD, obj.getNeighborhood());
            values.put(Constants.SELECTIVES_STATE, obj.getState());
            values.put(Constants.SELECTIVES_STREET, obj.getStreet());
            values.put(Constants.SELECTIVES_POSTALCODE, obj.getPostalCode());
            values.put(Constants.SELECTIVES_NOTE, obj.getNotes());
            values.put(Constants.SELECTIVES_PRICE, obj.getPrice());
            values.put(Constants.SELECTIVES_PAY, obj.isPay());

            ret = myDataBase.insert(Constants.TABLE_SELECTIVES, null, values);*/
            return ret;

        }catch (Exception e){
            Log.i("Error", e.getMessage());
            return 0;
        }
    }


    public void addTeamUsers(ArrayList<TeamUsers> teamUserses) {
        long ret = 0;
        this.openDataBase();
        try{
            for (TeamUsers obj : teamUserses) {
                ContentValues values = new ContentValues();

                values.put(Constants.TEAMUSERS_ID, obj.getId());
                values.put(Constants.TEAMUSERS_USER, obj.getUser());
                values.put(Constants.TEAMUSERS_TEAM, obj.getTeam());

                ret = myDataBase.insert(Constants.TABLE_TEAMUSERS, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addTeamUser(TeamUsers teamUserses) {
        long ret = 0;
        this.openDataBase();
        try{
            ContentValues values = new ContentValues();

            values.put(Constants.TEAMUSERS_ID, teamUserses.getId());
            values.put(Constants.TEAMUSERS_USER, teamUserses.getUser());
            values.put(Constants.TEAMUSERS_TEAM, teamUserses.getTeam());

            ret = myDataBase.insert(Constants.TABLE_TEAMUSERS, null, values);

        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addTeam(ArrayList<Team> teams) {
        long ret = 0;
        this.openDataBase();
        try{
            for (Team obj : teams) {
                ContentValues values = new ContentValues();

                values.put(Constants.TEAM_ID, obj.getId());
                values.put(Constants.TEAM_NAME, obj.getName());
                values.put(Constants.TEAM_CITY, obj.getCity());
                values.put(Constants.TEAM_MODALITY, obj.getModality());
                values.put(Constants.TEAM_SOCIAL_LINK, obj.getSocialLink());
                values.put(Constants.TEAM_PRESIDENTNAME, obj.getPresidentName());
                values.put(Constants.TEAM_EMAIL, obj.getEmail());
                values.put(Constants.TEAM_URL_IMAGE, obj.getUrlImage());
                values.put(Constants.TEAM_CODE, obj.getCode());

                ret = myDataBase.insert(Constants.TABLE_TEAM, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addTestTypes(ArrayList<TestTypes> testTypes) {
        long ret = 0;
        this.openDataBase();
        try{
            for (TestTypes obj : testTypes) {
                ContentValues values = new ContentValues();

                values.put(Constants.TESTTYPES_ID, obj.getId());
                values.put(Constants.TESTTYPES_NAME, obj.getName());
                values.put(Constants.TESTTYPES_ATTEMPTSLIMIT, obj.getAttemptsLimit());
                values.put(Constants.TESTTYPES_VISIBLETOREPORT, Services.convertBoolInInt(obj.getVisibleToReport()));
                values.put(Constants.TESTTYPES_DESCRIPTION, obj.getDescription());
                values.put(Constants.TESTTYPES_VALUETYPES, obj.getValueType());
                values.put(Constants.TESTTYPES_ICONIMAGEURL, obj.getIconImageURL());
                values.put(Constants.TESTTYPES_TUTORIALIMAGEURL, obj.getTutorialImageURL());
                values.put(Constants.TESTTYPES_REQUIRED_TO_REPORT, obj.isRequiredToReport());
                values.put(Constants.TESTTYPES_MAIN_TEST, obj.isMainTest());
                values.put(Constants.TESTTYPES_SIBLING_TEST_TYPE, obj.getSiblingTestType());

                ret = myDataBase.insert(Constants.TABLE_TESTTYPES, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void addTests(ArrayList<Tests> tests) {
        long ret = 0;
        this.openDataBase();
        try{
            for (Tests obj : tests) {
                String selectQuery = "INSERT INTO " + Constants.TABLE_TESTS +" ('" +
                        Constants.TESTS_ID+"','"+
                        Constants.TESTS_SELECTIVE+"','"+
                        Constants.TESTS_ATHLETE+"','"+
                        Constants.TESTS_TYPE+"','"+
                        Constants.TESTS_USER+"','"+
                        Constants.TESTS_VALUES+"','"+
                        Constants.TESTS_FIRST_VALUE+"','"+
                        Constants.TESTS_SECOND_VALUE+"','"+
                        Constants.TESTS_WINGSPAN+"','"+
                        Constants.TESTS_RATING+"','"+
                        Constants.TESTS_CANSYNC+"','"+
                        Constants.TESTS_SYNC+"') VALUES ('"+
                        obj.getId()+"','"+
                        obj.getSelective()+"','"+
                        obj.getAthlete()+"','"+
                        obj.getType()+"','"+
                        obj.getUser()+"','"+
                        obj.getValues()+"',"+
                        obj.getFirstValue()+","+
                        obj.getSecondValue()+",'"+
                        obj.getWingspan()+"',"+
                        obj.getRating()+","+
                        Services.convertBoolInInt(obj.getCanSync())+","+
                        obj.getSync()+")";

                Cursor c = myDataBase.rawQuery(selectQuery, null);
                ret = c.getCount();
                Log.i("Add test resp", ret+"");
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public long addTest(Tests test) {
        long ret = 0;
        this.openDataBase();
        try{
            String selectQuery = "INSERT INTO " + Constants.TABLE_TESTS +" ('" +
                    Constants.TESTS_ID+"','"+
                    Constants.TESTS_SELECTIVE+"','"+
                    Constants.TESTS_ATHLETE+"','"+
                    Constants.TESTS_TYPE+"','"+
                    Constants.TESTS_USER+"','"+
                    Constants.TESTS_VALUES+"','"+
                    Constants.TESTS_FIRST_VALUE+"','"+
                    Constants.TESTS_SECOND_VALUE+"','"+
                    Constants.TESTS_WINGSPAN+"','"+
                    Constants.TESTS_RATING+"','"+
                    Constants.TESTS_CANSYNC+"','"+
                    Constants.TESTS_SYNC+"') VALUES ('"+
                    test.getId()+"','"+
                    test.getSelective()+"','"+
                    test.getAthlete()+"','"+
                    test.getType()+"','"+
                    test.getUser()+"','"+
                    test.getValues()+"',"+
                    test.getFirstValue()+","+
                    test.getSecondValue()+",'"+
                    test.getWingspan()+"',"+
                    test.getRating()+","+
                    Services.convertBoolInInt(test.getCanSync())+","+
                    test.getSync()+")";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            ret = c.getCount();
            Log.i("Add test resp", ret+"");
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
        this.close();
        return  ret;
    }

    public void addUser(User user) {
        long ret = 0;
        this.openDataBase();
        try{
                ContentValues values = new ContentValues();

                values.put(Constants.USER_ID, user.getId());
                values.put(Constants.USER_NAME, user.getName());
                values.put(Constants.USER_EMAIL, user.getEmail());
                values.put(Constants.USER_ISADMIN, Services.convertBoolInInt(user.getIsAdmin()));
                values.put(Constants.USER_CANWRITE, Services.convertBoolInInt(user.getCanWrite()));
                values.put(Constants.USER_TOKEN, user.getToken());

                ret = myDataBase.insert(Constants.TABLE_USER, null, values);
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
        this.close();
    }

    public void addUsers(ArrayList<User> listUser) {
        long ret = 0;
        this.openDataBase();
        try{
            for (User obj : listUser) {
                ContentValues values = new ContentValues();

                values.put(Constants.USER_ID, obj.getId());
                values.put(Constants.USER_NAME, obj.getName());
                values.put(Constants.USER_EMAIL, obj.getEmail());
                values.put(Constants.USER_ISADMIN, Services.convertBoolInInt(obj.getIsAdmin()));
                values.put(Constants.USER_CANWRITE, Services.convertBoolInInt(obj.getCanWrite()));
                values.put(Constants.USER_TOKEN, obj.getToken());

                ret = myDataBase.insert(Constants.TABLE_USER, null, values);
            }
        }catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    public void deleteTable(String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, null, null);
        db.close();
    }

    /****************************************SELECT DATABASE********************************************************/

    public long getCountTable(String table){
        this.openDataBase();
       return  DatabaseUtils.longForQuery(myDataBase, "SELECT COUNT(*) FROM "+table, null);
    }

    public long getCountTest(String idTest){
        this.openDataBase();
        return  DatabaseUtils.longForQuery(myDataBase, "SELECT COUNT(*) FROM "
                +Constants.TABLE_TESTS +" WHERE ("
                +Constants.TESTS_TYPE+" ='"+idTest+"' " +
                "and "
                +Constants.TESTS_CANSYNC+"=1)", null);
    }

    public long getCountTestSync(String idTest){
        this.openDataBase();
        return  DatabaseUtils.longForQuery(myDataBase, "SELECT COUNT(*) FROM "+Constants.TABLE_TESTS +
                " WHERE "+Constants.TESTS_TYPE+" ='"+idTest+"' AND "+Constants.TESTS_SYNC+"=1", null);
    }

    public long getCountTetsSync(String idTest){
        this.openDataBase();
        return  DatabaseUtils.longForQuery(myDataBase, "SELECT COUNT(*) FROM "+Constants.TABLE_TESTS +" " +
                "WHERE "+Constants.TESTS_SYNC+" =1 and "+Constants.TESTS_TYPE+"='"+idTest+"'", null);
    }

    public ArrayList<Athletes> getAthletes() {
        this.openDataBase();
        ArrayList<Athletes> itens = new ArrayList<Athletes>();
        try {
            String selectQuery = "SELECT * FROM " + Constants.TABLE_ATHLETES;
            Cursor c = myDataBase.rawQuery(selectQuery, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String code = c.getString(c.getColumnIndex(Constants.ATHLETES_CODE));
                    if(code == null)
                        code = "";
                    Athletes obj = new Athletes(
                            c.getString(c.getColumnIndex(Constants.ATHLETES_ID)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_NAME)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_BIRTHDAY)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_CPF)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_ADDRESS)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_DESIRABLE_POSITION)),
                            c.getDouble(c.getColumnIndex(Constants.ATHLETES_HEIGHT)),
                            c.getDouble(c.getColumnIndex(Constants.ATHLETES_WEIGHT)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_CREATEDAT)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_UPDATEAT)),
                            code,
                            c.getString(c.getColumnIndex(Constants.ATHLETES_EMAIL)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_PHONE)),
                            Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_SYNC))),
                            Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_TERMSACCEPTED))),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_IMAGE_URL))
                    );
                    itens.add(obj);
                } while (c.moveToNext());

            } else {
                itens = null;
            }
            c.close();
            this.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return itens;
    }

    public ArrayList<SelectiveAthletes> getSelectivesAthletes() {
        this.openDataBase();
        ArrayList<SelectiveAthletes> itens = new ArrayList<SelectiveAthletes>();
        try {
            String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVEATHLETES;
            Cursor c = myDataBase.rawQuery(selectQuery, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    SelectiveAthletes obj = new SelectiveAthletes(
                            c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ID)),
                            c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ATHLETE)),
                            c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_SELECTIVE)),
                            c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER)).toUpperCase(),
                            Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVEATHLETES_PRESENCE)))
                    );
                    itens.add(obj);
                } while (c.moveToNext());

            } else {
                itens = null;
            }
            c.close();
            this.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return itens;
    }

    public SelectiveAthletes getSelectiveAthletesFromId(String id) {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVEATHLETES +
                " WHERE "+Constants.SELECTIVEATHLETES_ID+"='"+id+"'";
        Cursor c = myDataBase.rawQuery(selectQuery, null);
        SelectiveAthletes obj= new SelectiveAthletes();
        if (c.moveToNext()) {
            obj= new SelectiveAthletes(
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ID)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ATHLETE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_SELECTIVE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER)).toUpperCase(),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVEATHLETES_PRESENCE)))
            );

        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public SelectiveAthletes getSelectiveAthletesFromCode(String code) {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVEATHLETES +
                " WHERE "+Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER+"='"+code.toUpperCase()+"'";
        Cursor c = myDataBase.rawQuery(selectQuery, null);
        SelectiveAthletes obj= new SelectiveAthletes();
        if (c.moveToNext()) {
            obj= new SelectiveAthletes(
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ID)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ATHLETE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_SELECTIVE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER)).toUpperCase(),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVEATHLETES_PRESENCE)))
            );

        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public SelectiveAthletes getSelectiveAthletesFromAthlete(String athlete) {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVEATHLETES +
                " WHERE "+Constants.SELECTIVEATHLETES_ATHLETE+"='"+athlete+"'";
        Cursor c = myDataBase.rawQuery(selectQuery, null);
        SelectiveAthletes obj= new SelectiveAthletes();
        if (c.moveToNext()) {
            obj= new SelectiveAthletes(
                c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ID)),
                c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_ATHLETE)),
                c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_SELECTIVE)),
                c.getString(c.getColumnIndex(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER)).toUpperCase(),
                Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVEATHLETES_PRESENCE)))
                );

        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public ArrayList<Selective> getSelectives() {
        this.openDataBase();
        ArrayList<Selective> itens = new ArrayList<Selective>();
        try {
            String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVES;
            Cursor c = myDataBase.rawQuery(selectQuery, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    Selective obj = new Selective();
                    obj.setId(c.getString(c.getColumnIndex(Constants.SELECTIVES_ID)));
                    obj.setTitle(c.getString(c.getColumnIndex(Constants.SELECTIVES_TITLE)));
                    obj.setTeam(c.getString(c.getColumnIndex(Constants.SELECTIVES_TEAM)));
                    obj.setCodeSelective(c.getString(c.getColumnIndex(Constants.SELECTIVES_CODESELECTIVE)));
                    obj.setDate(c.getString(c.getColumnIndex(Constants.SELECTIVES_DATE)));
                    obj.setAddress(c.getString(c.getColumnIndex(Constants.SELECTIVES_ADDRESS)));
                    obj.setCity(c.getString(c.getColumnIndex(Constants.SELECTIVES_CITY)));
                    obj.setNeighborhood(c.getString(c.getColumnIndex(Constants.SELECTIVES_NEIGHBORHOOD)));
                    obj.setNotes(c.getString(c.getColumnIndex(Constants.SELECTIVES_NOTE)));
                    obj.setPostalCode(c.getString(c.getColumnIndex(Constants.SELECTIVES_POSTALCODE)));
                    obj.setState(c.getString(c.getColumnIndex(Constants.SELECTIVES_STATE)));
                    obj.setStreet(c.getString(c.getColumnIndex(Constants.SELECTIVES_STREET)));
                    obj.setCanSync(Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_CANSYNC))));
                    itens.add(obj);
                } while (c.moveToNext());

            } else {
                itens = null;
            }
            c.close();
            this.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return itens;
    }

    public Selective getSelective() {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVES;

        Cursor c = myDataBase.rawQuery(selectQuery, null);
        Selective obj= new Selective();
        if (c.moveToNext()) {
            obj= new Selective(
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_ID)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_TITLE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_TEAM)),
                    c.getString(c.getColumnIndex(Constants.USER_SELECTIVE_USER)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_DATE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_CODESELECTIVE)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_CANSYNC))),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_CITY)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_NEIGHBORHOOD)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_STATE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_STREET)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_POSTALCODE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_NOTE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_ADDRESS)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_PAY))),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_PRICE))
            );

        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public ArrayList<Positions> getPositions() {
        this.openDataBase();
        ArrayList<Positions> itens = new ArrayList<Positions>();
        try {
            String selectQuery = "SELECT * FROM " + Constants.TABLE_POSITIONS;
            Cursor c = myDataBase.rawQuery(selectQuery, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    Positions obj = new Positions(
                            c.getString(c.getColumnIndex(Constants.POSITIONS_ID)),
                            c.getString(c.getColumnIndex(Constants.POSITIONS_NAME)),
                            c.getString(c.getColumnIndex(Constants.POSITIONS_DESCRIPTIONS))
                    );
                    itens.add(obj);
                } while (c.moveToNext());

            } else {
                itens = null;
            }
            c.close();
            this.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return itens;
    }

    public Team getTeam() {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_TEAM;

        Cursor c = myDataBase.rawQuery(selectQuery, null);
        Team obj= new Team();
        if (c.getCount()>0) {
            obj= new Team(
                    c.getString(c.getColumnIndex(Constants.TEAM_ID)),
                    c.getString(c.getColumnIndex(Constants.TEAM_NAME)),
                    c.getString(c.getColumnIndex(Constants.TEAM_CITY)),
                    c.getString(c.getColumnIndex(Constants.TEAM_MODALITY)),
                    c.getString(c.getColumnIndex(Constants.TEAM_SOCIAL_LINK)),
                    c.getString(c.getColumnIndex(Constants.TEAM_PRESIDENTNAME)),
                    c.getString(c.getColumnIndex(Constants.TEAM_EMAIL)),
                    c.getString(c.getColumnIndex(Constants.TEAM_URL_IMAGE)),
                    c.getString(c.getColumnIndex(Constants.TEAM_CODE))
            );

        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public ArrayList<Team> getTeams() {
        this.openDataBase();
        ArrayList<Team> itens = new ArrayList<Team>();
        try {
            String selectQuery = "SELECT * FROM " + Constants.TABLE_TEAM;
            Cursor c = myDataBase.rawQuery(selectQuery, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    Team obj = new Team(
                            c.getString(c.getColumnIndex(Constants.TEAM_ID)),
                            c.getString(c.getColumnIndex(Constants.TEAM_NAME)),
                            c.getString(c.getColumnIndex(Constants.TEAM_CITY)),
                            c.getString(c.getColumnIndex(Constants.TEAM_MODALITY)),
                            c.getString(c.getColumnIndex(Constants.TEAM_SOCIAL_LINK)),
                            c.getString(c.getColumnIndex(Constants.TEAM_PRESIDENTNAME)),
                            c.getString(c.getColumnIndex(Constants.TEAM_EMAIL)),
                            c.getString(c.getColumnIndex(Constants.TEAM_URL_IMAGE)),
                            c.getString(c.getColumnIndex(Constants.TEAM_CODE))
                    );
                    itens.add(obj);
                } while (c.moveToNext());

            } else {
                itens = null;
            }
            c.close();
            this.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return itens;
    }

    public Selective getSelectiveFromTeam(String team) {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVES +" WHERE "
                +Constants.SELECTIVES_TEAM +"='"+team+"'" ;

        Cursor c = myDataBase.rawQuery(selectQuery, null);
        Selective obj= new Selective();
        if ( c.moveToFirst()) {
            obj= new Selective(
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_ID)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_TITLE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_TEAM)),
                    c.getString(c.getColumnIndex(Constants.USER_SELECTIVE_USER)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_DATE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_CODESELECTIVE)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_CANSYNC))),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_CITY)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_NEIGHBORHOOD)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_STATE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_STREET)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_POSTALCODE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_NOTE)),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_ADDRESS)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_PAY))),
                    c.getString(c.getColumnIndex(Constants.SELECTIVES_PRICE))
            );

        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public Selective getSelectiveFromId(String id) {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_SELECTIVES +" WHERE "
                +Constants.SELECTIVES_ID +"='"+id+"'" ;

        Cursor c = myDataBase.rawQuery(selectQuery, null);
        Selective obj= new Selective();
        if ( c.moveToFirst()) {
            obj= new Selective();
            obj.setId(c.getString(c.getColumnIndex(Constants.SELECTIVES_ID)));
            obj.setTitle(c.getString(c.getColumnIndex(Constants.SELECTIVES_TITLE)));
            obj.setTeam(c.getString(c.getColumnIndex(Constants.SELECTIVES_TEAM)));
            obj.setDate(c.getString(c.getColumnIndex(Constants.SELECTIVES_DATE)));
            obj.setCodeSelective(c.getString(c.getColumnIndex(Constants.SELECTIVES_CODESELECTIVE)));
            obj.setAddress(c.getString(c.getColumnIndex(Constants.SELECTIVES_ADDRESS)));
            obj.setNotes(c.getString(c.getColumnIndex(Constants.SELECTIVES_NOTE)));
            obj.setPostalCode(c.getString(c.getColumnIndex(Constants.SELECTIVES_POSTALCODE)));
            obj.setCanSync(Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_CANSYNC))));
            obj.setPay(Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.SELECTIVES_PAY))));
            obj.setPrice(c.getString(c.getColumnIndex(Constants.SELECTIVES_PRICE)));
        } else {
            obj = null;
        }
        c.close();
        this.close();

        return obj;
    }

    public ArrayList<Tests> getTests() {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_TESTS;
        Cursor c = myDataBase.rawQuery(selectQuery, null);

        ArrayList<Tests> itens = new ArrayList<Tests>();

        if (c.getCount()>0) {
            c.moveToFirst();
            do {
                Tests obj = new Tests(
                        c.getString(c.getColumnIndex(Constants.TESTS_ID)),
                        c.getString(c.getColumnIndex(Constants.TESTS_TYPE)),
                        c.getString(c.getColumnIndex(Constants.TESTS_ATHLETE)),
                        c.getString(c.getColumnIndex(Constants.TESTS_SELECTIVE)),
                        c.getLong(c.getColumnIndex(Constants.TESTS_FIRST_VALUE)),
                        c.getLong(c.getColumnIndex(Constants.TESTS_SECOND_VALUE)),
                        c.getFloat(c.getColumnIndex(Constants.TESTS_RATING)),
                        c.getString(c.getColumnIndex(Constants.TESTS_WINGSPAN)),
                        c.getString(c.getColumnIndex(Constants.TESTS_USER)),
                        c.getInt(c.getColumnIndex(Constants.TESTS_SYNC)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTS_CANSYNC))),
                        c.getString(c.getColumnIndex(Constants.TESTS_VALUES))
                );

                itens.add(obj);
            } while (c.moveToNext());

        } else {
            itens = null;
        }
        c.close();
        this.close();

        return itens;
    }

    public String getNameTeamByIdTeam(String idTeam){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT "+Constants.TEAM_NAME+" FROM "+ Constants.TABLE_TEAM +
                " WHERE "+Constants.TEAM_ID +" ='"+idTeam+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        String nameTeam = "";

        if (c.moveToFirst()) {
            nameTeam = c.getString(c.getColumnIndex(Constants.TEAM_NAME));
        } else {
            nameTeam = "";
        }
        c.close();
        this.close();
        return nameTeam;
    }

    public Team getTeamByIdTeam(String idTeam){
        this.openDataBase();
        String selectQuery = "SELECT * FROM "+ Constants.TABLE_TEAM +
                " WHERE "+Constants.TEAM_ID +" ='"+idTeam+"' LIMIT 1";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        Team team = new Team();

        if (c.moveToFirst()) {
            team = new Team(
                    c.getString(c.getColumnIndex(Constants.TEAM_ID)),
                    c.getString(c.getColumnIndex(Constants.TEAM_NAME)),
                    c.getString(c.getColumnIndex(Constants.TEAM_CITY)),
                    c.getString(c.getColumnIndex(Constants.TEAM_MODALITY)),
                    c.getString(c.getColumnIndex(Constants.TEAM_SOCIAL_LINK)),
                    c.getString(c.getColumnIndex(Constants.TEAM_PRESIDENTNAME)),
                    c.getString(c.getColumnIndex(Constants.TEAM_EMAIL)),
                    c.getString(c.getColumnIndex(Constants.TEAM_URL_IMAGE)),
                    c.getString(c.getColumnIndex(Constants.TEAM_CODE))
            );
        } else {
            team = null;
        }
        c.close();
        this.close();
        return team;
    }

    public ArrayList<Team> getSearchTeambyName(String search){
        this.openDataBase();
        ArrayList<Team> teams = new ArrayList<>();
        Team team = new Team();
        String selectQuery = "SELECT * FROM "+ Constants.TABLE_TEAM +
                " WHERE "+Constants.TEAM_NAME +" LIKE '%"+search+"%'";
        Log.i("Search", selectQuery);

        Cursor c = myDataBase.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            team = new Team(
                    c.getString(c.getColumnIndex(Constants.TEAM_ID)),
                    c.getString(c.getColumnIndex(Constants.TEAM_NAME)),
                    c.getString(c.getColumnIndex(Constants.TEAM_CITY)),
                    c.getString(c.getColumnIndex(Constants.TEAM_MODALITY)),
                    c.getString(c.getColumnIndex(Constants.TEAM_SOCIAL_LINK)),
                    c.getString(c.getColumnIndex(Constants.TEAM_PRESIDENTNAME)),
                    c.getString(c.getColumnIndex(Constants.TEAM_EMAIL)),
                    c.getString(c.getColumnIndex(Constants.TEAM_URL_IMAGE)),
                    c.getString(c.getColumnIndex(Constants.TEAM_CODE))
            );

            teams.add(team);
        } else {
            team = null;
        }
        Log.i("search", teams.size()+"");
        c.close();
        this.close();
        return teams;
    }

    public ArrayList<Tests> getTestsFromType(String idType) {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_TESTS +" WHERE "+Constants.TESTS_TYPE+" = '"+idType+"'";
        Cursor c = myDataBase.rawQuery(selectQuery, null);

        ArrayList<Tests> itens = new ArrayList<Tests>();

        if (c.getCount()>0) {
            c.moveToFirst();
            do {
                Tests obj = new Tests(
                        c.getString(c.getColumnIndex(Constants.TESTS_ID)),
                        c.getString(c.getColumnIndex(Constants.TESTS_TYPE)),
                        c.getString(c.getColumnIndex(Constants.TESTS_ATHLETE)),
                        c.getString(c.getColumnIndex(Constants.TESTS_SELECTIVE)),
                        c.getLong(c.getColumnIndex(Constants.TESTS_FIRST_VALUE)),
                        c.getLong(c.getColumnIndex(Constants.TESTS_SECOND_VALUE)),
                        c.getFloat(c.getColumnIndex(Constants.TESTS_RATING)),
                        c.getString(c.getColumnIndex(Constants.TESTS_WINGSPAN)),
                        c.getString(c.getColumnIndex(Constants.TESTS_USER)),
                        c.getInt(c.getColumnIndex(Constants.TESTS_SYNC)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTS_CANSYNC))),
                        c.getString(c.getColumnIndex(Constants.TESTS_VALUES))
                );

                itens.add(obj);
            } while (c.moveToNext());

        } else {
            itens = null;
        }
        c.close();
        this.close();

        return itens;
    }

    public Tests getTestsFromId(String id) {
        this.openDataBase();

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_TESTS +" WHERE "+Constants.TESTS_ID+" = '"+id+"' LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);

        Tests obj = new Tests();

        if (c.moveToFirst()) {
            obj = new Tests(
                    c.getString(c.getColumnIndex(Constants.TESTS_ID)),
                    c.getString(c.getColumnIndex(Constants.TESTS_TYPE)),
                    c.getString(c.getColumnIndex(Constants.TESTS_ATHLETE)),
                    c.getString(c.getColumnIndex(Constants.TESTS_SELECTIVE)),
                    c.getLong(c.getColumnIndex(Constants.TESTS_FIRST_VALUE)),
                    c.getLong(c.getColumnIndex(Constants.TESTS_SECOND_VALUE)),
                    c.getFloat(c.getColumnIndex(Constants.TESTS_RATING)),
                    c.getString(c.getColumnIndex(Constants.TESTS_WINGSPAN)),
                    c.getString(c.getColumnIndex(Constants.TESTS_USER)),
                    c.getInt(c.getColumnIndex(Constants.TESTS_SYNC)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTS_CANSYNC))),
                    c.getString(c.getColumnIndex(Constants.TESTS_VALUES))
            );
        } else {
            obj = null;
        }
        c.close();
        db.close();

        return obj;
    }

    public ArrayList<Athletes> getAthletesByTests(String idTest) {
        this.openDataBase();
        ArrayList<Athletes> itens = new ArrayList<Athletes>();
        try {
            String selectQuery = "SELECT DISTINCT athletes."+Constants.ATHLETES_ID+
                    ", athletes."+Constants.ATHLETES_NAME+
                    ", athletes."+Constants.ATHLETES_BIRTHDAY+
                    ", athletes."+Constants.ATHLETES_CPF+
                    ", athletes."+Constants.ATHLETES_ADDRESS+
                    ", athletes."+Constants.ATHLETES_DESIRABLE_POSITION+
                    ", athletes."+Constants.ATHLETES_HEIGHT+
                    ", athletes."+Constants.ATHLETES_WEIGHT+
                    ", athletes."+Constants.ATHLETES_CREATEDAT+
                    ", athletes."+Constants.ATHLETES_UPDATEAT+
                    ", athletes."+Constants.ATHLETES_EMAIL+
                    ", athletes."+Constants.ATHLETES_PHONE+
                    ", athletes."+Constants.ATHLETES_CODE+
                    ", athletes."+Constants.ATHLETES_SYNC+
                    ", athletes."+Constants.ATHLETES_TERMSACCEPTED+
                    ", athletes."+Constants.ATHLETES_IMAGE_URL+
                    " FROM "
                    + Constants.TABLE_ATHLETES+" as athletes INNER JOIN "
                    +Constants.TABLE_TESTS
                    +" as tests ON athletes."+Constants.TESTS_ID+" = tests."
                    +Constants.TESTS_ATHLETE+" WHERE tests."+Constants.TESTS_TYPE+
                    " ='"+idTest+"' and "+Constants.TESTS_CANSYNC+"=1"
                    ;

            Cursor c = myDataBase.rawQuery(selectQuery, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String code = c.getString(c.getColumnIndex(Constants.ATHLETES_CODE));
                    if(code == null)
                        code = "";
                    Athletes obj = new Athletes(
                            c.getString(c.getColumnIndex(Constants.ATHLETES_ID)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_NAME)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_BIRTHDAY)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_CPF)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_ADDRESS)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_DESIRABLE_POSITION)),
                            c.getDouble(c.getColumnIndex(Constants.ATHLETES_HEIGHT)),
                            c.getDouble(c.getColumnIndex(Constants.ATHLETES_WEIGHT)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_CREATEDAT)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_UPDATEAT)),
                            code,
                            c.getString(c.getColumnIndex(Constants.ATHLETES_EMAIL)),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_PHONE)),
                            Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_SYNC))),
                            Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_TERMSACCEPTED))),
                            c.getString(c.getColumnIndex(Constants.ATHLETES_IMAGE_URL))
                    );
                    itens.add(obj);
                } while (c.moveToNext());

            } else {
                itens = null;
            }
            c.close();
            this.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return itens;
    }

    public ArrayList<TestTypes> getTestsTypes() {
        this.openDataBase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_TESTTYPES;
        Cursor c = myDataBase.rawQuery(selectQuery, null);

        ArrayList<TestTypes> itens = new ArrayList<TestTypes>();

        if (c.getCount()>0) {
            c.moveToFirst();
            do {
                TestTypes obj = new TestTypes(
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_ID)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_NAME)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_ATTEMPTSLIMIT)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTTYPES_VISIBLETOREPORT))),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_DESCRIPTION)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_VALUETYPES)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_ICONIMAGEURL)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_TUTORIALIMAGEURL)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_SIBLING_TEST_TYPE)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTTYPES_REQUIRED_TO_REPORT))),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTTYPES_MAIN_TEST)))
                        );

                itens.add(obj);
            } while (c.moveToNext());

        } else {
            itens = null;
        }
        c.close();
        this.close();

        return itens;
    }

    public ArrayList<User> getUsres() {
        this.openDataBase();

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_USER;
        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<User> itens = new ArrayList<User>();

        if (c.getCount()>0) {
            c.moveToFirst();
            do {
                User obj = new User(
                        c.getString(c.getColumnIndex(Constants.USER_ID)),
                        c.getString(c.getColumnIndex(Constants.USER_NAME)),
                        c.getString(c.getColumnIndex(Constants.USER_EMAIL)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_ISADMIN))),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_CANWRITE))),
                        c.getString(c.getColumnIndex(Constants.USER_TOKEN))
                );

                itens.add(obj);
            } while (c.moveToNext());

        } else {
            itens = null;
        }
        c.close();
        db.close();

        return itens;
    }

    public User getUser() {
        this.openDataBase();

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_USER+" LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);

        User item = new User();

        if (c.moveToFirst()) {
                item = new User(
                        c.getString(c.getColumnIndex(Constants.USER_ID)),
                        c.getString(c.getColumnIndex(Constants.USER_NAME)),
                        c.getString(c.getColumnIndex(Constants.USER_EMAIL)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_ISADMIN))),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_CANWRITE))),
                        c.getString(c.getColumnIndex(Constants.USER_TOKEN))
                );
        } else {
            item = null;
        }
        c.close();
        db.close();

        return item;
    }

    public User getUserByToken(String token) {
        this.openDataBase();

        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_USER+" WHERE "+Constants.USER_TOKEN+"='"+token+"' LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);

        User item = new User();

        if (c.moveToFirst()) {
            item = new User(
                    c.getString(c.getColumnIndex(Constants.USER_ID)),
                    c.getString(c.getColumnIndex(Constants.USER_NAME)),
                    c.getString(c.getColumnIndex(Constants.USER_EMAIL)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_ISADMIN))),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_CANWRITE))),
                    c.getString(c.getColumnIndex(Constants.USER_TOKEN))
            );
        } else {
            item = null;
        }
        c.close();
        db.close();

        return item;
    }

    public Athletes getAthleteById(String idPlayer){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_ATHLETES +
                " WHERE "+Constants.ATHLETES_ID +" ='"+idPlayer+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        Athletes athlete = new Athletes();

        if (c.moveToFirst()) {
            athlete = new Athletes(
                    c.getString(c.getColumnIndex(Constants.ATHLETES_ID)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_NAME)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_BIRTHDAY)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_CPF)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_ADDRESS)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_DESIRABLE_POSITION)),
                    c.getDouble(c.getColumnIndex(Constants.ATHLETES_HEIGHT)),
                    c.getDouble(c.getColumnIndex(Constants.ATHLETES_WEIGHT)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_CREATEDAT)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_UPDATEAT)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_CODE)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_EMAIL)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_PHONE)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_SYNC))),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_TERMSACCEPTED))),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_IMAGE_URL))
            );
        } else {
            athlete = null;
        }
            c.close();
        this.close();
        return athlete;
    }

    public Athletes getAthleteByValue(String data, String value){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_ATHLETES +
                " WHERE "+data +" ='"+value+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        Athletes athlete = new Athletes();

        if (c.moveToFirst()) {
            athlete = new Athletes(
                    c.getString(c.getColumnIndex(Constants.ATHLETES_ID)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_NAME)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_BIRTHDAY)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_CPF)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_ADDRESS)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_DESIRABLE_POSITION)),
                    c.getDouble(c.getColumnIndex(Constants.ATHLETES_HEIGHT)),
                    c.getDouble(c.getColumnIndex(Constants.ATHLETES_WEIGHT)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_CREATEDAT)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_UPDATEAT)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_CODE)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_EMAIL)),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_PHONE)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_SYNC))),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_TERMSACCEPTED))),
                    c.getString(c.getColumnIndex(Constants.ATHLETES_IMAGE_URL))
            );
        } else {
            athlete = null;
        }
        c.close();
        this.close();
        return athlete;
    }

    public boolean existAthleteByCPF(String cpf){
        this.openDataBase();
        boolean ret = false;
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_ATHLETES +
                " WHERE "+Constants.ATHLETES_CPF +" ='"+cpf+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);


        if (c.moveToFirst()) {
            ret = true;
        } else {
            ret = false;
        }
        c.close();
        this.close();
        return ret;
    }

    public TestTypes getTestTypeFromId(String id){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_TESTTYPES +
                " WHERE "+Constants.TESTS_ID +" ='"+id+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        TestTypes test;
        try {
            if (c.moveToFirst()) {
                test = new TestTypes(
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_ID)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_NAME)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_ATTEMPTSLIMIT)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTTYPES_VISIBLETOREPORT))),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_DESCRIPTION)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_VALUETYPES)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_ICONIMAGEURL)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_TUTORIALIMAGEURL)),
                        c.getString(c.getColumnIndex(Constants.TESTTYPES_SIBLING_TEST_TYPE)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTTYPES_REQUIRED_TO_REPORT))),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTTYPES_MAIN_TEST)))
                );
            } else {
                test = null;
            }
        }catch (Exception e){
            test = null;
        }
        c.close();
        this.close();
        return test;
    }

    public Tests getTestFromAthleteAndType(String athlete, String type){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_TESTS +
                " WHERE "+Constants.TESTS_ATHLETE +" ='"+athlete+"' AND "+Constants.TESTS_TYPE+" ='"+type+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        Tests test;
        try {
            if (c.moveToFirst()) {
                test = new Tests(
                        c.getString(c.getColumnIndex(Constants.TESTS_ID)),
                        c.getString(c.getColumnIndex(Constants.TESTS_TYPE)),
                        c.getString(c.getColumnIndex(Constants.TESTS_ATHLETE)),
                        c.getString(c.getColumnIndex(Constants.TESTS_SELECTIVE)),
                        c.getLong(c.getColumnIndex(Constants.TESTS_FIRST_VALUE)),
                        c.getLong(c.getColumnIndex(Constants.TESTS_SECOND_VALUE)),
                        c.getFloat(c.getColumnIndex(Constants.TESTS_RATING)),
                        c.getString(c.getColumnIndex(Constants.TESTS_WINGSPAN)),
                        c.getString(c.getColumnIndex(Constants.TESTS_USER)),
                        c.getInt(c.getColumnIndex(Constants.TESTS_SYNC)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTS_CANSYNC))),
                        c.getString(c.getColumnIndex(Constants.TESTS_VALUES))
                );
            } else {
                test = null;
            }
        }catch (Exception e){
            test = null;
        }
        c.close();
        this.close();
        return test;
    }

    public ArrayList<Tests> getTestsFromAthleteAndType(String athlete, String type){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_TESTS +
                " WHERE "+Constants.TESTS_ATHLETE +" ='"+athlete+"' AND "+Constants.TESTS_TYPE+" ='"+type+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);


        ArrayList<Tests> tests = new ArrayList<Tests>();

        try {
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    Tests test = new Tests(
                            c.getString(c.getColumnIndex(Constants.TESTS_ID)),
                            c.getString(c.getColumnIndex(Constants.TESTS_TYPE)),
                            c.getString(c.getColumnIndex(Constants.TESTS_ATHLETE)),
                            c.getString(c.getColumnIndex(Constants.TESTS_SELECTIVE)),
                            c.getLong(c.getColumnIndex(Constants.TESTS_FIRST_VALUE)),
                            c.getLong(c.getColumnIndex(Constants.TESTS_SECOND_VALUE)),
                            c.getFloat(c.getColumnIndex(Constants.TESTS_RATING)),
                            c.getString(c.getColumnIndex(Constants.TESTS_WINGSPAN)),
                            c.getString(c.getColumnIndex(Constants.TESTS_USER)),
                            c.getInt(c.getColumnIndex(Constants.TESTS_SYNC)),
                            Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.TESTS_CANSYNC))),
                            c.getString(c.getColumnIndex(Constants.TESTS_VALUES))
                    );
                    tests.add(test);
                } while (c.moveToNext());
            }
            else
                tests=null;
        }catch (Exception e){
            tests = null;
        }
        return tests;
    }

    public Positions getPositiomById(String id){
        this.openDataBase();
        String selectQuery = "SELECT DISTINCT * FROM "+ Constants.TABLE_POSITIONS +
                " WHERE "+Constants.POSITIONS_ID +" ='"+id+"'";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        Positions position = new Positions();

        if (c.moveToFirst()) {
            position = new Positions(
                    c.getString(c.getColumnIndex(Constants.POSITIONS_ID)),
                    c.getString(c.getColumnIndex(Constants.POSITIONS_NAME)),
                    c.getString(c.getColumnIndex(Constants.POSITIONS_DESCRIPTIONS))
            );
        } else {
            position = null;
        }
        c.close();
        this.close();
        return position;
    }

    public User checkExistsUser(String userName, String password) {
        String selectGetUser = "SELECT DISTINCT * FROM [" + Constants.TABLE_USER + "] WHERE Username = '" + userName +
                "'" + " AND password ='" + password + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectGetUser, null);
        User user = null;
        if(c.moveToFirst()){
            User obj = new User(
                    c.getString(c.getColumnIndex(Constants.USER_ID)),
                    c.getString(c.getColumnIndex(Constants.USER_NAME)),
                    c.getString(c.getColumnIndex(Constants.USER_EMAIL)),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_ISADMIN))),
                    Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.USER_CANWRITE))),
                    c.getString(c.getColumnIndex(Constants.USER_TOKEN))
            );
        }
        return user;
    }

    public boolean updateSync(String athlete, String type, String id) {
        boolean ret = false;
        this.openDataBase();
        try {
            String selectQuery = "UPDATE " + Constants.TABLE_TESTS + " SET " + Constants.TESTS_SYNC + "=" +
                    Services.convertBoolInInt(true) + ", "+Constants.TESTS_ID+"='"+id+"' WHERE "
                    + Constants.TESTS_ATHLETE + "='" + athlete + "' AND "+Constants.TESTS_TYPE+"='"+type+"'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }

    public boolean updateSync(long second, float rating, String id) {
        boolean ret = false;
        this.openDataBase();
        try {
            String selectQuery = "UPDATE " + Constants.TABLE_TESTS + " SET "
                    + Constants.TESTS_CANSYNC + "=" + Services.convertBoolInInt(true) +
                    ", "+Constants.TESTS_SECOND_VALUE+"="+second+
                    ", "+Constants.TESTS_RATING+"="+rating+" WHERE "
                    + Constants.TESTS_ID + "='" + id + "'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }

    public void updateTests(ArrayList<Tests> tests) {
        boolean ret = false;
        for (Tests test : tests){
            this.openDataBase();
            Tests test1 = getTestsFromId(test.getId());
            if(test1==null)
                addTest(test);
            else
                updateTest(test);
            this.close();
        }
    }

    public boolean updateSelectiveAthlete(SelectiveAthletes obj) {
        boolean ret = false;
        this.openDataBase();
        try {

            String selectQuery = "UPDATE " +
                    Constants.TABLE_SELECTIVEATHLETES + " SET " +
                    Constants.SELECTIVEATHLETES_ID + "='" +obj.getId() +
                    "', "+Constants.SELECTIVEATHLETES_ATHLETE+"='"+obj.getAthlete()+
                    "'  WHERE "
                    + Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER + "='" + obj.getInscriptionNumber() + "'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }

    public boolean updateAthlete(Athletes athlete) {
        boolean ret = false;
        this.openDataBase();
        try {

            String selectQuery = "UPDATE " + Constants.TABLE_ATHLETES + " SET "
                    + Constants.ATHLETES_CODE + "='" +athlete.getCode() + "', "
                    + Constants.ATHLETES_NAME + "='" +athlete.getName() + "', "
                    + Constants.ATHLETES_CPF + "='" +athlete.getCPF() + "', "
                    + Constants.ATHLETES_ADDRESS + "='" +athlete.getAddress() + "', "
                    + Constants.ATHLETES_BIRTHDAY + "='" +athlete.getBirthday() + "', "
                    + Constants.ATHLETES_CREATEDAT + "='" +athlete.getCreatedAt() + "', "
                    + Constants.ATHLETES_UPDATEAT + "='" +athlete.getUpdateAt() + "', "
                    + Constants.ATHLETES_DESIRABLE_POSITION + "='" +athlete.getDesirablePosition() + "', "
                    + Constants.ATHLETES_EMAIL + "='" +athlete.getEmail() + "', "
                    + Constants.ATHLETES_PHONE + "='" +athlete.getPhoneNumber() + "', "
                    + Constants.ATHLETES_SYNC + "=" +Services.convertBoolInInt(athlete.getSync()) + ", "
                    + Constants.ATHLETES_WEIGHT+ "=" +athlete.getWeight() + ", "
                    + Constants.ATHLETES_IMAGE_URL+ "='" +athlete.getURLImage() + "', "
                    + Constants.ATHLETES_HEIGHT+ "=" +athlete.getHeight() +
                    "  WHERE "+ Constants.ATHLETES_ID + "='" + athlete.getId() + "'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }

    public boolean updateTestsAthlete(String idOlder, String newId) {
        boolean ret = false;
        this.openDataBase();
        try {

            String selectQuery = "UPDATE " + Constants.TABLE_TESTS + " SET "
                    + Constants.TESTS_ATHLETE + "='" +newId + "'  WHERE "+ Constants.TESTS_ATHLETE + "='" + idOlder+ "'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }

    public boolean updateTest(Tests test) {
        boolean ret = false;
        this.openDataBase();
        try {
            String selectQuery = "UPDATE " + Constants.TABLE_TESTS + " SET "
                    + Constants.TESTS_SYNC + "= 1, " +
                     Constants.TESTS_CANSYNC + "= 1, " +
                     Constants.TESTS_FIRST_VALUE + "= "+test.getFirstValue()+", "+
                     Constants.TESTS_SECOND_VALUE + "= "+test.getSecondValue()+", "+
                     Constants.TESTS_RATING + "= "+test.getRating()+", "+
                     Constants.TESTS_WINGSPAN + "= '"+test.getWingspan()+"', '"+
                     Constants.TESTS_SELECTIVE + "'= '"+test.getSelective()+"', '"+
                     Constants.TESTS_VALUES + "'= '"+test.getValues()+"'"+
                    " WHERE "+ Constants.TESTS_ATHLETE + "='" + test.getAthlete()+ "'"+
                    " AND "+ Constants.TESTS_TYPE +" = '"+test.getType()+"'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }

    public boolean updateTeam(Team team) {
        boolean ret = false;
        this.openDataBase();
        try {
            String selectQuery = "UPDATE " + Constants.TABLE_TEAM + " SET "
                    + Constants.TEAM_NAME + "= " +team.getName()+", "+
                    Constants.TEAM_CITY + "= " +team.getCity()+", "+
                    Constants.TEAM_MODALITY + "= "+team.getModality()+", "+
                    Constants.TEAM_ADDRESS + "= "+team.getAddres()+", "+
                    Constants.TEAM_PRESIDENTNAME + "= "+team.getPresidentName()+", "+
                    Constants.TEAM_EMAIL + "= '"+team.getEmail()+"', '"+
                    Constants.TEAM_SOCIAL_LINK + "'= '"+team.getSocialLink()+"', '"+
                    Constants.TEAM_URL_IMAGE + "'= '"+team.getUrlImage()+"'"+
                    Constants.TEAM_USER + "'= '"+team.getUser()+"'"+
                    " WHERE "+ Constants.TEAM_ID + "='" + team.getId()+ "'";

            Cursor c = myDataBase.rawQuery(selectQuery, null);
            c.getCount();

        } catch (Exception e) {
            Log.i("ERROR UPDATE", e.getMessage());
        }
        return ret;
    }


    /******************************** SEARCHS************************************************/

    public ArrayList<Athletes> searchAthletes(String search){
        this.openDataBase();
        String selectQuery =  "SELECT DISTINCT * FROM "+Constants.TABLE_ATHLETES+" WHERE " +
                "("+Constants.ATHLETES_NAME+" || \"\" || "+Constants.ATHLETES_CODE+") LIKE '%"+search+"%' GROUP BY "
                +Constants.ATHLETES_NAME+", "+Constants.ATHLETES_CODE+"";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        ArrayList<Athletes> athletes = new ArrayList<Athletes>();


        if(c.getCount()>0){
            c.moveToFirst();
            do{
                Athletes athlete = new Athletes(
                        c.getString(c.getColumnIndex(Constants.ATHLETES_ID)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_NAME)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_BIRTHDAY)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_CPF)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_ADDRESS)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_DESIRABLE_POSITION)),
                        c.getDouble(c.getColumnIndex(Constants.ATHLETES_HEIGHT)),
                        c.getDouble(c.getColumnIndex(Constants.ATHLETES_WEIGHT)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_CREATEDAT)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_UPDATEAT)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_CODE)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_EMAIL)),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_PHONE)),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_SYNC))),
                        Services.convertIntInBool(c.getInt(c.getColumnIndex(Constants.ATHLETES_TERMSACCEPTED))),
                        c.getString(c.getColumnIndex(Constants.ATHLETES_IMAGE_URL))
                );
                athletes.add(athlete);
            }while(c.moveToNext());
        }
        return athletes;

    }

    public ArrayList<Selective> searchSelective(String search){
        this.openDataBase();
        String selectQuery =  "SELECT DISTINCT * FROM "+Constants.TABLE_SELECTIVES+" WHERE " +
                "("+Constants.SELECTIVES_TITLE+" || \"\" || "+Constants.SELECTIVES_TEAM+") LIKE '%"+search+"%' GROUP BY "
                +Constants.SELECTIVES_TITLE+", "+Constants.SELECTIVES_TEAM+"";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        ArrayList<Selective> selectives = new ArrayList<Selective>();


        if(c.getCount()>0){
            c.moveToFirst();
            do{
                Selective selective = new Selective();
                selective.setId(c.getString(c.getColumnIndex(Constants.SELECTIVES_ID)));
                selective.setDate(c.getString(c.getColumnIndex(Constants.SELECTIVES_DATE)));
                selective.setTeam(c.getString(c.getColumnIndex(Constants.SELECTIVES_TEAM)));
                selective.setCodeSelective(c.getString(c.getColumnIndex(Constants.SELECTIVES_CODESELECTIVE)));
                selective.setTitle(c.getString(c.getColumnIndex(Constants.SELECTIVES_TITLE)));
                selectives.add(selective);
            }while(c.moveToNext());
        }
        return selectives;

    }

    public ArrayList<SelectiveUsers> searchSelectiveUser(String search){
        this.openDataBase();
        String selectQuery =  "SELECT DISTINCT * FROM "+Constants.TABLE_SELECTIVES+" WHERE " +
                "("+Constants.SELECTIVES_TITLE+" || \"\" || "+Constants.SELECTIVES_TEAM+") LIKE '%"+search+"%' GROUP BY "
                +Constants.SELECTIVES_TITLE+", "+Constants.SELECTIVES_TEAM+"";

        Cursor c = myDataBase.rawQuery(selectQuery, null);

        ArrayList<SelectiveUsers> selectives = new ArrayList<SelectiveUsers>();


        if(c.getCount()>0){
            c.moveToFirst();
            do{
                SelectiveUsers selective = new SelectiveUsers();
                selective.setId(c.getString(c.getColumnIndex(Constants.SELECTIVES_ID)));
                selective.setDate(c.getString(c.getColumnIndex(Constants.SELECTIVES_DATE)));
                selective.setTeam(c.getString(c.getColumnIndex(Constants.SELECTIVES_TEAM)));
                selective.setCodeSelective(c.getString(c.getColumnIndex(Constants.SELECTIVES_CODESELECTIVE)));
                selective.setTitle(c.getString(c.getColumnIndex(Constants.SELECTIVES_TITLE)));
                selectives.add(selective);
            }while(c.moveToNext());
        }
        return selectives;

    }
}