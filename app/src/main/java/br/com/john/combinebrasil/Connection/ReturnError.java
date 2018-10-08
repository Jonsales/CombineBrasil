package br.com.john.combinebrasil.Connection;

/**
 * Created by GTAC on 18/10/2016.
 */

import android.app.Activity;

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
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.TestSelectiveActivity;

public class ReturnError {
    public String message;

    private static ReturnError ourInstance = new ReturnError();

    public static ReturnError getInstance() {
        return ourInstance;
    }

    private ReturnError() {
    }

    public void goTo(String whoCalled, Activity activity, String message, int statusError) {
        if (whoCalled.equals(Constants.CALLED_POST_ATHLETES)) {
            //CreateAccountAthleteActivity.returnPostAthlete(activity, message, statusError);
        }
        if (whoCalled.equals("GET_DETAILS_ATHLETES")) {
            if (activity.getClass().getSimpleName().equals("CreateAccountAthleteActivity"))
                CreateAccountAthleteActivity.returnDetailsAthletes(message, activity, statusError);
        }
        else if(whoCalled.equals("PROCURA_POSICAO"))
            DetailsAthletes.retornaProcuraPosicao(activity, message, statusError);
        else if (whoCalled.equals("CONFIGURA_POSICOES")) {
            MainActivity.retornaPosicoes(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_LOGIN)) {
            LoginActivity.afterLogin(message, activity, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_USER)) {
            LoginActivity.returnGetDataUser(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_POSITIONS)) {
            if (activity.getClass().getSimpleName().equals("CreateAccountAthleteActivity"))
                CreateAccountAthleteActivity.returnGetAllPositions(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_SELECTIVE)) {
            if (activity.getClass().getSimpleName().equals("EnterSelectiveActivity"))
                EnterSelectiveActivity.returnGetAllSelectives(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("EnterSelectiveActivity"))
                HistoricSelectiveActivity.returnGetAllSelectives(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("MenuActivity"))
                MenuActivity.returnGetSelectiveByCode(activity, message, statusError);
        }
        else if (whoCalled.equals("UpdateSelectiveAthletes")) {
            if (activity.getClass().getSimpleName().equals("MainActivity"))
                MainActivity.returnUpdateSelectiveAthletes(activity, message, statusError);
            if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                AthletesActivity.returnUpdateSelectiveAthletes(activity, message, statusError);
        }
        else if (whoCalled.equals("UpdateAthletes")) {
            if (activity.getClass().getSimpleName().equals("MainActivity"))
                MainActivity.returnUpdateAthletes(activity, message, statusError);
            if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                AthletesActivity.returnUpdateAthletes(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_TESTTYPES)) {
            if (activity.getClass().getSimpleName().equals("TestSelectiveActivity"))
                TestSelectiveActivity.returnUpdateTests(activity, statusError, message);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_TESTTYPES)) {
            if (activity.getClass().getSimpleName().equals("TestSelectiveActivity"))
                TestSelectiveActivity.returnUpdateTests(activity, statusError, message);
            else if (activity.getClass().getSimpleName().equals("MainActivity"))
                MainActivity.testResponse(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_CEP)) {
            if (activity.getClass().getSimpleName().equals("LocalSelectiveActivity"))
                LocalSelectiveActivity.returnCEP(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("CreateTeamActivity"))
                CreateTeamActivity.returnCEP(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("EditSelectiveActivity"))
                EditSelectiveActivity.returnCEP(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("EditTeamActivity"))
                EditTeamActivity.returnCEP(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_TEAM)) {
            if (activity.getClass().getSimpleName().equals("ChooseTeamSelectiveActivity"))
                ChooseTeamSelectiveActivity.returnGetAllTeams(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("HistoricSelectiveActivity"))
                HistoricSelectiveActivity.returnGetAllTeams(activity, message, statusError);
            else if (activity.getClass().getSimpleName().equals("MenuHistoricSelectiveActivity"))
                MenuHistoricSelectiveActivity.returnGetTeamSelective(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_SUBSCRIBERS)) {
            MenuHistoricSelectiveActivity.returnGetSubscriber(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_POSITIONS_RESULT))
            HistoricRankingPositionsActivity.returnGetTestTypes(activity, message, statusError);

        else if (whoCalled.equals(Constants.CALLED_GET_ATHLETES)) {
            if (activity.getClass().getSimpleName().equals("SyncAthleteActivity"))
                HistoricPlayersSelectiveActivity.returnGetPlayers(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_RESULTS_ATHLETE)) {
            if (activity.getClass().getSimpleName().equals("HistoricPlayerActivity")) ;
            HistoricPlayerActivity.returnGetResultsSelectiveAthlete(activity, message, statusError);
        }
        else if (whoCalled.equals(Constants.CALLED_GET_TEST_TYPES))
            HistoricRankingTestsActivity.returnGetTestTypes(activity, message, statusError);
        else if (whoCalled.equals(Constants.CALLED_GET_TESTS)) {
            if (activity.getClass().getSimpleName().equals("AthletesActivity"))
                AthletesActivity.returnGetAllTests(activity, message, statusError);
        }
        if(whoCalled.equals(Constants.CALLED_GET_INFORMATIONS_SELECTIVES))
            EditSelectiveActivity.retornagetSelectiveInformations(activity,message, statusError);
        if(whoCalled.equals(Constants.GET_INFO_TEAM))
            EditTeamActivity.retornaGetTeasmInformations(activity,message, statusError);
    }
}