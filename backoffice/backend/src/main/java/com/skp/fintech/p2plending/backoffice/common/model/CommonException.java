package com.skp.fintech.p2plending.backoffice.common.model;

import lombok.Data;

@Data
public class CommonException extends Exception {

    private String errorCode;
    private String errorMessage;

    public CommonException(String errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
