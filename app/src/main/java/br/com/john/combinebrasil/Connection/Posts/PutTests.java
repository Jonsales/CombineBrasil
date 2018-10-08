package br.com.john.combinebrasil.Connection.Posts;

import br.com.john.combinebrasil.CreateAccountAthleteActivity;
import br.com.john.combinebrasil.CronometerOnlyOneActivity;
import br.com.john.combinebrasil.ResultsOnlyOneActivity;
import br.com.john.combinebrasil.TimerActivity;

/**
 * Created by John on 29/08/2017.
 */

public class PutTests extends PutBase {

    @Override
    protected void onPostExecute(String status) {
        if(activity.getClass().getSimpleName().equals("CronometerOnlyOneActivity"))
            CronometerOnlyOneActivity.returnPostTest(activity, resp, result);
        else if(activity.getClass().getSimpleName().equals("ResultsOnlyOneActivity"))
            ResultsOnlyOneActivity.returnPostTest(activity, resp, result);
        else if(activity.getClass().getSimpleName().equals("TimerActivity"))
            TimerActivity.returnPostTest(activity, resp, result);
        else if(activity.getClass().getSimpleName().equals("CreateAccountAthleteActivity"))
            CreateAccountAthleteActivity.afterSendAthlete(activity, resp, result);

    }
}
