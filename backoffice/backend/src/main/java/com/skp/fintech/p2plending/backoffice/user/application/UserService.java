package com.skp.fintech.p2plending.backoffice.user.application;

import com.skp.fintech.p2plending.backoffice.user.ui.LoginEvent;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface UserService {

    LoginEvent.Response login(LoginEvent.Request request) throws IOException, NoSuchAlgorithmException, KeyManagementException;

    LoginEvent.AddUserResponse addUser(LoginEvent.Request request);
}
