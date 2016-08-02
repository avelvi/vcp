package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by aivlev on 4/20/16.
 */
public interface VideoRepository extends MongoRepository<Video, String> {

    Page<Video> findByOwnerId(String ownerId, Pageable pageable);

    Page<Video> findTop3ByOrderByCreatedDateDesc(Pageable pageable);

    Page<Video> findTop3ByOrderByViewsDesc(Pageable pageable);

}
