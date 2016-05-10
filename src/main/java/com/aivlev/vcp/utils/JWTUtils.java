package com.aivlev.vcp.utils;

import com.aivlev.vcp.model.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * Created by aivlev on 5/9/16.
 */
public class JWTUtils {

    private static final long EXPIRED_DELTA_TIME = 24 * 60 * 60 * 1000l;
    private static final String SECRET_KEY = "video content portal";

    public static String generateActivationCode(User user){

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date();

        byte[] secretKeyBytes = TextCodec.BASE64.encode(SECRET_KEY).getBytes();
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setId(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRED_DELTA_TIME))
                .signWith(signatureAlgorithm, signingKey);

        //Builds the JWT and serializes it to a compact, URL-safe string
        String code = builder.compact();
        return code;
    }

}
