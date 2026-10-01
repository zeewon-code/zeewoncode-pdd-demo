package com.zeewoncode.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretStr;

    @Value("${jwt.expire-time}")
    private Long expireTime;

    // 密钥
    private SecretKey secretKey;

    // 项目启动初始化密钥
    @PostConstruct
    public void initKey() {
        this.secretKey = Keys.hmacShaKeyFor(secretStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建jwt令牌
     * @param userId 用户ID
     * @param userType 用户类型 1:C买家 2:B商家 9:A管理员
     * @return
     */
    public String generateToken(Integer userId, Integer userType) {
        // 创建载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        // 创建过期时间
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis + expireTime);
        // 包装jwt令牌
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(currentTimeMillis))
                .expiration(date)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析token，获取载荷claims
     * @param token 请求携带的token
     * @return 成功返回载荷claims，过期/篡改/格式错误返回null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    // 从claims中获取userId
    public static Integer getUserId(Claims claims) {
        return claims.get("userId", Integer.class);
    }

    // 从claims中获取userType
    public static Integer getUserType(Claims claims) {
        return claims.get("userType", Integer.class);
    }
}
