package com.aivlev.vcp.service;

import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;

/**
 * Created by aivlev on 4/26/16.
 */
public interface UserService {

    @Nonnull
    ResponseHolder<Video> uploadVideo (@Nonnull User user, @Nonnull UploadForm form);

    User findByLogin(String login);

    void save(String id, User user);

    void registerUser(UserDto userDto);

    User findUser(boolean isAdmin, String login, String id);

    Page<Video> findVideos(String id, Pageable pageable);

    Page<User> findAllUsers(Pageable pageable);

    void deleteUser(String id);

    void updateUser(String id, User user);

    void activateUser(String code);
}
