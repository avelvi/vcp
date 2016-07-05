package com.aivlev.vcp.config;

import com.aivlev.vcp.service.*;
import com.aivlev.vcp.service.impl.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by aivlev on 5/26/16.
 */
@Configuration
@EnableMongoRepositories("com.aivlev.vcp.repository.storage")
public class TestConfig {

    @Bean
    public AuthorityService authorityService(){
        return new AuthorityServiceImpl();
    }

    @Bean
    public CategoryService categoryService(){
        return new CategoryServiceImpl();
    }

    @Bean
    public CompanyService companyService(){
        return new CompanyServiceImpl();
    }
}
