package com.aivlev.vcp.service;

import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;

/**
 * Created by aivlev on 4/26/16.
 */
public interface UserService {


    void uploadVideo(String login, UploadForm form, Category category);

    User findByLogin(String login);

    User findByEmail(String email);

    void save(String id, User user);

    void save(User user);

    void registerUser(UserDto userDto);

    User findUser(boolean isAdmin, String login, String id);

    Page<Video> findVideos(String id, Pageable pageable);

    Page<User> findAllUsers(Pageable pageable);

    void deleteUser(String id);

    void updateUser(String id, User user);

    void activateUser(String code);

}
