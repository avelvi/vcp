package com.aivlev.vcp.service;

import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.model.VideoStatistic;

/**
 * Created by aivlev on 6/24/16.
 */
public interface VideoStatisticService {

    VideoStatistic findByVideoIdAndIpAddress(String videoId, String ipAddress);

    void updateVideoViews(String id, String ipAddress);
}
