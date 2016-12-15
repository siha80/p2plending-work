package com.skp.fintech.p2plending.backoffice.infra.token;

import com.skplanet.syruppay.token.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TokenHandler {
    public static final String ISSUER = "P2PLENDING";

    @Value("${backoffice.auth.token.key}")
    private String TOKEN_KEY;

    public Token getToken(final String authorization) {
        Token t;
        try {
            t = decryptAuthorizationHeader(authorization, TOKEN_KEY);
            if (t != null && t.isValidInTime()) {
                log.info("get user token from header");
                return t;
            }
        } catch (IOException | InvalidTokenException e) {
            log.error("fail user token initialize.", e);
        }
        return null;
    }

    public Token decryptAuthorizationHeader(final String token, final String tokenKey) throws IOException, InvalidTokenException {
        if(token != null) {
            return Token.decrypt(token.split(" ")[1], tokenKey);
        }
        return null;
    }
}
