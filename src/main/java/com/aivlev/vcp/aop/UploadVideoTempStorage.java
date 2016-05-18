package com.aivlev.vcp.aop;

import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.model.UploadForm;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by aivlev on 4/26/16.
 */

@Aspect
@Component
public class UploadVideoTempStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadVideoTempStorage.class);
    private final ThreadLocal<Path> tempUploadedVideoPathStorage = new ThreadLocal<>();

    public Path getTempUploadedVideoPath() {
        return tempUploadedVideoPathStorage.get();
    }

    @Around("execution(* com.aivlev.vcp.service.impl.VideoProcessorServiceImpl.processVideo(..))")
    public Object advice(ProceedingJoinPoint pjp) throws Throwable {
        UploadForm form = (UploadForm) pjp.getArgs()[0];
        Path tempUploadedVideoPath = null;
        try {
            tempUploadedVideoPath = Files.createTempFile("upload", ".video");
            form.getFile().transferTo(tempUploadedVideoPath.toFile());
            tempUploadedVideoPathStorage.set(tempUploadedVideoPath);
            return pjp.proceed();
        } catch (IOException e) {
            LOGGER.error("Error has occurred while saving video content to temp file", e.getMessage());
            throw new ProcessMediaContentException("Error has occurred while saving video content to temp file: " + e.getMessage(), e);
        } finally {
            tempUploadedVideoPathStorage.remove();
            if (tempUploadedVideoPath != null) {
                try {
                    Files.deleteIfExists(tempUploadedVideoPath);
                } catch (IOException e) {
                    LOGGER.error("Error has occurred while removing temp file", tempUploadedVideoPath, e);
                }
            }
        }
    }
}
