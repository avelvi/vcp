package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.exception.AccessDeniedException;
import com.aivlev.vcp.exception.ActivationCodeExpiredException;
import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.service.*;
import com.aivlev.vcp.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.*;

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
                throw new ModelNotFoundException("User not found.");
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
            Authority authority = authorityService.findByName("user");
            newUser.setAuthorities(new HashSet<>(Arrays.asList(authority)));
            newUser.setIsActive(false);
            userRepository.save(newUser);
            String code = JWTUtils.generateActivationCode(newUser);
            notificationService.sendActivationLink(newUser, code);
        } else {
            if(user.getLogin().equalsIgnoreCase(userDto.getLogin())){
                throw new DuplicateEntityException("User with the same login exists");
            }
            throw new DuplicateEntityException("User with the same email exists");
        }
    }

    @Override
    public User findUser(boolean isAdmin, String login, String id) {
        User user;
        if(isAdmin){
            user = userRepository.findOne(id);
        } else {
            User loggedUser = userRepository.findByLogin(login);
            if(loggedUser.getId().equals(id)){
                user = userRepository.findOne(id);
            } else {
                throw new AccessDeniedException("Sorry, but you don't have permissions.");
            }
        }
        if(null != user){
            user.setPassword("");
        }
        return user;
    }

    @Override
    public Page<Video> findVideos(String id, Pageable pageable) {
        return videoRepository.findByOwnerId(id, pageable);
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findOne(id);
        if(user != null){
            userRepository.delete(id);
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public void updateUser(String id, User user) {
        User userFromDB = userRepository.findOne(id);
        if(null != user){
            user.setPassword(userFromDB.getPassword());
            userRepository.save(user);
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public void activateUser(String code) {
        Claims claims = JWTUtils.getClaims(code);
        if(null != claims){
            String login = claims.getId();
            User user = userRepository.findByLogin(login);
            if(null != user){
                if(!user.isActive()){
                    Calendar expiredDate = Calendar.getInstance();
                    expiredDate.setTime(claims.getExpiration());
                    Calendar now = Calendar.getInstance();
                    if(now.before(expiredDate)){
                        user.setIsActive(true);
                        userRepository.save(user);
                    } else {
                        throw new ActivationCodeExpiredException("Activation code was expired");
                    }
                }
            } else {
                throw new ModelNotFoundException("User not found");
            }
        }
    }


}