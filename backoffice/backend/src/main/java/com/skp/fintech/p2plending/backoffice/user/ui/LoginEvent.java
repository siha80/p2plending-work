package com.skp.fintech.p2plending.backoffice.user.ui;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

public class LoginEvent {

    public enum LoginErrors {
        FAILED
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class Request extends LoginEvent {
        private String userId;
        private String password;
    }

    @Data
    public static class AuthenticationResponse{
        private String resultCd;
        private String resultMsg;
        private Map resultData;
    }

    @Data
    public static class AddUserResponse{
        private String resultCd;
        private String resultMsg;
    }

    @Data
    public static class UserInfo{
        private String userId;
        private String userName;
        private String email;
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class Response extends LoginEvent {
        private String token;
        private String userName;
        private String resultCd;
        private String resultMsg;

        public boolean expected() {
            return LoginErrors.FAILED.toString().equals(resultCd);
        }
    }
}
