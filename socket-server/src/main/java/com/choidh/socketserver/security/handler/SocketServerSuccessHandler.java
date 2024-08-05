package com.choidh.socketserver.security.handler;

import com.choidh.service.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SocketServerSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        User principal = (User) authentication.getPrincipal();

        String authorities = principal.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwt = jwtService.generateToken(principal.getUsername(), principal.getUsername(), authorities);

        // 기본 동작을 수행하여 이전 요청 페이지로 리다이렉트
        response.sendRedirect("/login/success?token=" + jwt);
    }
}
