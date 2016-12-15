package com.skp.payment.p2plending.backoffice.idms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.payment.p2plending.backoffice.common.HttpClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.http.conn.ConnectTimeoutException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IdmsService {

	private static Logger logger = LoggerFactory.getLogger(IdmsService.class);

	@Value("${external.idms.url}")
	private String IDMS_URL;

	@Value("${external.idms.sysid}")
	private String IDMS_SYSID;

	@Autowired
	private HttpClientService httpClientService;

	@Autowired
	private ObjectMapper objectMapper;

	public IdmsModel.ResponseIdms authenticate(IdmsModel.RequestIdms request)  {

		IdmsModel.ResponseIdms response = new IdmsModel.ResponseIdms();

		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("sysId", IDMS_SYSID);
			paramMap.put("loginId", request.getUserId());
			paramMap.put("clntIp", HttpClientService.getRequest().getRemoteAddr());

			Map<?, ?> map = objectMapper.readValue(httpClientService.doGetAsString(IDMS_URL, paramMap, ""), Map.class);
			response.setResultCd(String.valueOf(map.get("RESULT_CD")));
			response.setResultMsg(String.valueOf(map.get("RESULT_MSG")));

		} catch(ConnectTimeoutException e) {
			logger.error(e.getMessage());
			response.setResultCd("8002");
			response.setResultMsg("IDMS 시스템 연결 오류");

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setResultCd("8001");
			response.setResultMsg("IDMS 시스템 오류");
		}

		return response;
	}
}
