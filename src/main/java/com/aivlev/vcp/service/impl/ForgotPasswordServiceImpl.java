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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by aivlev on 5/19/16.
 */
@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    NotificationService notificationService;



    @Override
    public void sendRecoveryEmail(String email) {
        User user = userService.findByEmail(email);

        if(null != user){
            String code = JWTUtils.generateCode(user);
            notificationService.sendNotification(user, code, NotificationReason.RECOVERY.name());
            user.setRecoveryCode(code);
            userService.save(user);
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public void updatePassword(ResetPasswordDto resetPasswordDto) {
        Claims claims = JWTUtils.getClaims(resetPasswordDto.getCode());
        if(null != claims){
            String login = claims.getId();
            User user = userService.findByLogin(login);
            if(null != user){
                if(null != user.getRecoveryCode() && user.getRecoveryCode().equals(resetPasswordDto.getCode())){
                    Calendar expiredDate = Calendar.getInstance();
                    expiredDate.setTime(claims.getExpiration());
                    Calendar now = Calendar.getInstance();
                    if(now.before(expiredDate)){
                        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
                        user.setRecoveryCode(null);
                        userService.save(user);
                    } else {
                        throw new CodeExpiredException("Recover code was expired");
                    }
                } else {
                    throw new CodeNotFoundException("Recovery password code not found");
                }

            } else {
                throw new ModelNotFoundException("User not found");
            }
        } else {
            throw new CodeNotFoundException("Recovery password code not found");
        }
    }

    @Override
    public void validateCode(String code) {
        Claims claims = JWTUtils.getClaims(code);
        if(null != claims){
            String login = claims.getId();
            User user = userService.findByLogin(login);
            if(null != user){
                if(null != user.getRecoveryCode() && user.getRecoveryCode().equals(code)){
                    Calendar expiredDate = Calendar.getInstance();
                    expiredDate.setTime(claims.getExpiration());
                    Calendar now = Calendar.getInstance();
                    if(now.after(expiredDate)){
                        throw new CodeExpiredException("Recovery password code was expired");
                    }
                } else {
                    throw new CodeNotFoundException("Recovery password code not found");
                }
            } else {
                throw new ModelNotFoundException("User not found");
            }
        } else {
            throw new CodeNotFoundException("Recovery password code not found");
        }
    }
}
