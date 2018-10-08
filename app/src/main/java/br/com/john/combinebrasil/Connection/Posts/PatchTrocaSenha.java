package br.com.john.combinebrasil.Connection.Posts;

import br.com.john.combinebrasil.CronometerOnlyOneActivity;
import br.com.john.combinebrasil.MenuActivity;
import br.com.john.combinebrasil.PerfilUserActivity;

/**
 * Created by John on 13/09/2017.
 */

public class PatchTrocaSenha extends PutBase {

    @Override
    protected void onPostExecute(String status) {
        if(activity.getClass().getSimpleName().equals("MenuActivity"))
            MenuActivity.retornoSenhaAlterada(activity, resp, resp);
        else if(activity.getClass().getSimpleName().equals("PerfilUserActivity"))
            PerfilUserActivity.retornoSenhaAlterada(activity, resp, resp);
    }

}
