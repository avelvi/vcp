package com.aivlev.vcp.aop;

import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by aivlev on 7/11/16.
 */
@Aspect
@Component
public class GravatarAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GravatarAspect.class);

    @Value("${base.url}")
    private String baseUrl;

    @Around("execution(* com.aivlev.vcp.service.VideoService.findAll*(..))" +
            "|| execution(* com.aivlev.vcp.service.UserService.findVideos(..))")
    public Object videosAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Page val = null;
        try {
            val = (Page)pjp.proceed();
            List content = val.getContent();
            for (Video aContent : (Iterable<Video>) content) {
                rewriteAvatar(aContent.getOwner());
            }
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
        return val;
    }

    @Around("execution(* com.aivlev.vcp.service.VideoService.findOne(..))")
    public Object videoAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Video video = null;
        try {
            video = (Video)pjp.proceed();
            rewriteAvatar(video.getOwner());
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
        return video;
    }

    @Around("execution(* com.aivlev.vcp.service.UserService.find*(..))")
    public Object userAdvice(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            if(result instanceof Page){
                List<User> users = ((Page) result).getContent();
                for (User user : users) {
                    rewriteAvatar(user);
                }
                return result;
            } else if(result instanceof User){
                rewriteAvatar((User) result);
            }
            return result;
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
        return null;
    }

    private void rewriteAvatar(User user) {
        String hash = md5Hex(user.getEmail());
        if(hash != null){
            String avatarRedirectPath = (user.getAvatar() != null) ? baseUrl + user.getAvatar() : baseUrl + "/media/static/images/avatar.png";
            user.setAvatar("http://www.gravatar.com/avatar/" + hash + "?d=" + avatarRedirectPath);
        }
    }

    private String md5Hex(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(email.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error("Error has occurred while hashing avatar data", e);
            return null;
        }
    }

    private String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte anArray : array) {
            sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
