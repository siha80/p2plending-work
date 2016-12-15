package com.skp.fintech.p2plending.backoffice.config;

import com.skp.fintech.p2plending.backoffice.infra.token.TokenHandler;
import com.skp.fintech.p2plending.backoffice.infra.filter.AuthorizationFilter;
import com.skp.fintech.p2plending.backoffice.infra.resolver.UserTokenArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import java.util.List;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private Environment environment;

    private TokenHandler tokenHalder;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/unauthorized").setViewName("/error");
        registry.addViewController("/forbidden").setViewName("/error");
        registry.addViewController("/not-found").setViewName("/error");
        registry.addViewController("/internal-server-error").setViewName("/error");
        registry.addViewController("/service-unavailable").setViewName("/error");
        registry.addViewController("/method-not-allowed").setViewName("/error");
    }

    @Bean
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

    @Value("${backoffice.front.server}")
    private String BACKOFFICE_FRONT_SERVER;

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(BACKOFFICE_FRONT_SERVER);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}

