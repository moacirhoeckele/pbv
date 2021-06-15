package com.volvo.wis.pbv.services;

import com.volvo.wis.pbv.viewmodels.OperationResultSingle;
import com.volvo.wis.pbv.viewmodels.UserViewModel;

public interface IAuthenticationService {

    OperationResultSingle<UserViewModel> Authenticate(String json);

    OperationResultSingle<UserViewModel> GetLoggedUser();
}
