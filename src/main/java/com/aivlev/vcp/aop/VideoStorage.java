package com.aivlev.vcp.aop;

import com.aivlev.vcp.model.Video;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aivlev on 7/6/16.
 */
@Aspect
@Component
public class VideoStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoStorage.class);

    @Value("${media.dir}")
    private String mediaDir;

    @Around("execution(* com.aivlev.vcp.repository.storage.VideoRepository.delete(..))")
    public void advice(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Video video = (Video) pjp.getArgs()[0];
            Files.deleteIfExists(Paths.get(mediaDir.substring(0, mediaDir.length() - 6) + video.getVideoUrl()));
            Files.deleteIfExists(Paths.get(mediaDir.substring(0, mediaDir.length() - 6) + video.getThumbnails().get(0)));
        } catch (Exception ex){
            LOGGER.error("Error has occurred while deleting video from storage", ex);
        }
    }
}
