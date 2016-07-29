package com.aivlev.vcp.service;

import com.aivlev.vcp.exception.ProcessMediaContentException;

import java.nio.file.Path;

/**
 * Created by aivlev on 4/20/16.
 */
public interface ThumbnailService {

    byte[] createThumbnail(Path videoFilePath) throws ProcessMediaContentException;
}
