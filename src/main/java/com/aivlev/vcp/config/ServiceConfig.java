package com.aivlev.vcp.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Created by aivlev on 4/19/16.
 */
@Configuration
@ComponentScan({ "com.aivlev.vcp.service.impl", "com.aivlev.vcp.aop"})
@EnableAspectJAutoProxy
public class ServiceConfig {

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer(){
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocations(new Resource[] {new ClassPathResource("application.properties")});
        return configurer;
    }

}
