package com.aivlev.vcp.repository.search;

import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by aivlev on 4/26/16.
 */
public interface VideoSearchRepository extends ElasticsearchRepository<Video, String> {

    Page<Video> findByTitleOrOwnerName(String title, String ownerName, Pageable pageable);
}
