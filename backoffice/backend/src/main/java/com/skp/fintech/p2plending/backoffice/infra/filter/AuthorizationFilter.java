package com.skp.fintech.p2plending.backoffice.infra.filter;

import com.skp.fintech.p2plending.backoffice.infra.token.Token;
import com.skp.fintech.p2plending.backoffice.infra.token.TokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    TokenHandler tokenHandler;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        if (((HttpServletRequest) req).getRequestURI().contains("users/login")
                || ((HttpServletRequest) req).getRequestURI().contains("users/add")) {
            chain.doFilter(request, response);
            return;
        }

        log.info("Token deserialize...");
        Token token = tokenHandler.getToken(((HttpServletRequest) req).getHeader(USER_TOKEN_NAME_IN_EXTENSION_HEADER));
        if(token != null && token.isValidInTime()) {
            chain.doFilter(request, response);
            return;
        }

        log.info("token error...");
        response.sendError(HttpStatus.FORBIDDEN.value(), "Token is invalid");
        return;
    }
}
