package com.skp.payment.p2plending.user.ui;


import com.skp.payment.p2plending.launcher.domain.User;
import com.skp.payment.p2plending.user.ui.event.error.P2pLendingError;
import lombok.Data;

public class SignUpEvent {
    public enum SignupErrors {
        FAIL
    }

    @Data
    public static class Request extends SignUpEvent {
        private String email;
        private String password;
        private String userName;
    }

    @Data
    public static class Response extends SignUpEvent {
        private P2pLendingError error;
        public void setCode(SignupErrors e) {
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

        @Data
        public static class FromService extends Response {
            private User user;
        }

        @Data
        public static class ToUser extends Response {
            private String email;
            private String userName;
        }
    }
}
