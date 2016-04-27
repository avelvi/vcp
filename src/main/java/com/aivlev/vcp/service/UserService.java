package com.aivlev.vcp.service;

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
}
