package com.aivlev.vcp.service;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by aivlev on 4/19/16.
 */
public interface VideoService {
    ResponseHolder<Page<Video>> findAllVideos(Pageable pageable);

    Video processVideo(MultipartFile videoFile);
}
