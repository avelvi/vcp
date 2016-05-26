package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.service.ThumbnailService;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * Created by aivlev on 4/20/16.
 */
@Service
public class JCodecThumbnailService implements ThumbnailService{
    private static final Logger LOGGER = LoggerFactory.getLogger(JCodecThumbnailService.class);

    @Override
    public byte[] createThumbnail(Path videoFilePath) {
        try {
            return createThumbnailInternal(videoFilePath);
        } catch (IOException | JCodecException e) {
            LOGGER.error("Error has occurred while creating thumbnail", e.getMessage());
            throw new ProcessMediaContentException("Can't create thumbnail: " + e.getMessage(), e);
        }
    }

    private byte[] createThumbnailInternal(Path videoFilePath) throws IOException, JCodecException {
        Picture nativeFrame = getVideoFrameBySecondPrecise(videoFilePath, 0);
        if (nativeFrame == null) {
            LOGGER.error("Can't find first video frame for video file: ", videoFilePath.getFileName());
            throw new ProcessMediaContentException("Can't find first video frame for video file: " + videoFilePath.getFileName());
        }
        BufferedImage img = AWTUtil.toBufferedImage(nativeFrame);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        return out.toByteArray();
    }


    private Picture getVideoFrameBySecondPrecise(Path videoFilePath, double secondPrecise) throws IOException, JCodecException {
        FrameGrab grab = new FrameGrab(new FileChannelWrapper(FileChannel.open(videoFilePath)));
        return grab.seekToSecondPrecise(secondPrecise).getNativeFrame();
    }
}
