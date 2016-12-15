package com.skp.payment.p2plending.backoffice.idms;

import lombok.Data;

@Data
public class IdmsModel {

	@Data
	public static class RequestIdms {

		private String userId;
	}

	@Data
	public static class ResponseIdms {
		private String resultCd;
		private String resultMsg;
	}

}
