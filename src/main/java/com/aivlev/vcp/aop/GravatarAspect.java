package com.aivlev.vcp.aop;

import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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


    @AfterReturning(pointcut = "execution(com.aivlev.vcp.model.User com.aivlev.vcp.service.UserService.*(..))",
            returning = "retVal")
    public void userAdvice(User retVal) throws Throwable {
        try {
            rewriteAvatar(retVal);
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
    }


    @AfterReturning(pointcut = "execution(org.springframework.data.domain.Page com.aivlev.vcp.service.VideoService.*(..)) " +
            "|| execution(org.springframework.data.domain.Page com.aivlev.vcp.service.UserService.findVideos(..)))",
            returning = "retVal"
    )
    public void videosAdvice(Page<Video> retVal) throws Throwable {
        try {
            List content = (retVal.getContent());
            for (Video aContent : (Iterable<Video>) content) {
                rewriteAvatar(aContent.getOwner());
            }
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
    }

    @AfterReturning(pointcut = "execution(com.aivlev.vcp.model.Video com.aivlev.vcp.service.VideoService.findOne(..))", returning = "retVal")
    public void videoAdvice(Video retVal) throws Throwable {
        try {
            rewriteAvatar(retVal.getOwner());
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
    }

    private void rewriteAvatar(User user) {
        String hash = md5Hex(user.getEmail());
        if(hash != null){
            String avatarRedirectPath = (user.getAvatar() != null) ? baseUrl + user.getAvatar() : "404";
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
