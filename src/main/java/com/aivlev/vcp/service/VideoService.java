package com.aivlev.vcp.service;

import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * Created by aivlev on 4/19/16.
 */
public interface VideoService {

    Video findOne(String id);

    @Nonnull
    String saveVideo (@Nonnull Path tempFilePath) throws ProcessMediaContentException;

    @Nonnull
    Page<Video> findAllVideosByOwnerId(@Nonnull String ownerId, @Nonnull Pageable pageable);

    Page<Video> findAll(Pageable pageable);

    void deleteVideo(boolean isAdmin, String userName, String id);

    void updateVideo(boolean isAdmin, String login, String id, Video video);

    Page<Video> findAllVideosBySearchQuery(String searchQuery, Pageable pageable);

    long count();

}
