package com.skp.payment.p2plending.launcher.infra.log;

import ch.qos.logback.classic.PatternLayout;

/**
 * Logback 에 사용자 Context 정보 추가를 위한 PatternLayout.
 *
 * @author 임형태
 * @since 2015.07.02
 */
public class PatternLayoutWithUserContext extends PatternLayout {
    static {
        PatternLayout.defaultConverterMap.put("session", SessionConverter.class.getName());
    }
}
