package com.skp.payment.p2plending.user.ui.event.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by skplanet on 2014-12-15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class P2pLendingErrorDetail implements Serializable {
    private static final long serialVersionUID = 6991430857880276989L;
    private String field;
    private String code;
    private String message;
    private String exceptionName;
}

