package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by aivlev on 4/26/16.
 */
@Service
public class ElasticSearchIndexingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchIndexingService.class);

    @Value("${index.all.during.startup}")
    private boolean indexAllDuringStartup;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @PostConstruct
    private void postConstruct(){
        if(indexAllDuringStartup) {
            LOGGER.info("Detected indexAllDuringStartup command");
            LOGGER.info("Clear old index");
            elasticsearchOperations.deleteIndex(Video.class);
            LOGGER.info("Start indexing for videos");
            for(Video v : videoRepository.findAll()){
                videoSearchRepository.save(v);
                LOGGER.info("Successful indexed video: " + v.getVideoUrl());
            }
            LOGGER.info("Finish indexing of videos");
        }
        else{
            LOGGER.info("indexAllDuringStartup is disabled");
        }
    }

}