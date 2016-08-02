package com.aivlev.vcp.service;

import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

/**
 * Created by aivlev on 4/19/16.
 */
public interface VideoService {

    Video findOne(String id);

    String saveVideo (Path tempFilePath) throws ProcessMediaContentException;

    Page<Video> findAllVideosByOwnerId(String ownerId, Pageable pageable);

    Page<Video> findAll(Pageable pageable);

    void deleteVideo(boolean isAdmin, String userName, String id);

    void updateVideo(boolean isAdmin, String login, String id, Video video);

    Page<Video> findAllVideosBySearchQuery(String searchQuery, Pageable pageable);

    Page<Video> findTop3ByOrderByCreatedDateDesc(Pageable pageable);

    Page<Video> findTop3ByOrderByViewsDesc(Pageable pageable);

    long count();

}
