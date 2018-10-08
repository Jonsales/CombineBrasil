package br.com.john.combinebrasil.Connection.JSONServices;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.john.combinebrasil.AdapterList.ExpandableRecycler.ChildItemTests;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.Classes.CEP;
import br.com.john.combinebrasil.Classes.Positions;
import br.com.john.combinebrasil.Classes.ResultTest;
import br.com.john.combinebrasil.Classes.ResultTestsAthletes;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.SelectiveAthletes;
import br.com.john.combinebrasil.Classes.SelectiveUsers;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.Classes.TeamUsers;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.Classes.User;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

/**
 * Created by GTAC on 19/10/2016.
 */


public class DeserializerJsonElements {
    private String response;
    private Activity act;

    public DeserializerJsonElements() {
    }

    public DeserializerJsonElements(String response) {
        this.response = response;
    }

    public User getLogin() {
        User login = new User();
        try {
            JSONObject json = new JSONObject(this.response);
            login.setId(json.optString(Constants.USER_ID));
            login.setName(json.optString(Constants.USER_NAME));
            login.setEmail(json.optString(Constants.USER_EMAIL));
            login.setIsAdmin(json.optBoolean(Constants.USER_ISADMIN));
            login.setCanWrite(json.optBoolean(Constants.USER_CANWRITE));
            login.setToken(json.optString(Constants.USER_TOKEN));
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return login;
    }

    /*
    ***********************************DESERIALIZER USER********************************************
    **/
    public User getUsers() {
        User user = null;
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0;i<=jsonArray.length()-1;i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));

