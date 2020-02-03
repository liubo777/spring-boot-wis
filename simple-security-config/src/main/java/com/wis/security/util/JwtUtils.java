package com.wis.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuBo
 * 2020/2/3.
 */
@Slf4j
public class JwtUtils {
    public static Map verifyToken(String token, String secret){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Map map = new HashMap();
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build(); //Reusable verifier instance
        DecodedJWT jwt ;
        try {
            jwt = verifier.verify(token);
            map.put("subject",jwt.getSubject());
            jwt.getClaims().keySet().stream().forEach(k->{
                map.put(k,jwt.getClaim(k).isNull()?"":jwt.getClaim(k).asString());
            });
        } catch (JWTVerificationException e) {
            log.debug("verify jwt error :"+e.getMessage()+"("+token+")");
            return null;
        }

        return map;
    }

    public static String createToken(String secret,Integer expireSecond,Map<String,String> claims,String user){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        LocalDateTime time = LocalDateTime.now();
        Date now = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        time = time.plusSeconds(expireSecond);
        Date date = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        JWTCreator.Builder builder =JWT.create()
                .withIssuer("auth0").withSubject(user).withIssuedAt(now);
        if (claims!=null){
            claims.keySet().stream().forEach(k->{
                builder.withClaim(k,claims.get(k));
            });
        }
        return builder.withExpiresAt(date).sign(algorithm);
    }
}
