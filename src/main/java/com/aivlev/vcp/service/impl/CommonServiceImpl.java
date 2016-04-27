package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * Created by aivlev on 4/26/16.
 */

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Nonnull
    @Override
    public ResponseHolder<Page<Video>> findAllVideos(Pageable pageable) {
        return new ResponseHolder<>(videoRepository.findAll(pageable));
    }

    @Nonnull
    @Override
    public ResponseHolder<Page<Video>> findAllVideosBySearchQuery(@Nonnull String searchQuery, @Nonnull Pageable pageable) {
        return new ResponseHolder<>(videoSearchRepository.findByTitleOrOwnerName(searchQuery, searchQuery, pageable));
    }
}
