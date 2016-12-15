package com.skp.payment.p2plending.backoffice.crowd;

import lombok.Data;

import java.util.Map;

/**
 * Created by 1000903 on 2016-11-14.
 */
@Data
public class CrowdModel {

    @Data
    public static class Requset {
        private String userId;
        private String password;
    }



    @Data
    public static class Result{
        private String resultCd;
        private String resultMsg;
        private Map resultData;

        public Result(){
        }

        public Result(String resultCd, String resultMsg, Map resultData){
            this.resultCd = resultCd;
            this.resultMsg = resultMsg;
            this.resultData = resultData;
        }

    }

    @Data
    public static class UserInfo {
        private String userId;
        private String email;
        private String userName;

        public UserInfo(){
        }

        public UserInfo(String userId, String email, String userName){
            this.userId = userId;
            this.email = email;
            this.userName = userName;
        }
    }

}
