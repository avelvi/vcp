package com.aivlev.vcp.service;

import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;

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
}
