package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.ImageService;
import com.aivlev.vcp.service.ThumbnailService;
import com.aivlev.vcp.service.VideoProcessorService;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;

/**
 * Created by aivlev on 5/18/16.
 */
@Service
public class VideoProcessorServiceImpl implements VideoProcessorService {

    @Autowired
    private UploadVideoTempStorage uploadVideoTempStorage;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private VideoService videoService;

    @Override
    public Video processVideo(UploadForm uploadForm) {
        Path tempUploadedVideoPath = uploadVideoTempStorage.getTempUploadedVideoPath();
        String videoUrl = videoService.saveVideo(tempUploadedVideoPath);
        byte[] thumbnailImageData = thumbnailService.createThumbnail(tempUploadedVideoPath);
        String thumbnailImageUrl = imageService.saveImageData(thumbnailImageData);

        return new Video(uploadForm.getTitle(), uploadForm.getDescription(),
                String.valueOf(new Date().getTime()), videoUrl, Collections.singletonList(thumbnailImageUrl));
    }
}
