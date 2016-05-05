package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by aivlev on 4/20/16.
 */
public interface VideoRepository extends PagingAndSortingRepository<Video, String> {

    Page<Video> findByOwnerId(String ownerId, Pageable pageable);

}
