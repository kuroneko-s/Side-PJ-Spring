package com.choidh.app.security.oauth;

import com.choidh.app.common.AppConstant;
import com.choidh.app.security.jwt.service.JWTService;
import com.choidh.service.account.vo.api.ApiAccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2 인증이 성공했을 경우, 성공 후 처리를 위한 클래스
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 회원 존재 유무 확인 후 JWT 생성 혹은 회원가입으로 전환.
        String token = jwtService.generateToken(name, email, ApiAccountType.USER.getKey());

        String targetUrl = UriComponentsBuilder.fromUriString("/login/success")
                .queryParam("token", token)
                .queryParam("expiration", jwtService.getExpiration(token))
                .queryParam("email", email)
                .build()
                .toUriString();

        response.setHeader(AppConstant.JWT_HEADER_NAME, token);
        response.sendRedirect(targetUrl);
    }

}
