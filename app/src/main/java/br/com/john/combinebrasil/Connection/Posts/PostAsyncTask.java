package br.com.john.combinebrasil.Connection.Posts;

import br.com.john.combinebrasil.AthletesActivity;
import br.com.john.combinebrasil.ChooseTeamSelectiveActivity;
import br.com.john.combinebrasil.EditSelectiveActivity;
import br.com.john.combinebrasil.HistoricPlayerActivity;
import br.com.john.combinebrasil.HistoricPlayersSelectiveActivity;
import br.com.john.combinebrasil.HistoricRankingTestsActivity;
import br.com.john.combinebrasil.HistoricSelectiveActivity;
import br.com.john.combinebrasil.LoginActivity;
import br.com.john.combinebrasil.MainActivity;
import br.com.john.combinebrasil.MenuActivity;
import br.com.john.combinebrasil.Services.Constants;

/**
 * Created by GTAC on 20/07/2017.
 */

public class PostAsyncTask extends PostBase{
    @Override
    protected void onPostExecute(String status) {
        if (whoCalled.equals(Constants.CALLED_GET_ATHLETES)) {
            if (activity.getClass().getSimpleName().equals("HistoricPlayersSelectiveActivity"))
                HistoricPlayersSelectiveActivity.returnGetPlayers(activity, result, statusCode);
        }
        else if(whoCalled.equals(Constants.CALLED_GET_USER_SELECTIVE))
            HistoricSelectiveActivity.returnGetAllSelectives(activity, result, statusCode);
        else if(whoCalled.equals(Constants.CALLED_RESULTS_ATHLETE))
            HistoricPlayerActivity.returnGetResultsSelectiveAthlete(activity, result, statusCode);
        else if(whoCalled.equals(Constants.CALLED_GET_TEST_TYPES))
            HistoricRankingTestsActivity.returnGetTestTypes(activity, result, statusCode);
        else if(method== MainActivity.METHOD_ATHLETE_SELECTIVE_GET){
            if(activity.getClass().getSimpleName().equals("MainActivity"))
                MainActivity.returnGetAthleteSelective(activity, result, statusCode);
        }
        else if(whoCalled.equals(Constants.CALLED_GET_TEAM)){
            if(activity.getClass().getSimpleName().equals("ChooseTeamSelectiveActivity"))
                ChooseTeamSelectiveActivity.returnGetAllTeams(activity, result, statusCode);
            else if(activity.getClass().getSimpleName().equals("HistoricSelectiveActivity"))
                HistoricSelectiveActivity.returnGetAllTeams(activity, result, statusCode);
        }
        else if(whoCalled.equals("ESQUECEU_CODIGO")){
            if(activity.getClass().getSimpleName().equals("ChooseTeamSelectiveActivity"))
                ChooseTeamSelectiveActivity.retornoEsqueciCodigo(activity, result, statusCode);
        }
        else if(whoCalled.equals(Constants.CALLED_GET_TESTS)){
            AthletesActivity.returnGetAllTests(activity, result, statusCode);
        }
        else if(whoCalled.equals("POST_ESQUECI_SENHA")){
            LoginActivity.retornoEsqueciSenha(activity, result, statusCode);
        }
        else if(whoCalled.equals(Constants.CALLED_SELECTIVE_ADM))
            EditSelectiveActivity.retornaADMSelective(activity, result, statusCode);
        else
            LoginActivity.afterLogin(result, activity, statusCode);
    }

}
