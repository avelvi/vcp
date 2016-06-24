package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.VideoStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by aivlev on 6/24/16.
 */
public interface VideoStatisticRepository extends MongoRepository<VideoStatistic, String> {

    VideoStatistic findByVideoIdAndIpAddress(String videoId, String ipAddress);
}
