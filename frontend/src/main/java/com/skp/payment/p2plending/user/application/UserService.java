package com.skp.payment.p2plending.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skp.payment.p2plending.user.ui.LoginEvent;
import com.skp.payment.p2plending.user.ui.SignUpEvent;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface UserService {
    SignUpEvent.Response singup(SignUpEvent.Request request) throws IOException, NoSuchAlgorithmException, KeyManagementException;

    public LoginEvent.Response.ToUser loginFromUser(LoginEvent.Request request) throws IOException, NoSuchAlgorithmException, KeyManagementException;
}
