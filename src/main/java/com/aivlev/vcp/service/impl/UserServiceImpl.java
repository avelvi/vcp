package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.*;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.service.AuthorityService;
import com.aivlev.vcp.service.NotificationService;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoProcessorService;
import com.aivlev.vcp.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;

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
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Autowired
    VideoProcessorService videoProcessorService;


    @Override
    public void uploadVideo(String login, UploadForm form, Category category) {
        User user = userRepository.findByLogin(login);
        if(null != user){
            Video video = videoProcessorService.processVideo(form);
            video.setOwner(user);
            video.setCategory(category);
            videoRepository.save(video);
            videoSearchRepository.save(video);
        } else {
            throw new ModelNotFoundException("User not found");
        }

    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void registerUser(User user, boolean isRegistrationForm) {
        User userFromDb = userRepository.findByLogin(user.getLogin());
        if(null == userFromDb){
            userFromDb = userRepository.findByEmail(user.getEmail());
            if(null != userFromDb){
                throw new DuplicateEntityException("User with the same email exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String code = JWTUtils.generateCode(user);
            if(isRegistrationForm){
                Authority authority = authorityService.findByName("user");
                user.setAuthorities(Arrays.asList(authority));
                user.setActive(false);
                notificationService.sendNotification(user, code, NotificationReason.ACTIVATION.name());
            }
            if(!user.isActive()){
                notificationService.sendNotification(user, code, NotificationReason.ACTIVATION.name());
            }
            userRepository.save(user);
        } else {
            throw new DuplicateEntityException("User with the same login exists");
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
        } else {
            throw new ModelNotFoundException("User not found");
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
        if(null != userFromDB){
            if(!userFromDB.getEmail().equalsIgnoreCase(user.getEmail())) {
                User tempUser = userRepository.findByEmail(user.getEmail());
                if (null != tempUser) {
                    throw new DuplicateEntityException("User with the same email exists");
                }
            }
            if(!userFromDB.getLogin().equalsIgnoreCase(user.getLogin())){
                User tempUser = userRepository.findByLogin(user.getLogin());
                if(null != tempUser){
                    throw new DuplicateEntityException("User with the same login exists");
                }
            }
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
                        user.setActive(true);
                        userRepository.save(user);
                    } else {
                        throw new CodeExpiredException("Activation code was expired");
                    }
                }
            } else {
                throw new ModelNotFoundException("User not found");
            }
        } else {
            throw new CodeNotFoundException("Recovery password code not found");
        }
    }
}