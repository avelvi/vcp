package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.service.*;
import com.aivlev.vcp.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by aivlev on 4/26/16.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ImageService imageService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

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

        if(id == null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }else {
            User userFromDB = userRepository.findOne(id);
            if(null != userFromDB){
                user.setPassword(userFromDB.getPassword());
            } else {
                throw new UsernameNotFoundException("User not found.");
            }
        }
        userRepository.save(user);
    }

    @Override
    public void registerUser(UserDto userDto) {
        User user = userRepository.findByLogin(userDto.getLogin());

        if(null == user){
            User newUser = UserDto.convertToModel(userDto);
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            HashSet<Authority> authorities = authorityService.findByName("user");
            newUser.setAuthorities(authorities);
            newUser.setIsActive(false);
            userRepository.save(newUser);
            String code = JWTUtils.generateActivationCode(newUser);
            notificationService.sendActivationLink(newUser, code);
        }
    }


}