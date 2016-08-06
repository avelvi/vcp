package com.aivlev.vcp.utils;

import com.aivlev.vcp.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * Created by aivlev on 5/9/16.
 */
public class JWTUtils {

    private static final long EXPIRED_DELTA_TIME = 24 * 60 * 60 * 1000l;
    private static final String SECRET_KEY = "video content portal";

    public static String generateCode(User user){

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date();

        byte[] secretKeyBytes = TextCodec.BASE64.encode(SECRET_KEY).getBytes();
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setId(user.getLogin())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRED_DELTA_TIME))
                .signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }

    public static Claims getClaims(String code){
        Claims claims = null;

        try {
            claims = Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.encode(SECRET_KEY).getBytes())
                    .parseClaimsJws(code).getBody();
        } catch (JwtException ex){

        }
        return claims;
    }

}
