package br.com.john.combinebrasil.Connection.Posts;

import br.com.john.combinebrasil.EditSelectiveActivity;
import br.com.john.combinebrasil.EditTeamActivity;
import br.com.john.combinebrasil.Services.Constants;

/**
 * Created by John on 02/11/2017.
 */

public class PutEditSelectiveAsyncTask extends PutBase {

    @Override
    protected void onPostExecute(String status) {
        if (METHOD.equals(Constants.METHOD_EDIT_SELECTIVE))
            EditSelectiveActivity.returnPutEditSelective(activity, result, resp);
        else if (METHOD.equals(Constants.METHOD_EDIT_TEAM))
            EditTeamActivity.returnEditTeam(activity, result, resp);
        }
}