package com.skp.payment.p2plending.launcher.infra.security.filter;

import com.skp.payment.p2plending.launcher.domain.UserAuthenticationToken;
import com.skp.payment.p2plending.launcher.domain.TokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthorizationFilter extends GenericFilterBean {
    public static final String USER_TOKEN_NAME_IN_EXTENSION_HEADER = "Authorization";
    private static final String TOKEN_KEY = "12345678123456781234567812345678";

    @Autowired
    TokenHandler tokenHandler;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        if (!((HttpServletRequest) req).getRequestURI().contains("pages/")) {
            chain.doFilter(request, response);
            return;
        }

        log.info("UserAuthenticationToken deserialize...");
        UserAuthenticationToken userAuthenticationToken = tokenHandler.getToken(((HttpServletRequest) req).getHeader(USER_TOKEN_NAME_IN_EXTENSION_HEADER), TOKEN_KEY);
        if(userAuthenticationToken != null && userAuthenticationToken.isValidInTime()) {
            chain.doFilter(request, response);
            return;
        }

        log.info("userAuthenticationToken error...");
        response.sendError(HttpStatus.FORBIDDEN.value(), "UserAuthenticationToken is invalid");
        return;
    }
}