                user = new User(
                        json.optString(Constants.USER_ID),
                        json.optString(Constants.USER_NAME),
                        json.optString(Constants.USER_EMAIL),
                        json.optBoolean(Constants.USER_ISADMIN),
                        json.optBoolean(Constants.USER_CANWRITE),
                        json.optString(Constants.USER_TOKEN)
                );
            }
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return user;
    }

    public User getObjectsUser() {
        User user = null;
        try {
            JSONObject json = new JSONObject(this.response);
            user = new User(
                    json.optString(Constants.USER_ID),
                    json.optString(Constants.USER_NAME),
                    json.optString(Constants.USER_EMAIL),
                    json.optBoolean(Constants.USER_ISADMIN),
                    json.optBoolean(Constants.USER_CANWRITE),
                    json.optString(Constants.USER_TOKEN)
            );
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return user;
    }


    /*
    ************************************DESERIALIZER ROUTEACTIVITYDATAS*****************************
    */


    /********************************************************
     * ATHLETES
     ******************************/

    public ArrayList<Athletes> getAthletes() {
        ArrayList<Athletes> AthletesList = new ArrayList<Athletes>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));
                Athletes athletesEntity = new Athletes(
                        json.optString(Constants.ATHLETES_ID),
                        json.optString(Constants.ATHLETES_NAME),
                        json.optString(Constants.ATHLETES_BIRTHDAY),
                        json.optString(Constants.ATHLETES_CPF),
                        json.optString(Constants.ATHLETES_ADDRESS),
                        json.optString(Constants.ATHLETES_DESIRABLE_POSITION),
                        json.optDouble(Constants.ATHLETES_HEIGHT),
                        json.optDouble(Constants.ATHLETES_WEIGHT),
                        json.optString(Constants.ATHLETES_CREATEDAT),
                        json.optString(Constants.ATHLETES_UPDATEAT),
                        "",
                        json.optString(Constants.ATHLETES_EMAIL),
                        json.optString(Constants.ATHLETES_PHONE),
                        true,
                        json.optBoolean(Constants.ATHLETES_TERMSACCEPTED, true),
                        json.optString(Constants.ATHLETES_IMAGE_URL)
                );
                AthletesList.add(athletesEntity);
            }

        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return AthletesList;
    }

    public Athletes getAthlete() {
        Athletes athlete = new Athletes();
        try {

            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));
                athlete = new Athletes(
                        json.optString(Constants.ATHLETES_ID),
                        json.optString(Constants.ATHLETES_NAME),
                        json.optString(Constants.ATHLETES_BIRTHDAY),
                        json.optString(Constants.ATHLETES_CPF),
                        json.optString(Constants.ATHLETES_ADDRESS),
                        json.optString(Constants.ATHLETES_DESIRABLE_POSITION),
                        json.optDouble(Constants.ATHLETES_HEIGHT),
                        json.optDouble(Constants.ATHLETES_WEIGHT),
                        json.optString(Constants.ATHLETES_CREATEDAT),
                        json.optString(Constants.ATHLETES_UPDATEAT),
                        "",
                        json.optString(Constants.ATHLETES_EMAIL),
                        json.optString(Constants.ATHLETES_PHONE),
                        true,
                        json.optBoolean(Constants.ATHLETES_TERMSACCEPTED, true),
                        json.optString(Constants.ATHLETES_IMAGE_URL)
                );
            }
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
            athlete = null;
        }
        return athlete;
    }

    public Athletes getObjAthlete() {
        Athletes athlete = new Athletes();
        try {

            JSONObject json = new JSONObject(response);

                athlete = new Athletes(
                        json.optString(Constants.ATHLETES_ID),
                        json.optString(Constants.ATHLETES_NAME),
                        json.optString(Constants.ATHLETES_BIRTHDAY),
                        json.optString(Constants.ATHLETES_CPF),
                        json.optString(Constants.ATHLETES_ADDRESS),
                        json.optString(Constants.ATHLETES_DESIRABLE_POSITION),
                        json.optDouble(Constants.ATHLETES_HEIGHT),
                        json.optDouble(Constants.ATHLETES_WEIGHT),
                        json.optString(Constants.ATHLETES_CREATEDAT),
                        json.optString(Constants.ATHLETES_UPDATEAT),
                        json.optString(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER),
                        json.optString(Constants.ATHLETES_EMAIL),
                        json.optString(Constants.ATHLETES_PHONE),
                        true,
                        json.optBoolean(Constants.ATHLETES_TERMSACCEPTED, true),
                        json.optString(Constants.ATHLETES_IMAGE_URL)
                );
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
            athlete = null;
        }
        return athlete;
    }

    public ArrayList<Athletes> getAthletesInSelective() {
        ArrayList<Athletes> AthletesList = new ArrayList<Athletes>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));
                try {
                    json = json.getJSONObject(Constants.SELECTIVEATHLETES_ATHLETE);
                    Athletes athletesEntity = new Athletes(
                            json.optString(Constants.ATHLETES_ID),
                            json.optString(Constants.ATHLETES_NAME),
                            json.optString(Constants.ATHLETES_BIRTHDAY),
                            json.optString(Constants.ATHLETES_CPF),
                            json.optString(Constants.ATHLETES_ADDRESS),
                            json.optString(Constants.ATHLETES_DESIRABLE_POSITION),
                            json.optDouble(Constants.ATHLETES_HEIGHT),
                            json.optDouble(Constants.ATHLETES_WEIGHT),
                            json.optString(Constants.ATHLETES_CREATEDAT),
                            json.optString(Constants.ATHLETES_UPDATEAT),
                            "",
                            json.optString(Constants.ATHLETES_EMAIL),
                            json.optString(Constants.ATHLETES_PHONE),
                            true,
                            json.optBoolean(Constants.ATHLETES_TERMSACCEPTED, true),
                            json.optString(Constants.ATHLETES_IMAGE_URL)
                    );
                    AthletesList.add(athletesEntity);
                }catch (JSONException jsonExc){
                    Log.i("JSON ERROR", jsonExc.toString());
                }
            }

        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return AthletesList;
    }
    /***************************************
     * POSITIONS
     ********************************************/
    public ArrayList<Positions> getPositions() {
        ArrayList<Positions> positions = new ArrayList<Positions>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    Positions obj = new Positions(json.optString(Constants.POSITIONS_ID),
                            json.optString(Constants.POSITIONS_NAME),
                            json.optString(Constants.POSITIONS_DESCRIPTIONS));

                    positions.add(obj);
                }
            }
        } catch (JSONException e) {
            positions = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return positions;
    }

    public Positions getPosicao() {
        Positions posicao = null;
        try {
            JSONObject json = new JSONObject(this.response);
            posicao = new Positions(
                    json.optString(Constants.POSITIONS_ID),
                    json.optString(Constants.POSITIONS_NAME),
                    json.optString(Constants.POSITIONS_DESCRIPTIONS)
            );
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return posicao;
    }

    /***************************************
     * SELECTIVE ATHLETES
     ********************************************/
    public ArrayList<SelectiveAthletes> getSelectiveAthletes() {
        ArrayList<SelectiveAthletes> selectiveAthletes = new ArrayList<SelectiveAthletes>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    SelectiveAthletes obj = new SelectiveAthletes(
                            json.optString(Constants.SELECTIVEATHLETES_ID),
                            json.optString(Constants.SELECTIVEATHLETES_ATHLETE),
                            json.optString(Constants.SELECTIVEATHLETES_SELECTIVE),
                            json.optString(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER),
                            json.optBoolean(Constants.SELECTIVEATHLETES_PRESENCE)
                    );

                    selectiveAthletes.add(obj);
                }
            }
        } catch (JSONException e) {
            selectiveAthletes = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return selectiveAthletes;
    }

    public SelectiveAthletes getSelectiveAthlete() {
        SelectiveAthletes selectiveAthletes = new SelectiveAthletes();
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0; i<=jsonArray.length()-1; i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));
                selectiveAthletes = new SelectiveAthletes(
                        json.optString(Constants.SELECTIVEATHLETES_ID),
                        json.optString(Constants.SELECTIVEATHLETES_ATHLETE),
                        json.optString(Constants.SELECTIVEATHLETES_SELECTIVE),
                        json.optString(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER),
                        json.optBoolean(Constants.SELECTIVEATHLETES_PRESENCE)
                );
            }
        } catch (JSONException e) {
            selectiveAthletes = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return selectiveAthletes;

    }

    public SelectiveAthletes getObjSelectiveAthlete() {
        SelectiveAthletes selectiveAthletes = new SelectiveAthletes();
        try {
            JSONObject json = new JSONObject(response);

                selectiveAthletes = new SelectiveAthletes(
                        json.optString(Constants.SELECTIVEATHLETES_ID),
                        json.optString(Constants.SELECTIVEATHLETES_ATHLETE),
                        json.optString(Constants.SELECTIVEATHLETES_SELECTIVE),
                        json.optString(Constants.SELECTIVEATHLETES_INSCRIPTIONNUMBER),
                        json.optBoolean(Constants.SELECTIVEATHLETES_PRESENCE)
                );

        } catch (JSONException e) {
            selectiveAthletes = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return selectiveAthletes;

    }

    /***************************************
     * SELECTIVE
     ********************************************/
    public ArrayList<Selective> getSelectives() {
        ArrayList<Selective> selectives = new ArrayList<Selective>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    Selective obj = new Selective(
                            json.optString(Constants.SELECTIVES_ID),
                            json.optString(Constants.SELECTIVES_TITLE),
                            json.optString(Constants.SELECTIVES_TEAM),
                            json.optString(Constants.USER_SELECTIVE_USER),
                            json.optString(Constants.SELECTIVES_DATE),
                            json.optString(Constants.SELECTIVES_CODESELECTIVE),
                            json.optBoolean(Constants.SELECTIVES_CANSYNC, true),
                            json.optString(Constants.SELECTIVES_CITY),
                            json.optString(Constants.SELECTIVES_NEIGHBORHOOD),
                            json.optString(Constants.SELECTIVES_STATE),
                            json.optString(Constants.SELECTIVES_STREET),
                            json.optString(Constants.SELECTIVES_POSTALCODE),
                            json.optString(Constants.SELECTIVES_NOTE),
                            json.optString(Constants.SELECTIVES_ADDRESS),
                            json.optBoolean(Constants.SELECTIVES_PAY, false),
                            json.optString(Constants.SELECTIVES_PRICE, "")
                    );

                    selectives.add(obj);
                }
            }
        } catch (JSONException e) {
            selectives = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return selectives;
    }

    public Selective getSelective() {
        Selective selective = new Selective();
        try {
            JSONObject json = new JSONObject(response);
            selective = new Selective(
                    json.optString(Constants.SELECTIVES_ID),
                    json.optString(Constants.SELECTIVES_TITLE),
                    json.optString(Constants.SELECTIVES_TEAM),
                    json.optString(Constants.USER_SELECTIVE_USER),
                    json.optString(Constants.SELECTIVES_DATE),
                    json.optString(Constants.SELECTIVES_CODESELECTIVE),
                    json.optBoolean(Constants.SELECTIVES_CANSYNC, true),
                    json.optString(Constants.SELECTIVES_CITY),
                    json.optString(Constants.SELECTIVES_NEIGHBORHOOD),
                    json.optString(Constants.SELECTIVES_STATE),
                    json.optString(Constants.SELECTIVES_STREET),
                    json.optString(Constants.SELECTIVES_POSTALCODE),
                    json.optString(Constants.SELECTIVES_NOTE),
                    json.optString(Constants.SELECTIVES_ADDRESS),
                    json.optBoolean(Constants.SELECTIVES_PAY, false),
                    json.optString(Constants.SELECTIVES_PRICE, "")
            );
        } catch (JSONException e) {
            selective = null;
            Log.i("ERROR: getSelective", e.getMessage());
        }
        return selective;
    }

    public Selective getOlnySelective() {
        Selective selective = new Selective();
        try {
            JSONArray jsonArray = new JSONArray(response);

            JSONObject json = new JSONObject(jsonArray.getString(0));
            selective = new Selective(
                json.optString(Constants.SELECTIVES_ID),
                json.optString(Constants.SELECTIVES_TITLE),
                json.optString(Constants.SELECTIVES_TEAM),
                json.optString(Constants.USER_SELECTIVE_USER),
                json.optString(Constants.SELECTIVES_DATE),
                json.optString(Constants.SELECTIVES_CODESELECTIVE),
                json.optBoolean(Constants.SELECTIVES_CANSYNC, true),
                json.optString(Constants.SELECTIVES_CITY),
                json.optString(Constants.SELECTIVES_NEIGHBORHOOD),
                json.optString(Constants.SELECTIVES_STATE),
                json.optString(Constants.SELECTIVES_STREET),
                json.optString(Constants.SELECTIVES_POSTALCODE),
                json.optString(Constants.SELECTIVES_NOTE),
                json.optString(Constants.SELECTIVES_ADDRESS),
                json.optBoolean(Constants.SELECTIVES_PAY, false),
                json.optString(Constants.SELECTIVES_PRICE, "")
        );
        } catch (JSONException e) {
            selective = null;
            Log.i("ERROR: getOlnySelective", e.getMessage());
        }
        return selective;
    }
    /***************************************
     * TEAMUSERS
     ********************************************/
    public ArrayList<TeamUsers> getTeamUsers() {
        ArrayList<TeamUsers> teamUserses = new ArrayList<TeamUsers>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    TeamUsers obj = new TeamUsers(
                            json.optString(Constants.TEAMUSERS_ID),
                            json.optString(Constants.TEAMUSERS_USER),
                            json.optString(Constants.TEAMUSERS_TEAM)
                    );

                    teamUserses.add(obj);
                }
            }
        } catch (JSONException e) {
            teamUserses = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return teamUserses;
    }

    public TeamUsers getTeamUser() {
        TeamUsers team = new TeamUsers();
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i=0; i<=jsonArray.length()-1; i++){
                JSONObject json = new JSONObject(jsonArray.getString(i));
                team = new TeamUsers(
                        json.optString(Constants.TEAMUSERS_ID),
                        json.optString(Constants.TEAMUSERS_USER),
                        json.optString(Constants.TEAMUSERS_TEAM)
                );
            }
        } catch (JSONException e) {
            team = null;
            Log.i("ERROR: getTeamUser", e.getMessage());
        }
        return team;
    }

    /***************************************
     * TEAM
     ********************************************/
    public ArrayList<Team> getTeam() {
        ArrayList<Team> teams = new ArrayList<Team>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    Team obj = new Team(
                            json.optString(Constants.TEAM_ID, ""),
                            json.optString(Constants.TEAM_NAME, ""),
                            json.optString(Constants.TEAM_CITY, ""),
                            json.optString(Constants.TEAM_MODALITY, ""),
                            json.optString(Constants.TEAM_SOCIAL_LINK, ""),
                            json.optString(Constants.TEAM_PRESIDENTNAME, ""),
                            json.optString(Constants.TEAM_EMAIL, ""),
                            json.optString(Constants.TEAM_URL_IMAGE, ""),
                            json.optString(Constants.TEAM_CODE, "")
                    );
                    obj.setAddres(json.optString(Constants.TEAM_ADDRESS, ""));
                    obj.setUser(json.optString(Constants.TEAM_USER, ""));

                    teams.add(obj);
                }
            }
        } catch (JSONException e) {
            teams = null;
            Log.i("ERROR: getTeam", e.getMessage());
        }
        return teams;
    }

    public Team getObjectTeam() {
        Team team = new Team();
        try {
            JSONObject json = new JSONObject(response);

            team = new Team(
                    json.optString(Constants.TEAM_ID, ""),
                    json.optString(Constants.TEAM_NAME, ""),
                    json.optString(Constants.TEAM_CITY, ""),
                    json.optString(Constants.TEAM_MODALITY, ""),
                    json.optString(Constants.TEAM_SOCIAL_LINK, ""),
                    json.optString(Constants.TEAM_PRESIDENTNAME, ""),
                    json.optString(Constants.TEAM_EMAIL, ""),
                    json.optString(Constants.TEAM_URL_IMAGE, ""),
                    json.optString(Constants.TEAM_CODE, "")
            );
            team.setUser(json.optString(Constants.TEAM_USER, ""));
        } catch (JSONException e) {
            team = null;
            Log.i("ERROR: getObjectTeam", e.getMessage());
        }
        return team;
    }
    /***************************************
     * TESTTYPES
     ********************************************/
    public ArrayList<TestTypes> getTestTypes() {
        ArrayList<TestTypes> teams = new ArrayList<TestTypes>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    TestTypes obj = new TestTypes(
                            json.optString(Constants.TESTTYPES_ID),
                            json.optString(Constants.TESTTYPES_NAME, ""),
                            json.optString(Constants.TESTTYPES_ATTEMPTSLIMIT, ""),
                            json.optBoolean(Constants.TESTTYPES_VISIBLETOREPORT, false),
                            json.optString(Constants.TESTTYPES_DESCRIPTION, ""),
                            json.optString(Constants.TESTTYPES_VALUETYPES, ""),
                            json.optString(Constants.TESTTYPES_ICONIMAGEURL, ""),
                            json.optString(Constants.TESTTYPES_TUTORIALIMAGEURL, ""),
                            json.optString(Constants.TESTTYPES_SIBLING_TEST_TYPE, ""),
                            json.optBoolean(Constants.TESTTYPES_REQUIRED_TO_REPORT, false),
                            json.optBoolean(Constants.TESTTYPES_MAIN_TEST, false)
                    );

                    teams.add(obj);
                }
            }
        } catch (JSONException e) {
            teams = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return teams;
    }

    /***************************************
     * TESTS
     ********************************************/
    public ArrayList<Tests> getTest() {
        ArrayList<Tests> testses = new ArrayList<Tests>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                    Tests obj = new Tests(
                            json.optString(Constants.TESTS_ID),
                            json.optString(Constants.TESTS_TYPE),
                            json.optString(Constants.TESTS_ATHLETE),
                            json.optString(Constants.TESTS_SELECTIVE),
                            json.optLong(Constants.TESTS_FIRST_VALUE),
                            json.optLong(Constants.TESTS_SECOND_VALUE),
                            (float) (json.optDouble(Constants.TESTS_RATING)),
                            json.optString(Constants.TESTS_WINGSPAN),
                            json.optString(Constants.TESTS_USER),
                            Services.convertBoolInInt(true),
                            true,
                            returnTest(json.optJSONArray(Constants.TESTS_VALUES))
                    );

                    testses.add(obj);
                }
            }
        } catch (JSONException e) {
            testses = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return testses;
    }

    private String returnTest(JSONArray jsonValues) throws JSONException {
        String values = "";
        for(int i=0; i<=jsonValues.length()-1; i++){
            values = values +jsonValues.getJSONArray(i).optLong(0)+"/";
        }

        return values;
    }

    public ArrayList<TestTypes> getSelectiveTestType() {
        ArrayList<TestTypes> testses = new ArrayList<TestTypes>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    try {
                        JSONObject json = new JSONObject(jsonArray.getString(i));
                        json = new JSONObject(json.getString("testType"));

                        TestTypes obj = new TestTypes(
                                json.optString(Constants.TESTTYPES_ID),
                                json.optString(Constants.TESTTYPES_NAME),
                                json.optString(Constants.TESTTYPES_ATTEMPTSLIMIT),
                                json.optBoolean(Constants.TESTTYPES_VISIBLETOREPORT),
                                json.optString(Constants.TESTTYPES_DESCRIPTION),
                                json.optString(Constants.TESTTYPES_VALUETYPES),
                                json.optString(Constants.TESTTYPES_ICONIMAGEURL),
                                json.optString(Constants.TESTTYPES_TUTORIALIMAGEURL),
                                json.optString(Constants.TESTTYPES_SIBLING_TEST_TYPE),
                                json.optBoolean(Constants.TESTTYPES_REQUIRED_TO_REPORT),
                                json.optBoolean(Constants.TESTTYPES_MAIN_TEST)
                        );
                        testses.add(obj);
                    }catch (JSONException ex){
                        Log.i("SelectiveTestType-Exc.", ex.getMessage());
                    }
                }
            }
        } catch (JSONException e) {
            testses = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return testses;
    }

    public Tests getTestReportObject() {
        Tests test = new Tests();
        try {
            JSONObject json = new JSONObject(response);
            test = new Tests(
                    json.optString(Constants.TESTS_ID),
                    json.optString(Constants.TESTS_TYPE),
                    json.optString(Constants.TESTS_ATHLETE),
                    json.optString(Constants.TESTS_SELECTIVE),
                    json.optLong(Constants.TESTS_FIRST_VALUE),
                    json.optLong(Constants.TESTS_SECOND_VALUE),
                    (float) (json.optDouble(Constants.TESTS_RATING)),
                    json.optString(Constants.TESTS_WINGSPAN),
                    json.optString(Constants.TESTS_USER),
                    Services.convertBoolInInt(true),
                    true,
                    json.optJSONArray("bestValue").optString(0)
            );
        } catch (JSONException e) {
            test = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return test;
    }

    public Tests getTestObject() {
        Tests test = new Tests();
        try {
            JSONObject json = new JSONObject(response);
            test = new Tests(
                json.optString(Constants.TESTS_ID),
                json.optString(Constants.TESTS_TYPE),
                json.optString(Constants.TESTS_ATHLETE),
                json.optString(Constants.TESTS_SELECTIVE),
                json.optLong(Constants.TESTS_FIRST_VALUE),
                json.optLong(Constants.TESTS_SECOND_VALUE),
                (float) (json.optDouble(Constants.TESTS_RATING)),
                json.optString(Constants.TESTS_WINGSPAN),
                json.optString(Constants.TESTS_USER),
                Services.convertBoolInInt(true),
                true,
                    returnTest(json.optJSONArray(Constants.TESTS_VALUES))
            );
        } catch (JSONException e) {
            test = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return test;
    }

    public Tests getTestObjectTest() {
        Tests tests = new Tests();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject json = new JSONObject(jsonArray.getString(i));
                     tests = new Tests(
                            json.optString(Constants.TESTS_ID),
                            json.optString(Constants.TESTS_TYPE),
                            json.optString(Constants.TESTS_ATHLETE),
                            json.optString(Constants.TESTS_SELECTIVE),
                            json.optLong(Constants.TESTS_FIRST_VALUE),
                            json.optLong(Constants.TESTS_SECOND_VALUE),
                            (float) (json.optDouble(Constants.TESTS_RATING)),
                            json.optString(Constants.TESTS_WINGSPAN),
                            json.optString(Constants.TESTS_USER),
                            Services.convertBoolInInt(true),
                            true,
                            returnTest(json.optJSONArray(Constants.TESTS_VALUES))
                    );
                }
            }
        } catch (JSONException e) {
            tests = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return tests;
    }

    /*************************************
     *USER_SELECTIVE
     *************************************/

    public ArrayList<SelectiveUsers> getUserSelectives() {
        ArrayList<SelectiveUsers> selectives = new ArrayList<SelectiveUsers>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    try {
                        JSONObject json = new JSONObject(jsonArray.getString(i));
                        boolean isAdmin = json.optBoolean("isSelectiveAdmin", false);
                        JSONObject jsonSelective = new JSONObject(json.getString(Constants.USER_SELECTIVE_SELECTIVE));
                        JSONObject jsonUser = json.getJSONObject(Constants.USER_SELECTIVE_USER);
                        DatabaseHelper db = new DatabaseHelper(act);
                        db.openDataBase();
                        Team team = db.getTeamByIdTeam(jsonSelective.optString(Constants.SELECTIVES_TEAM));
                        SelectiveUsers obj = new SelectiveUsers(
                                jsonSelective.optString(Constants.SELECTIVES_ID),
                                jsonSelective.optString(Constants.SELECTIVES_TITLE),
                                jsonSelective.optString(Constants.SELECTIVES_TEAM),
                                jsonSelective.optString(Constants.USER_SELECTIVE_USER),
                                jsonSelective.optString(Constants.SELECTIVES_DATE),
                                jsonSelective.optString(Constants.SELECTIVES_CODESELECTIVE),
                                jsonSelective.optBoolean(Constants.SELECTIVES_CANSYNC, false),
                                jsonSelective.optString(Constants.SELECTIVES_CITY),
                                jsonSelective.optString(Constants.SELECTIVES_NEIGHBORHOOD),
                                jsonSelective.optString(Constants.SELECTIVES_STATE),
                                jsonSelective.optString(Constants.SELECTIVES_STREET),
                                jsonSelective.optString(Constants.SELECTIVES_POSTALCODE),
                                jsonSelective.optString(Constants.SELECTIVES_NOTE),
                                jsonSelective.optString(Constants.SELECTIVES_ADDRESS),
                                jsonUser.getString(Constants.USER_NAME),
                                isAdmin,
                                team.getUrlImage(),
                                team.getName()
                        );

                        selectives.add(obj);
                    }catch(JSONException e){
                        Log.i("JSONException", e.getMessage());
                    }
                }
            }
        } catch (JSONException e) {
            selectives = null;
            Log.i("ERROR: getPositions", e.getMessage());
        }
        return selectives;
    }

    /**********************************CEP**********************************/
    public CEP getCep() {
        CEP cep = null;
        try {
            JSONObject json = new JSONObject(this.response);
            cep = new CEP(
                    json.optString(Constants.CEP),
                    json.optString(Constants.STREET),
                    json.optString(Constants.NEIGHBORHOOD),
                    json.optString(Constants.STATE),
                    json.optString(Constants.CITY)
            );
        } catch (JSONException jsonExc) {
            Log.i("JSON ERROR", jsonExc.toString());
        }
        return cep;
    }

    /***************************RESULTS SELECTIVES*************************/
    public ArrayList<ResultTestsAthletes> deserializerResultsSelectiveHistoric(String response){
        ArrayList<ResultTestsAthletes> arraylistHash=new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("byTestType");
            JSONArray jsonAthletes = jsonObject.getJSONArray("byAthletes");
            for (int i=0; i<=jsonArray.length()-1;i++) {
                try {
                    TestTypes type = new TestTypes();

                    JSONObject json = jsonArray.getJSONObject(i);

                    type.setId(json.optString("type"));
                    type.setName(json.optString("name"));

                    JSONArray jsonArrayTests = json.getJSONArray("tests");

                    ArrayList<ChildItemTests> tests = new ArrayList<>();

                    for (int x = 0; x <= jsonArrayTests.length() - 1; x++) {
                        String athlete = "";
                        JSONObject jsonAthlete = new JSONObject(jsonArrayTests.getJSONObject(x).optString("athlete", ""));

                        for(int a=0; a<=jsonAthletes.length()-1;a++){
                            if(jsonAthletes.getJSONObject(a).getString("id").equals(jsonAthlete.getString("_id")))
                            {
                                athlete = jsonAthletes.getString(a);
                                break;
                            }
                        }

                        tests.add(getResultTests(jsonArrayTests.getJSONObject(x),
                                athlete.equals("")||athlete.equals("null") ? new JSONObject() : new JSONObject(athlete)));
                    }
                    arraylistHash.add(new ResultTestsAthletes(type, tests));
                }catch(Exception ex){
                    Log.i("Exception", ex.getMessage());
                }
            }
        }catch(Exception ex){
            String message = ex.getMessage();
            Log.i("Exception", message);
        }
        return arraylistHash;
    }

    private ChildItemTests getResultTests(JSONObject jsonTest, JSONObject jsonAthlete) throws JSONException {
        ChildItemTests test = new ChildItemTests();

        test.setId(jsonTest.optString("_id"));
        test.setName(jsonTest.getJSONObject("type").optString("name"));
        test.setFirstResult(Integer.parseInt(jsonTest.optJSONArray("bestValue").optString(0)));
        test.setSecondResult(jsonTest.optInt("secondValue"));
        test.setPosition(jsonTest.optInt("position"));
        test.setRaiting(jsonTest.optInt("rating"));
        test.setType("");
        test.setUser(jsonTest.optString("user"));
        test.setValueType(jsonTest.getJSONObject("type").optString(Constants.TESTTYPES_VALUETYPES).toLowerCase());

        if(jsonAthlete!=null && jsonAthlete.toString().replace("{","").replace("}","").length()>0) {
            test.setPositions(getPositionsResults(jsonAthlete.getJSONArray("positions")));
            test.setIdAthlete(jsonAthlete.getJSONObject("completeInfo").optString(Constants.ATHLETES_ID));
            test.setNameAthlete(jsonAthlete.getJSONObject("completeInfo").optString(Constants.ATHLETES_NAME));
            test.setImageURL(jsonAthlete.getJSONObject("completeInfo").optString(Constants.ATHLETES_IMAGE_URL));
        }
        else{
            test.setIdAthlete("");
            test.setNameAthlete("");
        }
        return test;
    }

    private String getPositionsResults(JSONArray jsonPositions) throws JSONException {
        String positions = "";

        for(int p=0;p<=jsonPositions.length()-1;p++)
            positions =positions + jsonPositions.getJSONObject(p).optString("name")+", ";

        return positions.length()>2?positions.substring(0, positions.length()-2) : positions;
    }

    public Activity getAct() {
        return act;
    }

    public void setAct(Activity act) {
        this.act = act;
    }
}
