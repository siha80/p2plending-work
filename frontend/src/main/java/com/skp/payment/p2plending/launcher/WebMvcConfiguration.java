package com.skp.payment.p2plending.launcher;

import com.skp.payment.p2plending.launcher.domain.TokenHandler;
import com.skp.payment.p2plending.launcher.infra.resolver.UserTokenArgumentResolver;
import com.skp.payment.p2plending.launcher.infra.security.filter.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import java.util.List;

/**
 * Web 플랫폼의 MVC 영역에 대한 환경을 설정하고 관리한다.
 * <p>
 * 정적 자원에 대한 라우팅 경로를 설정하여 개발에 대한 편의성을 관리하며 웹 환경에서의 home, about 과 같은 Default 를 설정하고 관리한다.
 *
 * @author 임형태
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
 * @since 2014.12.12
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private Environment environment;
    @Autowired
    private TokenHandler tokenHalder;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/unauthorized").setViewName("/error");
        registry.addViewController("/forbidden").setViewName("/error");
        registry.addViewController("/not-found").setViewName("/error");
        registry.addViewController("/internal-server-error").setViewName("/error");
        registry.addViewController("/service-unavailable").setViewName("/error");
        registry.addViewController("/method-not-allowed").setViewName("/error");

        registry.addViewController("/").setViewName("/syruppay");
        registry.addViewController("/about").setViewName("/syruppay");
        registry.addViewController("/home").setViewName("/syruppay");
    }

    @Bean
    @Autowired
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserTokenArgumentResolver(tokenHalder));
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}

