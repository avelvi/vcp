package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.model.VideoStatistic;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.repository.storage.VideoStatisticRepository;
import com.aivlev.vcp.service.VideoStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by aivlev on 6/24/16.
 */
@Service
@Transactional
public class VideoStatisticServiceImpl implements VideoStatisticService {

    @Autowired
    private VideoStatisticRepository videoStatisticRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public VideoStatistic findByVideoIdAndIpAddress(String videoId, String ipAddress) {
        return videoStatisticRepository.findByVideoIdAndIpAddress(videoId, ipAddress);
    }

    @Override
    public void updateVideoViews(String id, String ipAddress) {
        Video videoFromDb = videoRepository.findOne(id);
        if(videoFromDb != null){
            VideoStatistic videoStatistic = videoStatisticRepository.findByVideoIdAndIpAddress(id, ipAddress);
            if(videoStatistic == null){
                videoStatistic = new VideoStatistic(videoFromDb.getId(), ipAddress);
                videoStatisticRepository.save(videoStatistic);
                videoFromDb.setViews(videoFromDb.getViews() + 1);
                videoRepository.save(videoFromDb);
            }
        }
    }
}
