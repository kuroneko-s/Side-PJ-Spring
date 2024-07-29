package com.choidh.app.security.jwt.service;

import com.choidh.app.common.AppConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * 토큰 분석 서비스 로직
 */

@Service
public class JWTService {
    private Key secretKey;

    @Value("${security.jwt.token.secret-key}")
    private String SECRET_KEY;

    // 토큰의 유효 기간을 밀리초 단위로 설정.
    private final long REFRESH_PERIOD = (1000L * 60L * 60L * 24L) * 7; // 7일
    private final long TOKEN_PERIOD = 1000L * 60L * 30L; // 30분

    @PostConstruct
    protected void init() {
        byte[] apiKeySecurityBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        secretKey = new SecretKeySpec(apiKeySecurityBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * JWT 갱신
     */
    public String generateRefreshToken(String email, String role) {
        // 새로운 클레임 객체를 생성하고, 이메일과 역할(권한)을 셋팅
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        // 현재 시간과 날짜를 가져온다.
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .setClaims(claims) // Payload를 구성하는 속성들을 정의한다.
                .setIssuedAt(now) // 발행일자를 넣는다.
                .setExpiration(new Date(now.getTime() + REFRESH_PERIOD)) // 토큰의 만료일시를 설정한다.
                .signWith(SignatureAlgorithm.HS256, secretKey) // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                .compact();
    }

    /**
     * JWT 생성
     */
    public String generateToken(String name, String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("name", name);
        claims.put("role", role);

        Date date = new Date(System.currentTimeMillis());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + TOKEN_PERIOD))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * JWT 검증
     */
    public boolean validateToken(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiration(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().getTime();
    }

    public String getEmail(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    /**
     * JWT 획득
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AppConstant.JWT_HEADER_NAME);
    }
}
