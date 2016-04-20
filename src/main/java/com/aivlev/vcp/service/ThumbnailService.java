package com.aivlev.vcp.service;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by aivlev on 4/20/16.
 */
public interface ThumbnailService {

    List<String> createThumbnails(Path videoFilePath);
}
