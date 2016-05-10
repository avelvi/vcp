package com.aivlev.vcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Created by aivlev on 4/19/16.
 */
@Configuration
@ComponentScan({ "com.aivlev.vcp.service.impl", "com.aivlev.vcp.aop"})
@EnableAspectJAutoProxy
@PropertySource("classpath:/application.properties")
public class ServiceConfig {

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer(){
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocations(new Resource[] {new ClassPathResource("application.properties")});
        return configurer;
    }

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(environment.getRequiredProperty("email.smtp.host"));
        if(environment.containsProperty("email.smtp.username")){
            javaMailSender.setUsername(environment.resolveRequiredPlaceholders(environment.getRequiredProperty("email.smtp.username")));
            javaMailSender.setPassword(environment.resolveRequiredPlaceholders(environment.getRequiredProperty("email.smtp.password")));
            javaMailSender.setPort(Integer.parseInt(environment.getRequiredProperty("email.smtp.port")));
            javaMailSender.setDefaultEncoding("UTF-8");
            javaMailSender.setJavaMailProperties(javaMailProperties());
        }
        return javaMailSender;
    }

    private Properties javaMailProperties(){
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", "true");
        return p;
    }

}
