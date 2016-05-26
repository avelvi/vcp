package com.aivlev.vcp.service;

import com.aivlev.vcp.exception.ProcessMediaContentException;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * Created by aivlev on 4/20/16.
 */
public interface ThumbnailService {

    @Nonnull
    byte[] createThumbnail(@Nonnull Path videoFilePath) throws ProcessMediaContentException;
}
