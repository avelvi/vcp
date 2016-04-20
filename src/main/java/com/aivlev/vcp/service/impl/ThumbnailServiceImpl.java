package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.service.ApplicationException;
import com.aivlev.vcp.service.ThumbnailService;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by aivlev on 4/20/16.
 */
@Service
public class ThumbnailServiceImpl implements ThumbnailService{
    private static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailServiceImpl.class);

    @Value("${media.dir}")
    private String mediaDir;

    @Override
    public List<String> createThumbnails(Path videoFilePath) {
        try {
            return createThumbnailsInternal(videoFilePath);
        } catch (IOException | JCodecException e) {
            throw new ApplicationException("create thumbnails failed: " + e.getMessage(), e);
        }
    }

    private List<String> createThumbnailsInternal(Path videoFilePath) throws IOException, JCodecException {
        String thumbnail = createThumbnail(videoFilePath);
        List<String> thumbnails = Collections.singletonList(thumbnail);
        LOGGER.info("Created {} thumbnails for video {}", thumbnails.size(), videoFilePath.getFileName());
        return thumbnails;
    }

    private String createThumbnail(Path videoFilePath) throws IOException, JCodecException {
        Picture nativeFrame = getVideoFrameBySecondPrecise(videoFilePath, 0);
        if (nativeFrame == null) {
            throw new ApplicationException("Can't create thumbnail for file: " + videoFilePath.getFileName());
        }
        String uniquieThumbnailFileName = generateUniqueThumbnailFileName();
        savePictureToDisk(nativeFrame, uniquieThumbnailFileName);
        return "/media/thumbnails/" + uniquieThumbnailFileName;
    }

    private Picture getVideoFrameBySecondPrecise(Path videoFilePath, double secondPrecise) throws IOException, JCodecException {
        FrameGrab grab = new FrameGrab(new FileChannelWrapper(FileChannel.open(videoFilePath)));
        return grab.seekToSecondPrecise(secondPrecise).getNativeFrame();
    }

    private void savePictureToDisk(Picture nativeFrame, String uniquieThumbnailFileName) throws IOException {
        BufferedImage img = AWTUtil.toBufferedImage(nativeFrame);
        ImageIO.write(img, "jpg", new File(mediaDir + "/thumbnails", uniquieThumbnailFileName));
    }

    private String generateUniqueThumbnailFileName() {
        return UUID.randomUUID() + ".jpg";
    }
}
