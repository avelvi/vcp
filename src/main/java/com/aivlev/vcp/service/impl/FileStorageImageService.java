package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by aivlev on 4/26/16.
 */
@Service
public class FileStorageImageService implements ImageService {

    private static final String THUMBNAIL_PATH_PREFIX = "/media/thumbnails/";
    private static final String AVATAR_PATH_PREFIX = "/media/avatars/";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageImageService.class);

    @Value("${media.dir}")
    private String mediaDir;

    @Override
    public String saveImageData(byte[] imageBytes, boolean isThumbnail) {
        try {
            return saveImageDataInternal(imageBytes, isThumbnail);
        } catch (IOException e) {
            LOGGER.error("Error has occurred while saving image data", e.getMessage());
            throw new ProcessMediaContentException("Error has occurred while saving image data: " + e.getMessage(), e);
        }
    }

    private String saveImageDataInternal(byte[] imageBytes, boolean isThumbnail) throws IOException {
        String uniqueThumbnailFileName = generateUniqueThumbnailFileName();
        String fileSubdirPath = isThumbnail ? "/thumbnails/" : "/avatars/";
        Path path = Paths.get(mediaDir + fileSubdirPath + uniqueThumbnailFileName);
        Files.write(path, imageBytes);
        String filePathPrefix = isThumbnail ? THUMBNAIL_PATH_PREFIX : AVATAR_PATH_PREFIX;
        return filePathPrefix + uniqueThumbnailFileName;
    }

    private String generateUniqueThumbnailFileName() {
        return UUID.randomUUID() + ".jpg";
    }
}