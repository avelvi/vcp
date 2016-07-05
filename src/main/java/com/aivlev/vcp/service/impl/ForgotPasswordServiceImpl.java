package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.dto.ResetPasswordDto;
import com.aivlev.vcp.exception.CodeExpiredException;
import com.aivlev.vcp.exception.CodeNotFoundException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.NotificationReason;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.service.ForgotPasswordService;
import com.aivlev.vcp.service.NotificationService;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by aivlev on 5/19/16.
 */
@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void sendRecoveryEmail(String email) {
        User user = userService.findByEmail(email);
        String code = JWTUtils.generateCode(user);
        notificationService.sendNotification(user, code, NotificationReason.RECOVERY.name());
        user.setRecoveryCode(code);
        userService.save(user.getId(), user);
    }

    @Override
    public void updatePassword(ResetPasswordDto resetPasswordDto) {
        Claims claims = JWTUtils.getClaims(resetPasswordDto.getCode());
        if(claims != null){
            String login = claims.getId();
            User user = userService.findByLogin(login);
            if(user.getRecoveryCode() != null && user.getRecoveryCode().equals(resetPasswordDto.getCode())){
                Calendar expiredDate = Calendar.getInstance();
                expiredDate.setTime(claims.getExpiration());
                Calendar now = Calendar.getInstance();
                if(now.before(expiredDate)){
                    user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
                    user.setRecoveryCode(null);
                    userService.save(user.getId(), user);
                } else {
                    LOGGER.error("Recover code was expired");
                    throw new CodeExpiredException("Recover code was expired");
                }
            } else {
                LOGGER.error("Recovery password code not found");
                throw new CodeNotFoundException("Recovery password code not found");
            }
        } else {
            LOGGER.error("Recovery password code not found");
            throw new CodeNotFoundException("Recovery password code not found");
        }
    }

    @Override
    public void validateCode(String code) {
        Claims claims = JWTUtils.getClaims(code);
        if(claims != null){
            String login = claims.getId();
            User user = userService.findByLogin(login);
            if(user.getRecoveryCode() != null && user.getRecoveryCode().equals(code)){
                Calendar expiredDate = Calendar.getInstance();
                expiredDate.setTime(claims.getExpiration());
                Calendar now = Calendar.getInstance();
                if(now.after(expiredDate)){
                    LOGGER.error("Recover code was expired");
                    throw new CodeExpiredException("Recovery password code was expired");
                }
            } else {
                LOGGER.error("Recovery password code not found");
                throw new CodeNotFoundException("Recovery password code not found");
            }
        } else {
            LOGGER.error("Recovery password code not found");
            throw new CodeNotFoundException("Recovery password code not found");
        }
    }
}
