package com.skp.fintech.p2plending.backoffice.user.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.StringUtils;
import com.skp.fintech.p2plending.backoffice.common.model.CommonException;
import com.skp.fintech.p2plending.backoffice.common.util.HttpClientManager;
import com.skp.fintech.p2plending.backoffice.infra.token.Token;
import com.skp.fintech.p2plending.backoffice.mapper.plpdb.UserMapper;
import com.skp.fintech.p2plending.backoffice.user.ui.LoginEvent;
import com.sun.jersey.api.client.ClientResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserEventHandler implements UserService {

    @Value("${backoffice.auth.token.key}")
    private String TOKEN_KEY;

    @Value("${backoffice.auth.server}")
    private String AUTH_SERVER;

    private static Logger logger = LoggerFactory.getLogger(UserEventHandler.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public LoginEvent.Response login(LoginEvent.Request request)  {
        LoginEvent.Response response = new LoginEvent.Response();

        try{

            if("test1".equals((request.getUserId()))){
                response.setResultCd("0000");
                response.setResultMsg("테스트계정");
                response.setToken(new Token(request.getUserId()).toString(TOKEN_KEY));
                response.setUserName(request.getUserId() + " / 테스트계정");
                return response;
            }

            /* CROWD 를 이용한 사용자 인증 및 정보 조회. */
            LoginEvent.UserInfo userInfo = getUserInfoFromCrowd(request);
            if(userInfo == null){
                throw new CommonException("9009", "CROWD 인증 실패");
            }

            /* IDSM 인증 */
            if(!isAuthIDMS(userInfo)){
                throw new CommonException("9008", "IDMS 인증 실패");
            }

            /* P2P Lending Backoffice 사용자로 등록 되어 있는지 확인. */
            if(!isBackofficeUser(userInfo)){
                throw new CommonException("9007", "Backoffice 인증 실패");
            }

            response.setResultCd("0000");
            response.setResultMsg("로그인 인증 성공");
            response.setToken(new Token(userInfo.getUserId()).toString(TOKEN_KEY));
            response.setUserName(userInfo.getUserName());

        } catch(CommonException e) {
            response.setResultCd(e.getErrorCode());
            response.setResultMsg(e.getErrorMessage());
        } catch(Exception e){
            e.printStackTrace();
            response.setResultCd("9999");
            response.setResultMsg("인증실패. 시스템 관리자에게 문의 바랍니다.");
        }

        return response;
    }

    /**
     * CROWD 인증 요청 및 사용자 정보 조회.
     * @param request
     * @return
     * @throws Exception
     */
    public LoginEvent.UserInfo getUserInfoFromCrowd(LoginEvent.Request request) throws CommonException{

        LoginEvent.UserInfo userInfo = null;
        LoginEvent.AuthenticationResponse result = null;

        try{
            HttpClientManager httpClientManager = new HttpClientManager();
            ClientResponse r = httpClientManager.postRawData(AUTH_SERVER+"crowd/"+request.getUserId()+"/authenticate", objectMapper.writeValueAsString(request));
            result = objectMapper.readValue(r.getEntity(String.class), LoginEvent.AuthenticationResponse.class);
        } catch(Exception e){
            logger.error("Crowd Error <FAILED> : {}" , e.getMessage());
            throw new CommonException(LoginEvent.LoginErrors.FAILED.toString(), "로그인 인증실패(timeout).");
        }

        if(result!=null){
            if("0000".equals(result.getResultCd())){
                Map resultData = result.getResultData();
                userInfo = modelMapper.map(resultData.get("userInfo"), LoginEvent.UserInfo.class);
            } else {
                logger.error("Crowd Error <Code, Message>: {} : {}" , result.getResultCd(), result.getResultMsg());
                throw new CommonException(result.getResultCd(), result.getResultMsg());
            }
        } else {
            logger.error("Crowd Error <Code, Message>: NULL");
            throw new CommonException(LoginEvent.LoginErrors.FAILED.toString(), "로그인 인증실패.");
        }

        return userInfo;
    }

    public boolean isAuthIDMS(LoginEvent.UserInfo userInfo) throws CommonException{

        boolean result = false;
        LoginEvent.AuthenticationResponse idmsResult = null;

        try{
            HttpClientManager httpClientManager = new HttpClientManager();
            ClientResponse r = httpClientManager.postRawData(AUTH_SERVER+"idms/"+userInfo.getUserId()+"/authenticate", objectMapper.writeValueAsString(userInfo));
            idmsResult = objectMapper.readValue(r.getEntity(String.class), LoginEvent.AuthenticationResponse.class);
        } catch(Exception e){
            logger.error("IDSM Error <FAILED> : {}" , e.getMessage());
            throw new CommonException(LoginEvent.LoginErrors.FAILED.toString(), "IDSM 인증실패(timeout).");
        }

        if(idmsResult!=null){
            if("E0".equals(idmsResult.getResultCd())){
                result = true;
            } else {
                logger.error("IDSM Error <Code, Message>: {} : {}" , idmsResult.getResultCd(), idmsResult.getResultMsg());
                throw new CommonException(idmsResult.getResultCd(), idmsResult.getResultMsg());
            }
        } else {
            logger.error("IDMS Error <Code, Message>: NULL");
            throw new CommonException(LoginEvent.LoginErrors.FAILED.toString(), "IDMS 인증실패.");
        }

        return result;
    }


    public boolean isBackofficeUser(LoginEvent.UserInfo userInfo) throws CommonException{
        boolean result = true;

        UserInformation userInformation = userMapper.selectLoginUserInfo(userInfo.getUserId());

        if(userInformation==null) {
            throw new CommonException("7001", "미등록 사용자 입니다.");
        }

        if(!"Y".equals(userInformation.getStatus())){

            if("N".equals(userInformation.getStatus())){
                throw new CommonException("7002", "관리자 승인 대기 중 입니다. ");
            } else {
                throw new CommonException("7003", "기간 만료 사용자 입니다.");
            }
        }

        if(StringUtils.isNullOrEmpty(userInformation.getAuthId())){
            throw new CommonException("7004", "권한 그룹이 설정되지 않았습니다.");
        }

        return result;
    }


    public LoginEvent.AddUserResponse addUser(LoginEvent.Request request) {

        LoginEvent.AddUserResponse reponse = new LoginEvent.AddUserResponse();

        UserInformation userInformation = new UserInformation();
        userInformation.setUserId(request.getUserId());
        userInformation.setStatus("N");

        boolean isSuccess = userMapper.insertUser(userInformation);

        if(isSuccess){
            reponse.setResultCd("0000");
            reponse.setResultMsg("등록 신청이 완료 되었습니다.");
        } else {
            reponse.setResultCd("9999");
            reponse.setResultCd("등록 신청 중 오류가 발생 하였습니다. 관리자에게 문의 부탁 드립니다.");
        }

        return reponse;
    }
}

