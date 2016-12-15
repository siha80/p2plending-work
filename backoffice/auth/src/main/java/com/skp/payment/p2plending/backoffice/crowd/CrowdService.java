package com.skp.payment.p2plending.backoffice.crowd;

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import com.skp.payment.p2plending.backoffice.common.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CrowdService {

    private static Logger logger = LoggerFactory.getLogger(CrowdService.class);

    @Value("${crowd.api.uri}")
    private String CROWD_API_URI;

    @Value("${crowd.api.application}")
    private String CROWD_API_APPLICATION;

    /**
     * 인증 요청.
     * @param request
     * @return
     */
    public CrowdModel.Result authenticate(CrowdModel.Requset request){

        testConntect();

        if(isAuthenticateValidParam(request)){
            return error("9001", "사용자 정보가 유효하지 않습니다.");
        }

        User user = authenticateUser(request.getUserId(), request.getPassword());

        if(user == null){
            return error("9002", "통합인증실패");
        }

        Map resultData = new HashMap<>();
        resultData.put("userInfo", convertUserInfo(user));

        return new CrowdModel.Result("0000", "통합인증성공", resultData);
    }


    /**
     * 사용자 정보 조회.
     * @param request
     * @return
     */
    public CrowdModel.Result info(CrowdModel.Requset request){

        if(isGetUserValidParam(request)){
            return error("9001", "사용자 정보가 유효하지 않습니다.");
        }

        User user = getUser(request.getUserId());

        if(user == null){
            return error("9003", "사용자정보 조회 실패");
        }

        Map resultData = new HashMap<>();
        resultData.put("userInfo", convertUserInfo(user));

        return new CrowdModel.Result("0000", "사용자정보 조회 성공", resultData);
    }


    /**
     * CROWD 인증 요청.
     * @param userId
     * @param password
     * @return
     */
    private User authenticateUser(String userId, String password ){

        User user = null;

        try {
            CrowdClient client = new RestCrowdClientFactory().newInstance(CROWD_API_URI, CROWD_API_APPLICATION, CROWD_API_APPLICATION);
            client.testConnection();
            user = client.authenticateUser(userId, password);
            client.shutdown();

        } catch(Exception e ) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return user;
    }

    /**
     * test Connection
     */
    private void testConntect(){
        try {
            CrowdClient client = new RestCrowdClientFactory().newInstance(CROWD_API_URI, CROWD_API_APPLICATION, CROWD_API_APPLICATION);
            client.testConnection();
            client.shutdown();
        } catch(Exception e ) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 사용자 정보 조회.
     * @param userId
     * @return
     */
    private User getUser(String userId){

        User user = null;

        try {
            CrowdClient client = new RestCrowdClientFactory().newInstance(CROWD_API_URI, CROWD_API_APPLICATION, CROWD_API_APPLICATION);
            user = client.getUser(userId);
            client.shutdown();
        } catch(Exception e ) {
            logger.error(e.getMessage());
        }

        return user;
    }

    /**
     * 인증 파라미터 검증
     * @param request
     * @return
     */
    private boolean isAuthenticateValidParam(CrowdModel.Requset request){

        if(CommonUtils.isEmpty(request.getUserId())){
            return true;
        }

        if(CommonUtils.isEmpty(request.getPassword())){
            return true;
        }

        return false;
    }

    /**
     * 사용자 조회 파라미터 검증.
     * @param request
     * @return
     */
    private boolean isGetUserValidParam(CrowdModel.Requset request){
        if(CommonUtils.isEmpty(request.getUserId())){
            return true;
        }
        return false;
    }

    /**
     * 에러 처리.
     * @param resultCd
     * @param resultMsg
     * @return
     */
    private CrowdModel.Result error(String resultCd, String resultMsg){
        logger.error("ErrorCode : {}, ErrorMessage : {}", resultCd, resultMsg);
        return new CrowdModel.Result(resultCd, resultMsg, null);
    }

    /**
     * CROWD 정보를 변환.
     * @param user
     * @return
     */
    private CrowdModel.UserInfo convertUserInfo(User user){

        CrowdModel.UserInfo userInfo = new CrowdModel.UserInfo();
        userInfo.setUserId(user.getName());
        userInfo.setEmail(user.getEmailAddress());
        userInfo.setUserName(user.getDisplayName());




        return userInfo;
    }
}
