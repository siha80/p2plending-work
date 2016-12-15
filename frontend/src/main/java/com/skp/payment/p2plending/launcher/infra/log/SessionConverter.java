package com.skp.payment.p2plending.launcher.infra.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 세션에 대한 ID 를 추출한다.
 *
 * @author 임형태
 * @since 2015.07.02
 */
public class SessionConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getSessionId();
        }
        return "SESSION";
    }
}
