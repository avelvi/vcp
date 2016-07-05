package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.model.VideoStatistic;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.repository.storage.VideoStatisticRepository;
import com.aivlev.vcp.service.VideoStatisticService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/30/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class VideoStatisticServiceImplTest {

    @Autowired
    private VideoStatisticService videoStatisticService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoStatisticRepository videoStatisticRepository;

    @Mock
    private VideoStatistic videoStatistic;

    @Mock
    private Video video;

    private String videoId = "id";

    private String ipAddress = "127.0.0.1";

    @Before
    public void setUp() throws Exception {
        reset(videoRepository);
        reset(videoStatisticRepository);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByVideoIdAndIpAddress() throws Exception {
        when(videoStatisticRepository.findByVideoIdAndIpAddress(anyString(), anyString())).thenReturn(videoStatistic);
        videoStatisticService.findByVideoIdAndIpAddress(anyString(), anyString());
        verify(videoStatisticRepository).findByVideoIdAndIpAddress(anyString(), anyString());
    }

    @Test
    public void testUpdateVideoViews() throws Exception {
        when(videoRepository.findOne(videoId)).thenReturn(video);
        when(videoStatisticRepository.findByVideoIdAndIpAddress(videoId, ipAddress)).thenReturn(null);
        when(video.getId()).thenReturn(videoId);
        when(video.getViews()).thenReturn(anyInt());
        videoStatisticService.updateVideoViews(videoId, ipAddress);
        verify(videoStatisticRepository).save(any(VideoStatistic.class));
        verify(videoRepository).save(video);
    }
}