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

    private static final String PATH_PREFIX = "/media/thumbnails/";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageImageService.class);

    @Value("${media.dir}")
    private String mediaDir;

    @Override
    public String saveImageData(byte[] imageBytes) {
        try {
            return saveImageDataInternal(imageBytes);
        } catch (IOException e) {
            LOGGER.error("Error has occurred while saving image data", e.getMessage());
            throw new ProcessMediaContentException("Error has occurred while saving image data: " + e.getMessage(), e);
        }
    }

    private String saveImageDataInternal(byte[] imageBytes) throws IOException {
        String uniqueThumbnailFileName = generateUniqueThumbnailFileName();
        Path path = Paths.get(mediaDir + "/thumbnails/" + uniqueThumbnailFileName);
        Files.write(path, imageBytes);
        return PATH_PREFIX + uniqueThumbnailFileName;
    }

    private String generateUniqueThumbnailFileName() {
        return UUID.randomUUID() + ".jpg";
    }
}