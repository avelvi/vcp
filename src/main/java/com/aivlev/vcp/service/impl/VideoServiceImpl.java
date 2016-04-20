package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.VideoRepository;
import com.aivlev.vcp.service.ApplicationException;
import com.aivlev.vcp.service.ThumbnailService;
import com.aivlev.vcp.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by aivlev on 4/19/16.
 */
@Service
public class VideoServiceImpl implements VideoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    VideoRepository videoRepository;

    @Value("${media.dir}")
    private String mediaDir;

    @Override
    public ResponseHolder<Page<Video>> findAllVideos(Pageable pageable) {
        return new ResponseHolder<>(videoRepository.findAll(pageable));
    }

    @Override
    public Video processVideo(MultipartFile videoFile) {
        try {
            return processVideoInternal(videoFile);
        } catch (IOException e) {
            throw new ApplicationException("save video failed: " + e.getMessage(), e);
        }
    }

    private Video processVideoInternal(MultipartFile multipartVideoFile) throws IOException {
        String uniqueVideoFileName = generateUniqueVideoFileName();
        Path videoFilePath = saveMultipartFile(multipartVideoFile, uniqueVideoFileName);
        List<String> thumbnails = thumbnailService.createThumbnails(videoFilePath);
        LOGGER.info("new video successful uploaded: {}", videoFilePath.getFileName());
        return new Video(String.valueOf(new Date().getTime()), "/media/video/" + uniqueVideoFileName, thumbnails);
    }

    private Path saveMultipartFile(MultipartFile multipartVideoFile, String uniqueVideFileName) throws IOException {
        Path videoFilePath = Paths.get(mediaDir + "/video/" + uniqueVideFileName);
        multipartVideoFile.transferTo(videoFilePath.toFile());
        return videoFilePath;
    }

    private String generateUniqueVideoFileName() {
        return UUID.randomUUID()+".mp4";
    }
}
