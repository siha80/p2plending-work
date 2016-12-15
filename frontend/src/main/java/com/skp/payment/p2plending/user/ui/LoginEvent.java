package com.skp.payment.p2plending.user.ui;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.skp.payment.p2plending.user.ui.event.error.P2pLendingError;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class LoginEvent {
    public enum LoginErrors {
        FAILED
    }

    @Data
    public static class Request extends LoginEvent {
        private String userName;
        private String email;
        private String password;
    }

    @Data
    public static class Response extends LoginEvent {
        @Data
        @EqualsAndHashCode(callSuper = true)
        public static class Inquiry extends Response {
            private String userSerialNumber;
            @JsonProperty("isValid")
            private boolean isValid;
        }

        @Data
        @EqualsAndHashCode(callSuper = true)
        public static class ToUser extends Response {
            private String token;

            private P2pLendingError error;
            public void setCode(LoginErrors e) {
                if (e != null) {
                    if (error == null) {
                        error = new P2pLendingError();
                    }
                    error.setCode(e.toString());
                }
            }

            public boolean expected() {
                return error != null;
            }
        }
    }
}
