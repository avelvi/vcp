package com.aivlev.vcp.service;

import com.aivlev.vcp.model.User;

/**
 * Created by aivlev on 5/9/16.
 */
public interface NotificationService {
    void sendActivationLink(User user, String activationLink);
}
