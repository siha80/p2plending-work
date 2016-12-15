package com.skp.payment.p2plending.user.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.payment.p2plending.launcher.common.util.HttpClientManager;
import com.skp.payment.p2plending.launcher.common.util.PropertyReader;
import com.skp.payment.p2plending.launcher.domain.Token;
import com.skp.payment.p2plending.launcher.domain.User;
import com.skp.payment.p2plending.launcher.domain.UserAuthenticationToken;
import com.skp.payment.p2plending.user.ui.LoginEvent;
import com.skp.payment.p2plending.user.ui.SignUpEvent;
import com.sun.jersey.api.client.ClientResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@Service
public class UserEventHandler implements UserService {
    private static final String TOKEN_KEY = "12345678123456781234567812345678";
    private static final String USER_SIGNUP_URL = PropertyReader.getValue("p2plending.user.address") + "/v1/users/signup";
    private static final String USER_LOGIN_URL = PropertyReader.getValue("p2plending.user.address") + "/v1/users/authentication";

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public SignUpEvent.Response singup(SignUpEvent.Request request) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientManager httpClientManager = new HttpClientManager();
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + new Token().toString(TOKEN_KEY));
        ClientResponse r = httpClientManager.postRawData(USER_SIGNUP_URL, headerMap, objectMapper.writeValueAsString(request));
        if(r.getStatus() == 201) {
            SignUpEvent.Response.FromService res = objectMapper.readValue(r.getEntity(String.class), SignUpEvent.Response.FromService.class);
            return modelMapper.map(res.getUser(), SignUpEvent.Response.ToUser.class);
        } else {
            return new SignUpEvent.Response() {{
                setCode(SignUpEvent.SignupErrors.FAIL);
            }};
        }
    }
    @Override
    public LoginEvent.Response.ToUser loginFromUser(LoginEvent.Request request) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientManager httpClientManager = new HttpClientManager();

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + new Token().toString(TOKEN_KEY));
        ClientResponse r = httpClientManager.postRawData(USER_LOGIN_URL, headerMap, objectMapper.writeValueAsString(request));
        LoginEvent.Response.Inquiry result = objectMapper.readValue(r.getEntity(String.class), LoginEvent.Response.Inquiry.class);

        LoginEvent.Response.ToUser response = new LoginEvent.Response.ToUser();
        if(result.isValid()) {
            response.setToken(new UserAuthenticationToken(result.getUserSerialNumber()).toString(TOKEN_KEY));
        } else {
            response.setCode(LoginEvent.LoginErrors.FAILED);
        }
        return response;
    }
}
