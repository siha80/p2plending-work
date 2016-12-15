package com.skp.fintech.p2plending.backoffice.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.skp.fintech.p2plending.backoffice.mapper.plpdb", sqlSessionFactoryRef = "sqlSessionFactoryPlpdb")
public class DatabaseConfigurationPlpdb implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    private static Logger logger = LoggerFactory.getLogger(DatabaseConfigurationPlpdb.class);

    @Bean
    @Primary
    public DataSource dataSourceForMybatisPlpdb() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("spring.datasource.plpdb.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("spring.datasource.plpdb.url"));
        dataSource.setUsername(env.getRequiredProperty("spring.datasource.plpdb.username"));
        dataSource.setPassword(env.getRequiredProperty("spring.datasource.plpdb.password"));
        return dataSource;
    }

    @Bean(name = "transactionManagerForMybatisPlpdb")
    @Primary
    public DataSourceTransactionManager transactionManagerForMybatisPlpdb() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSourceForMybatisPlpdb());
        return transactionManager;
    }

    @Bean
    @Primary
    public SqlSessionFactoryBean sqlSessionFactoryPlpdb() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSourceForMybatisPlpdb());
        bean.setConfigLocation (new PathMatchingResourcePatternResolver().getResource("classpath:/mybatis-config.xml"));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/plpdb/*.xml"));
        bean.setConfigurationProperties(sqlSessionFactoryPropertiesPlpdb());
        return bean;
    }

    @Bean
    @Primary
    public Properties sqlSessionFactoryPropertiesPlpdb() {
        Properties properties = new Properties();
        properties.setProperty("cacheEnabled", "false");
        properties.setProperty("lazyLoadingEnabled", "false");
        properties.setProperty("jdbcTypeForNull", "NULL");
        properties.setProperty("localCacheScope", "STATEMENT");
        return properties;
    }

    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionPlpdb() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryPlpdb().getObject());
    }

}
