package com.skp.fintech.p2plending.backoffice.infra.resolver;

import com.skp.fintech.p2plending.backoffice.infra.token.Token;
import com.skp.fintech.p2plending.backoffice.infra.token.TokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * {@link com.skplanet.syruppay.token.jwt.SyrupPayToken}을 Controller 에 DI 하기 위한 Resolver
 *
 * @author 임형태
 * @since 2015.05.12
 */
@Slf4j
public class UserTokenArgumentResolver implements HandlerMethodArgumentResolver {
    public static final String USER_TOKEN_NAME_IN_EXTENSION_HEADER = "Authorization";

    TokenHandler tokenHandler;

    public UserTokenArgumentResolver(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Token.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            return Optional.ofNullable(tokenHandler.getToken(webRequest.getHeader(USER_TOKEN_NAME_IN_EXTENSION_HEADER)))
                    .orElseGet(() -> {
                        log.warn("{} class couldn't find syrup pay token. request : {}, method : {}", this.getClass().getSimpleName(), webRequest, parameter);
                        return null;
                    });
        } catch (Exception e) {
            log.error("fail syrup pay token initialize. request : {}, method : {}, cause : {}", webRequest, parameter, e);
        }
        return null;
    }


}
