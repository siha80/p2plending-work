package com.skp.fintech.p2plending.backoffice.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.skp.fintech.p2plending.backoffice.mapper.auth", sqlSessionFactoryRef = "sqlSessionFactoryAuth")
public class DatabaseConfigurationAuth implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    private static Logger logger = LoggerFactory.getLogger(DatabaseConfigurationAuth.class);

    @Bean
    public DataSource dataSourceForMybatisAuth() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("spring.datasource.auth.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("spring.datasource.auth.url"));
        dataSource.setUsername(env.getRequiredProperty("spring.datasource.auth.username"));
        dataSource.setPassword(env.getRequiredProperty("spring.datasource.auth.password"));
        return dataSource;
    }

    @Bean(name = "transactionManagerForMybatisAuth")
    public DataSourceTransactionManager transactionManagerForMybatisAuth() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSourceForMybatisAuth());
        return transactionManager;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryAuth() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSourceForMybatisAuth());
        bean.setConfigLocation (new PathMatchingResourcePatternResolver().getResource("classpath:/mybatis-config.xml"));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/auth/*.xml"));
        bean.setConfigurationProperties(sqlSessionFactoryPropertiesAuth());
        return bean;
    }

    @Bean
    public Properties sqlSessionFactoryPropertiesAuth() {
        Properties properties = new Properties();
        properties.setProperty("cacheEnabled", "false");
        properties.setProperty("lazyLoadingEnabled", "false");
        properties.setProperty("jdbcTypeForNull", "NULL");
        properties.setProperty("localCacheScope", "STATEMENT");
        return properties;
    }

    @Bean
    public SqlSessionTemplate sqlSessionAuth() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryAuth().getObject());
    }

}
