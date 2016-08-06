package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.dto.UpdatePasswordDto;
import com.aivlev.vcp.exception.*;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.service.*;
import com.aivlev.vcp.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by aivlev on 4/26/16.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${media.dir}")
    private String mediaDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private ImageService imageService;

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
        User user = findByLogin(login);
        Video video = videoProcessorService.processVideo(form);
        video.setOwner(user);
        video.setCategory(category);
        videoRepository.save(video);
        videoSearchRepository.save(video);
    }

    @Override
    public User findByLogin(String login) {
        User user = userRepository.findByLogin(login);
        if(user != null){
            return user;
        } else {
            LOGGER.error("User with login = " + login + " not found");
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user != null){
            return user;
        } else {
            LOGGER.error("User with email = " + email + " not found");
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public void save(String id, User user) {
        if(id == null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User userFromDB = findOne(id);
            if(userFromDB == null){
                LOGGER.error("User with id = " + id + " not found");
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
                LOGGER.error("User with the same email exists. Email - " + user.getEmail());
                throw new DuplicateEntityException("User with the same email exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String code = JWTUtils.generateCode(user);
            if(isRegistrationForm){
                Authority authority = authorityService.findByName("user");
                user.setAuthorities(Collections.singletonList(authority));
                user.setActive(false);
                notificationService.sendNotification(user, code, NotificationReason.ACTIVATION.name());
            } else {
                if(!user.isActive()){
                    notificationService.sendNotification(user, code, NotificationReason.ACTIVATION.name());
                }
            }
            userRepository.save(user);
        } else {
            LOGGER.error("User with the same login exists. Login - " + user.getLogin());
            throw new DuplicateEntityException("User with the same login exists");
        }
    }

    @Override
    public User findUser(boolean isAdmin, String login, String id) {
        User user;
        if(isAdmin){
            user = findOne(id);
        } else {
            user = userRepository.findByLogin(login);
            if(user != null && !user.getId().equals(id)){
                LOGGER.error("Sorry, but you don't have permissions.");
                throw new AccessDeniedException("Sorry, but you don't have permissions.");
            }
        }
        if(user != null){
            user.setPassword("");
        } else {
            LOGGER.error("User with id = " + id + " not found");
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
            LOGGER.error("Error has occurred while deleting user with id = " + id + ". User not found");
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public void updateUser(String id, User user) {
        User userFromDB = findOne(id);
        if(!userFromDB.getEmail().equalsIgnoreCase(user.getEmail())) {
            User tempUser = userRepository.findByEmail(user.getEmail());
            if (tempUser != null) {
                LOGGER.error("User with the same email exists. Email - " + user.getEmail());
                throw new DuplicateEntityException("User with the same email exists");
            }
        }
        if(!userFromDB.getLogin().equalsIgnoreCase(user.getLogin())){
            User tempUser = userRepository.findByLogin(user.getLogin());
            if(tempUser != null){
                LOGGER.error("User with the same login exists. Login - " + user.getLogin());
                throw new DuplicateEntityException("User with the same login exists");
            }
        }
        user.setPassword(userFromDB.getPassword());
        userRepository.save(user);
    }

    @Override
    public void activateUser(String code) {
        Claims claims = JWTUtils.getClaims(code);
        if(claims != null){
            String login = claims.getId();
            User user = findByLogin(login);
            if(!user.isActive()){
                Calendar expiredDate = Calendar.getInstance();
                expiredDate.setTime(claims.getExpiration());
                Calendar now = Calendar.getInstance();
                if(now.before(expiredDate)){
                    user.setActive(true);
                    userRepository.save(user);
                } else {
                    LOGGER.error("Activation code was expired");
                    throw new CodeExpiredException("Activation code was expired");
                }
            } else {
                LOGGER.error("Activation code was expired");
                throw new CodeExpiredException("Your profile was activated");
            }
        } else {
            LOGGER.error("Recovery password code not found");
            throw new CodeNotFoundException("Recovery password code not found");
        }
    }

    @Override
    public void updatePassword(String id, UpdatePasswordDto updatePasswordDto, String userName) {
        User user = findOne(id);
        User userFromDB = userRepository.findByLogin(userName);
        if(userFromDB != null && userFromDB.getId().equalsIgnoreCase(id)){
            user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
            userRepository.save(user);
        } else {
            LOGGER.error("Sorry, but you don't have permissions");
            throw new AccessDeniedException("Sorry, but you don't have permissions");
        }

    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public User findOne(String id) {
        User user = userRepository.findOne(id);
        if(user != null){
            return user;
        } else {
            LOGGER.error("User with id = " + id + " not found");
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public String changeAvatar(String login, UploadForm uploadForm) {
        User user = findByLogin(login);
        try {
            byte[] imageBytes = uploadForm.getFile().getBytes();
            String avatarPath = imageService.saveImageData(imageBytes, false);
            if(user.getAvatar() != null){
                Files.deleteIfExists(Paths.get(mediaDir.substring(0, mediaDir.length() - 6) + user.getAvatar()));
            }
            user.setAvatar(avatarPath);
            userRepository.save(user);
            return avatarPath;

        } catch (IOException e) {
            LOGGER.error("Error has occurred while reading image");
            throw new BadImageException("Bad Image");
        }

    }
}