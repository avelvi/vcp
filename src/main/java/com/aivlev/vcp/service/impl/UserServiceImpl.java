package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.*;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.AuthorityRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by aivlev on 4/26/16.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Autowired
    private VideoProcessorService videoProcessorService;

    @Override
    public void uploadVideo(String login, UploadForm form, Category category) {
        User user = userRepository.findByLogin(login);
        if(user != null){
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
        User user = userRepository.findByLogin(login);
        if(user != null){
            return user;
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user != null){
            return user;
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    @Transactional
    public void save(String id, User user) {
        if(id == null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User userFromDB = userRepository.findOne(id);
            if(userFromDB == null){
                throw new ModelNotFoundException("User not found");
            }
        }
        userRepository.save(user);
    }

    @Override
    public void registerUser(User user, boolean isRegistrationForm) {
        User userFromDb = userRepository.findByLogin(user.getLogin());
        if(userFromDb == null){
            userFromDb = userRepository.findByEmail(user.getEmail());
            if(userFromDb != null){
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
            user = userRepository.findByLogin(login);
            if(user != null && !user.getId().equals(id)){
                throw new AccessDeniedException("Sorry, but you don't have permissions.");
            }
        }
        if(user != null){
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
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findOne(id);
        if(user != null){
            userRepository.delete(id);
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    @Transactional
    public void updateUser(String id, User user) {
        User userFromDB = userRepository.findOne(id);
        if(userFromDB != null){
            if(!userFromDB.getEmail().equalsIgnoreCase(user.getEmail())) {
                User tempUser = userRepository.findByEmail(user.getEmail());
                if (tempUser != null) {
                    throw new DuplicateEntityException("User with the same email exists");
                }
            }
            if(!userFromDB.getLogin().equalsIgnoreCase(user.getLogin())){
                User tempUser = userRepository.findByLogin(user.getLogin());
                if(tempUser != null){
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
        if(claims != null){
            String login = claims.getId();
            User user = userRepository.findByLogin(login);
            if(user != null){
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