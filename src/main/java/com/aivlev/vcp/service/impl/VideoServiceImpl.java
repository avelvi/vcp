package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.AccessDeniedException;
import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by aivlev on 4/19/16.
 */
@Service
public class VideoServiceImpl implements VideoService {

    private static final String PATH_PREFIX = "/media/video/";

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserService userService;

    @Value("${media.dir}")
    private String mediaDir;

    @Override
    public Video findOne(String id) {
        return videoRepository.findOne(id);
    }

    @Override
    public String saveVideo(Path tempFilePath) {
        try {
            return saveVideoInternal(tempFilePath);
        } catch (IOException e) {
            LOGGER.error("Error has occurred while saving video", e.getMessage());
            throw new ProcessMediaContentException("Error has occurred while saving video: " + e.getMessage(), e);
        }
    }

    @Nonnull
    @Override
    public ResponseHolder<Page<Video>> findAllVideosByOwnerId(@Nonnull String ownerId, @Nonnull Pageable pageable) {
        return new ResponseHolder<>(videoRepository.findByOwnerId(ownerId, pageable));
    }

    @Override
    public Page<Video> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    @Override
    public void deleteVideo(boolean isAdmin, String userName, String id) {
        User user = userService.findByLogin(userName);
        if(null != user){
            if(isAdmin){
                videoRepository.delete(id);
            } else {
                Video video = videoRepository.findOne(id);
                if(video.getOwner().getId().equals(user.getId())){
                    videoRepository.delete(id);
                } else {
                    throw new AccessDeniedException("Sorry, but you don't have permissions.");
                }
            }
        }
    }

    @Override
    public Video updateVideo(Video video) {
        return videoRepository.save(video);
    }

    private String saveVideoInternal(Path tempFilePath) throws IOException {
        String uniqueVideoFileName = generateUniqueVideoFileName();
        Path videoFilePath = Paths.get(mediaDir + "/video/" + uniqueVideoFileName);
        Files.copy(tempFilePath, videoFilePath);
        return PATH_PREFIX + uniqueVideoFileName;
    }

    private String generateUniqueVideoFileName() {
        return UUID.randomUUID()+".mp4";
    }


}
