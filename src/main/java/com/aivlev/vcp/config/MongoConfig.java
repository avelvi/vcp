package com.aivlev.vcp.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

/**
 * Created by aivlev on 4/19/16.
 */

@Configuration
@EnableMongoRepositories("com.aivlev.vcp.repository.storage")
public class MongoConfig {

    @Value("${db.host}")
    private String dbHost;

    @Value("${db.port}")
    private int dbPort;


    @Bean
    public MongoClient mongoClient() throws UnknownHostException {
        return new MongoClient(dbHost, dbPort);
    }

    @Bean
    public MongoTemplate mongoTemplate(@Value("${db.name}") String dbName) throws UnknownHostException {
        return new MongoTemplate(mongoClient(), dbName);
    }
}
