package com.example.demo.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.config.ProfileProperties.Datasource;

@Configuration
@EnableConfigurationProperties (ProfileProperties.class)
public class PersistentConfig {

    @Bean
    public DataSource dataSource (ProfileProperties props) {
        Datasource prop = props.getDatasource();
        return DataSourceBuilder.create().driverClassName(prop.getDriverClassName()).url(prop.getUrl()).username(prop.getUsername()).password(prop.getPassword()).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, ProfileProperties properties) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.example.demo.modules");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // properties
        Properties props = new Properties();
        props.putAll(properties.getJpa().getProperties());
        System.out.println("\n\n\n"+properties.getJpa().getProperties());
        emf.setJpaProperties(props);
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf.getObject());
        return txManager;
    }
}
