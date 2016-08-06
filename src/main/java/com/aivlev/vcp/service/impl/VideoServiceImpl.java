package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.AccessDeniedException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by aivlev on 4/19/16.
 */
@Service
@Transactional
public class VideoServiceImpl implements VideoService {

    private static final String PATH_PREFIX = "/media/video/";

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Autowired
    private UserService userService;

    @Value("${media.dir}")
    private String mediaDir;

    @Override
    public Video findOne(String id) {
        Video video = videoRepository.findOne(id);
        if(video == null){
            LOGGER.error("Video with id = " + id + " not found");
            throw new ModelNotFoundException("Video not found");
        }
        return video;
    }

    @Override
    public String saveVideo(Path tempFilePath) {
        try {
            return saveVideoInternal(tempFilePath);
        } catch (IOException e) {
            LOGGER.error("Error has occurred while saving video", e.getMessage());
            throw new ProcessMediaContentException("Error has occurred while saving video: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<Video> findAllVideosByOwnerId(String ownerId, Pageable pageable) {
        return videoRepository.findByOwnerId(ownerId, pageable);
    }

    @Override
    public Page<Video> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    @Override
    public void deleteVideo(boolean isAdmin, String userName, String id) {
        Video video = findOne(id);
        if(isAdmin){
            videoRepository.delete(video);
        } else {
            User user = userService.findByLogin(userName);
            if(video.getOwner().getId().equals(user.getId())){
                videoRepository.delete(video);
            } else {
                LOGGER.error("Sorry, but you don't have permissions");
                throw new AccessDeniedException("Sorry, but you don't have permissions");
            }
        }
    }

    @Override
    public void updateVideo(boolean isAdmin, String login, String id, Video video) {
        Video videoFromDB = videoRepository.findOne(id);
        if(videoFromDB != null && videoFromDB.getId().equals(video.getId())){
            videoFromDB.setTitle(video.getTitle());
            videoFromDB.setDescription(video.getDescription());
            if(isAdmin){
                videoRepository.save(video);
                videoSearchRepository.save(video);
            } else {
                User user = userService.findByLogin(login);
                if(video.getOwner().getId().equals(user.getId())){
                    videoRepository.save(video);
                    videoSearchRepository.save(video);
                } else {
                    LOGGER.error("Sorry, but you don't have permissions.");
                    throw new AccessDeniedException("Sorry, but you don't have permissions.");
                }
            }
        } else {
            LOGGER.error("Video with id = " + id + " not found");
            throw new ModelNotFoundException("Video not found");
        }
    }

    @Override
    public Page<Video> findAllVideosBySearchQuery(String searchQuery, Pageable pageable) {
        SearchQuery sq = createSearchQuery(searchQuery, pageable);
        return videoSearchRepository.search(sq);
    }


    @Override
    public Page<Video> findTop3ByOrderByCreatedDateDesc(Pageable pageable) {
        return videoRepository.findTop3ByOrderByCreatedDateDesc(pageable);
    }

    @Override
    public Page<Video> findTop3ByOrderByViewsDesc(Pageable pageable) {
        return videoRepository.findTop3ByOrderByViewsDesc(pageable);
    }

    @Override
    public long count() {
        return videoRepository.count();
    }

    private String saveVideoInternal(Path tempFilePath) throws IOException {
        String uniqueVideoFileName = generateUniqueVideoFileName();
        Path videoFilePath = Paths.get(mediaDir + "/video/" + uniqueVideoFileName);
        Files.copy(tempFilePath, videoFilePath);
        return PATH_PREFIX + uniqueVideoFileName;
    }

    private String generateUniqueVideoFileName() {
        return UUID.randomUUID()+".mp4";
    }

    private SearchQuery createSearchQuery(String searchQuery, Pageable pageable) {
        SearchQuery sq = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(searchQuery)
                        .field("title")
                        .field("owner.name")
                        .field("owner.surname")
                        .field("owner.company.name")
                        .field("category.name")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .fuzziness(Fuzziness.TWO)
                        .operator(MatchQueryBuilder.Operator.OR))
                .build();
        sq.setPageable(pageable);
        return sq;
    }


}
