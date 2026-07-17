package com.yimian.system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析、校验
 */
@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long expiration;
    private final Long refreshExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") Long expiration,
                   @Value("${jwt.refresh-expiration}") Long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * 生成访问 Token
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .header().type("JWT").and()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 生成刷新 Token
     */
    public String generateRefreshToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .header().type("JWT").and()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取用户名（subject）
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从 Claims 中获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token 已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("不支持的 Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("Token 格式错误: {}", e.getMessage());
        } catch (SignatureException e) {
            log.debug("Token 签名无效: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("Token 为空: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 检查 Token 是否已过期（不抛异常）
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
