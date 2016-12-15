package com.skp.payment.p2plending.launcher.domain;

import com.skplanet.syruppay.token.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TokenHandler {
    public static final String ISSUER = "P2PLENDING";

    public UserAuthenticationToken getToken(final String authorization, final String tokenKey) {
        UserAuthenticationToken t;
        try {
            t = decryptAuthorizationHeader(authorization, tokenKey);
            if (t != null && t.isValidInTime()) {
                log.info("get user token from header");
                return t;
            }
        } catch (IOException | InvalidTokenException e) {
            log.error("fail user token initialize.", e);
        }
        return null;
    }

    public UserAuthenticationToken decryptAuthorizationHeader(final String token, final String tokenKey) throws IOException, InvalidTokenException {
        if(token != null) {
            return UserAuthenticationToken.decrypt(token.split(" ")[1], tokenKey);
        }
        return null;
    }
}
