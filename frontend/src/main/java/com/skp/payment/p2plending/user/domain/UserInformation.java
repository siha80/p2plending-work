package com.skp.payment.p2plending.user.domain;

import lombok.Data;

@Data
public class UserInformation {
    private final String userSerialNumber;
    private final String email;
    private final String userName;
}
