package com.aivlev.vcp.service;

import com.aivlev.vcp.exception.ProcessMediaContentException;

import javax.annotation.Nonnull;

/**
 * Created by aivlev on 4/26/16.
 */
public interface ImageService {

    @Nonnull
    String saveImageData (@Nonnull byte[] imageBytes) throws ProcessMediaContentException;

}
