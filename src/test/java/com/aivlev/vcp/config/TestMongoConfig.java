package com.aivlev.vcp.config;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.*;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by aivlev on 6/1/16.
 */
@Configuration
@EnableMongoRepositories("com.aivlev.vcp.repository.storage")
public class TestMongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String dbHost;

    @Value("${spring.data.mongodb.port}")
    private int dbPort;

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(dbPort, Network.localhostIsIPv6()))
                .build();
        return mongodConfig;
    }

    @Bean(destroyMethod = "stop")
    public MongodExecutable mongodExecutable() throws IOException {
        return MongodStarter.getDefaultInstance().prepare(mongodConfig());
    }

    @Bean(destroyMethod = "stop")
    public MongodProcess mongodProcess() throws IOException {
        MongodProcess mongodProcess =  mongodExecutable().start();
        return mongodProcess;
    }

    @Bean
    public MongoClient mongoClient() throws UnknownHostException {
        return new MongoClient(dbHost, dbPort);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(mongoClient(), dbName);
    }

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer(){
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocations(new Resource[] {new ClassPathResource("/application.properties")});
        return configurer;
    }
}