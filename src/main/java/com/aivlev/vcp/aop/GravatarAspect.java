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
import java.util.Iterator;
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

    @Around("execution(* com.aivlev.vcp.service.VideoService.findAll(..))")
    public Object advice(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Page val = (Page)pjp.proceed();
            List content = val.getContent();
            Iterator<Video> it = content.iterator();
            while(it.hasNext()){
                User user = it.next().getOwner();
                String hash = md5Hex(user.getEmail());
                if(hash != null){
                    String avatarRedirectPath = user.getAvatar() != null ? baseUrl + user.getAvatar() : baseUrl + "/media/static/images/avatar.png";
                    user.setAvatar("http://www.gravatar.com/avatar/" + hash + "?d=" + avatarRedirectPath);
                }

            }
            return val;
        } catch (Exception ex){
            LOGGER.error("Error has occurred while getting result data", ex);
        }
        return null;
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
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
