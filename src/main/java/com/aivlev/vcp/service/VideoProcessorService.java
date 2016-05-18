package com.aivlev.vcp.service;

import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.Video;

/**
 * Created by aivlev on 5/18/16.
 */
public interface VideoProcessorService {

    Video processVideo(UploadForm uploadForm);
}
