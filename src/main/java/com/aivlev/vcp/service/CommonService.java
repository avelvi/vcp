package com.aivlev.vcp.service;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;

/**
 * Created by aivlev on 4/26/16.
 */
public interface CommonService {

    @Nonnull
    ResponseHolder<Page<Video>> findAllVideos(Pageable pageable);

    @Nonnull
    ResponseHolder<Page<Video>> findAllVideosBySearchQuery(@Nonnull String searchQuery, @Nonnull Pageable pageable);

}
