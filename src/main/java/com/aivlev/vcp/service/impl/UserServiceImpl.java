package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.service.ImageService;
import com.aivlev.vcp.service.ThumbnailService;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;

/**
 * Created by aivlev on 4/26/16.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private VideoService videoService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ImageService imageService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Autowired
    private UploadVideoTempStorage uploadVideoTempStorage;

    @Override
    public ResponseHolder<Video> uploadVideo(User user, UploadForm form) {
        Path tempUploadedVideoPath = uploadVideoTempStorage.getTempUploadedVideoPath();
        String videoUrl = videoService.saveVideo(tempUploadedVideoPath);
        byte[] thumbnailImageData = thumbnailService.createThumbnail(tempUploadedVideoPath);
        String thumbnailImageUrl = imageService.saveImageData(thumbnailImageData);
        Video video = new Video(form.getTitle(), form.getDescription(), String.valueOf(new Date().getTime()), videoUrl, Collections.singletonList(thumbnailImageUrl), user);
        videoRepository.save(video);
        videoSearchRepository.save(video);
        return new ResponseHolder<>(video);
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public void save(String id, User user) {

        User userFromDB = userRepository.findOne(id);
        if(userFromDB != null){
            user.setPassword(userFromDB.getPassword());
        }
        userRepository.save(user);
    }


}