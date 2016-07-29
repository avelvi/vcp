package com.aivlev.vcp.service;

import com.aivlev.vcp.exception.ProcessMediaContentException;

/**
 * Created by aivlev on 4/26/16.
 */
public interface ImageService {

    String saveImageData (byte[] imageBytes) throws ProcessMediaContentException;

}
