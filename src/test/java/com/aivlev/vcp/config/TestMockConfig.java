package com.aivlev.vcp.config;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.*;
import com.aivlev.vcp.service.*;
import com.aivlev.vcp.service.impl.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by aivlev on 6/9/16.
 */
@Configuration
public class TestMockConfig {

    @Bean
    public AdminService adminService(){
        return new AdminServiceImpl();
    }

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

    @Bean
    public VideoService videoService(){
        return new VideoServiceImpl();
    }

    @Bean
    public UserService userService(){
        return new UserServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public NotificationService notificationService(){
        return new NotificationServiceImpl();
    }

    @Bean
    public VideoProcessorService videoProcessorService(){
        return new VideoProcessorServiceImpl();
    }

    @Bean
    public UploadVideoTempStorage uploadVideoTempStorage(){
        return Mockito.mock(UploadVideoTempStorage.class);
    }

    @Bean
    public ImageService imageService(){
        return new FileStorageImageService();
    }

    @Bean
    public ThumbnailService thumbnailService(){
        return new JCodecThumbnailService();
    }

    @Bean
    public ForgotPasswordService forgotPasswordService(){
        return new ForgotPasswordServiceImpl();
    }

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer(){
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocations(new Resource[] {new ClassPathResource("/application.properties")});
        return configurer;
    }

    @Bean
    public AuthorityRepository authorityRepository(){
        return Mockito.mock(AuthorityRepository.class);
    }

    @Bean
    public CategoryRepository categoryRepository(){
        return Mockito.mock(CategoryRepository.class);
    }

    @Bean
    public CompanyRepository companyRepository(){
        return Mockito.mock(CompanyRepository.class);
    }

    @Bean
    public VideoRepository videoRepository(){
        return Mockito.mock(VideoRepository.class);
    }

    @Bean
    public VideoSearchRepository videoSearchRepository(){
        return Mockito.mock(VideoSearchRepository.class);
    }

    @Bean
    public UserRepository userRepository(){
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public JavaMailSender javaMailSender(){
        return Mockito.mock(JavaMailSender.class);
    }

}
