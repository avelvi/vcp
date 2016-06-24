package com.aivlev.vcp.service;

import com.aivlev.vcp.dto.UpdatePasswordDto;
import com.aivlev.vcp.model.Category;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by aivlev on 4/26/16.
 */
public interface UserService {


    void uploadVideo(String login, UploadForm form, Category category);

    User findByLogin(String login);

    User findByEmail(String email);

    void save(String id, User user);

    void registerUser(User user, boolean isRegistrationForm);

    User findUser(boolean isAdmin, String login, String id);

    Page<Video> findVideos(String id, Pageable pageable);

    Page<User> findAllUsers(Pageable pageable);

    void deleteUser(String id);

    void updateUser(String id, User user);

    void activateUser(String code);

    void updatePassword(String id, UpdatePasswordDto updatePasswordDto, String userName);

    long count();

    User findOne(String id);
}
