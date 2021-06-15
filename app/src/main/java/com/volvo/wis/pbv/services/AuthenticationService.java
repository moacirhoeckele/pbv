package com.volvo.wis.pbv.services;

import android.content.Context;
import android.content.ContextWrapper;

import com.volvo.wis.pbv.R;
import com.volvo.wis.pbv.helpers.Log4jHelper;
import com.volvo.wis.pbv.viewmodels.OperationResultSingle;
import com.volvo.wis.pbv.viewmodels.UserViewModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AuthenticationService extends ContextWrapper implements IAuthenticationService {

    private UserViewModel loggedUser;

    public AuthenticationService(Context base) {
        super(base);
    }

    @Override
    public OperationResultSingle<UserViewModel> Authenticate(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(json);

            //{"user":"A265063","password":"123"}
            if (!obj.isEmpty() && obj.containsKey("user") && obj.containsKey("password")) {
                loggedUser = new UserViewModel();
                loggedUser.setId(obj.get("user").toString().toUpperCase());
                return new OperationResultSingle<UserViewModel>(true, loggedUser);
            }

            return new OperationResultSingle<UserViewModel>(false, null, getString(R.string.invalid_credential));
            
        } catch (Exception e){
            Log4jHelper.getLogger(AuthenticationService.class.getName()).error(e);
            return new OperationResultSingle<UserViewModel>(false, null, getString(R.string.invalid_credential));
        }
    }

    @Override
    public OperationResultSingle<UserViewModel> GetLoggedUser() {
        return new OperationResultSingle<UserViewModel>(true, loggedUser);
    }
}
