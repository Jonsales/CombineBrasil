package br.com.john.combinebrasil.Connection;

/**
 * Created by GTAC on 18/10/2016.
 */

import android.app.Activity;
import android.util.Log;

import br.com.john.combinebrasil.AthletesActivity;
import br.com.john.combinebrasil.ChooseTeamSelectiveActivity;
import br.com.john.combinebrasil.CreateAccountAthleteActivity;
import br.com.john.combinebrasil.CreateTeamActivity;
import br.com.john.combinebrasil.DetailsAthletes;
import br.com.john.combinebrasil.EditSelectiveActivity;
import br.com.john.combinebrasil.EditTeamActivity;
import br.com.john.combinebrasil.EnterSelectiveActivity;
import br.com.john.combinebrasil.HistoricPlayerActivity;
import br.com.john.combinebrasil.HistoricPlayersSelectiveActivity;
import br.com.john.combinebrasil.HistoricRankingPositionsActivity;
import br.com.john.combinebrasil.HistoricRankingTestsActivity;
import br.com.john.combinebrasil.HistoricSelectiveActivity;
import br.com.john.combinebrasil.LocalSelectiveActivity;
import br.com.john.combinebrasil.LoginActivity;
import br.com.john.combinebrasil.MainActivity;
import br.com.john.combinebrasil.MenuActivity;
import br.com.john.combinebrasil.MenuHistoricSelectiveActivity;
import br.com.john.combinebrasil.ResultsOnlyOneActivity;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.TestSelectiveActivity;


public class ReturnResponse {
    public static String login;
    public static String password;

    private static ReturnResponse ourInstance = new ReturnResponse();

    public static ReturnResponse getInstance() {
        return ourInstance;
    }

    private ReturnResponse() {
    }

    //esse método é de retorno quando à sucesso na requisição
    //redireciona de volta para quem chamou
    //whocalled é importante para identificar quem  fez a requisição e para onde os dados irão
    public void goTo(String whoCalled, String response, boolean isList, Activity activity, int statuCode) {
        try {
            if (!response.equals(null) || response.length() <= 0) {
                if (whoCalled.equals("updateAthleteAccount")) {
                    CreateAccountAthleteActivity.returnAccountAthlete(activity, response);
                } else if (whoCalled.equals("CONFIGURA_POSICOES")) {
                    MainActivity.retornaPosicoes(activity, response, statuCode);
                } else if (whoCalled.equals("PROCURA_POSICAO"))
                    DetailsAthletes.retornaProcuraPosicao(activity, response, statuCode);
                if (whoCalled.equals("UPDATE_TEST")) {
                    if (activity.getClass().getSimpleName().equals("ResultsOnlyOneActivity"))
                        ResultsOnlyOneActivity.returnUpdateSync(activity, response);
                }
                if (whoCalled.equals("GET_DETAILS_ATHLETES")) {
                    if (activity.getClass().getSimpleName().equals("CreateAccountAthleteActivity"))
                        CreateAccountAthleteActivity.returnDetailsAthletes(response, activity, statuCode);
                }
                if (whoCalled.equals(Constants.CALLED_LOGIN)) {
                    LoginActivity.afterLogin(response, activity, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_USER)) {
                    LoginActivity.returnGetDataUser(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_SELECTIVE)) {
                    if (activity.getClass().getSimpleName().equals("EnterSelectiveActivity"))
                        EnterSelectiveActivity.returnGetAllSelectives(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("HistoricSelectiveActivity"))
                        HistoricSelectiveActivity.returnGetAllSelectives(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("MenuActivity"))
                        MenuActivity.returnGetSelectiveByCode(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_TEAM)) {
                    if (activity.getClass().getSimpleName().equals("ChooseTeamSelectiveActivity"))
                        ChooseTeamSelectiveActivity.returnGetAllTeams(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("HistoricSelectiveActivity"))
                        HistoricSelectiveActivity.returnGetAllTeams(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("MenuHistoricSelectiveActivity"))
                        MenuHistoricSelectiveActivity.returnGetTeamSelective(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_ATHLETES)) {
                    if (activity.getClass().getSimpleName().equals("SyncAthleteActivity"))
                        HistoricPlayersSelectiveActivity.returnGetPlayers(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_POSITIONS)) {
                    if (activity.getClass().getSimpleName().equals("CreateAccountAthleteActivity"))
                        CreateAccountAthleteActivity.returnGetAllPositions(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_TESTTYPES)) {
                    if (activity.getClass().getSimpleName().equals("MainActivity"))
                        MainActivity.testResponse(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("TestSelectiveActivity"))
                        TestSelectiveActivity.returnUpdateTests(activity, statuCode, response);
                } else if (whoCalled.equals(Constants.CALLED_GET_TESTS)) {
                    if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                        AthletesActivity.returnGetAllTests(activity, response, statuCode);
                } else if (whoCalled.equals("UpdateSelectiveAthletes")) {
                    if (activity.getClass().getSimpleName().equals("MainActivity"))
                        MainActivity.returnUpdateSelectiveAthletes(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                        AthletesActivity.returnUpdateSelectiveAthletes(activity, response, statuCode);
                } else if (whoCalled.equals("UpdateSelectiveAthletes")) {
                    if (activity.getClass().getSimpleName().equals("MainActivity"))
                        MainActivity.returnUpdateSelectiveAthletes(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                        AthletesActivity.returnUpdateSelectiveAthletes(activity, response, statuCode);
                } else if (whoCalled.equals("UpdateAthletes")) {
                    if (activity.getClass().getSimpleName().equals("MainActivity"))
                        MainActivity.returnUpdateAthletes(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                        AthletesActivity.returnUpdateAthletes(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_CEP)) {
                    if (activity.getClass().getSimpleName().equals("LocalSelectiveActivity"))
                        LocalSelectiveActivity.returnCEP(activity, response, statuCode);
                    else if (activity.getClass().getSimpleName().equals("CreateTeamActivity"))
                        CreateTeamActivity.returnCEP(activity, response, statuCode);
                    else if(activity.getClass().getSimpleName().equals("EditSelectiveActivity"))
                        EditSelectiveActivity.returnCEP(activity, response, statuCode);
                    else if(activity.getClass().getSimpleName().equals("EditTeamActivity"))
                        EditTeamActivity.returnCEP(activity, response, statuCode);
                } else if (whoCalled.endsWith(Constants.CALLED_RESULTS_ATHLETE)) {
                    if (activity.getClass().getSimpleName().equals("HistoricPlayerActivity")) ;
                    HistoricPlayerActivity.returnGetResultsSelectiveAthlete(activity, response, statuCode);
                } else if (whoCalled.equals(Constants.CALLED_GET_SUBSCRIBERS))
                    MenuHistoricSelectiveActivity.returnGetSubscriber(activity, response, statuCode);
                else if (whoCalled.equals(Constants.CALLED_GET_POSITIONS_RESULT))
                    HistoricRankingPositionsActivity.returnGetTestTypes(activity, response, statuCode);
                else if (whoCalled.equals(Constants.CALLED_GET_TEST_TYPES))
                    HistoricRankingTestsActivity.returnGetTestTypes(activity, response, statuCode);
                if(whoCalled.equals(Constants.CALLED_GET_INFORMATIONS_SELECTIVES))
                    EditSelectiveActivity.retornagetSelectiveInformations(activity,response, statuCode);
                if(whoCalled.equals(Constants.GET_INFO_TEAM))
                    EditTeamActivity.retornaGetTeasmInformations(activity,response, statuCode);
            }
        } catch (Exception e) {
            Log.i("ERRO", e.toString());
        }
    }
}

