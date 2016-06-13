package com.aivlev.vcp.config;

import com.aivlev.vcp.service.AdminService;
import com.aivlev.vcp.service.AuthorityService;
import com.aivlev.vcp.service.CategoryService;
import com.aivlev.vcp.service.CompanyService;
import com.aivlev.vcp.service.impl.AdminServiceImpl;
import com.aivlev.vcp.service.impl.AuthorityServiceImpl;
import com.aivlev.vcp.service.impl.CategoryServiceImpl;
import com.aivlev.vcp.service.impl.CompanyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by aivlev on 5/26/16.
 */
@Configuration
//@ComponentScan({"com.aivlev.vcp.aop"})
//@EnableAspectJAutoProxy
@EnableMongoRepositories("com.aivlev.vcp.repository.storage")
//@EnableElasticsearchRepositories("com.aivlev.vcp.repository.search")
public class TestConfig {

//    @Value("${elasticsearch.home}")
//    private String elasticSearchHome;
//
//    @Bean
//    public Node node(){
//        return new NodeBuilder()
//                .local(true)
//                .settings(Settings.builder().put("path.home", elasticSearchHome))
//                .node();
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchTemplate(node().client());
//    }

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
    public AdminService adminService(){
        return new AdminServiceImpl();
    }

//    @Bean
//    public UserService userService(){
//        return new UserServiceImpl();
//    }
//
//    @Bean
//    public NotificationService notificationService(){
//        return Mockito.mock(NotificationServiceImpl.class);
//    }
//
//    @Bean
//    public VideoProcessorService videoProcessorService(){
//        return new VideoProcessorServiceImpl();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public JavaMailSender javaMailSender(){
//        return Mockito.mock(JavaMailSender.class);
//    }
//
//    @Bean
//    public UploadVideoTempStorage uploadVideoTempStorage(){
//        return new UploadVideoTempStorage();
//    }
//
//    @Bean
//    public ThumbnailService thumbnailService(){
//        return new JCodecThumbnailService();
//    }
//
//    @Bean
//    public ImageService imageService(){
//        return new FileStorageImageService();
//    }
//
//    @Bean
//    public TestDataGeneration testDataGeneration(){
//        return new TestDataGeneration();
//    }
//
//    @Bean
//    public VideoService videoService(){
//        return new VideoServiceImpl();
//    }
}
