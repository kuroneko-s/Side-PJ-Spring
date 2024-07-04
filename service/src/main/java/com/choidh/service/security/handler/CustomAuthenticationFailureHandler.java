package com.choidh.service.security.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        if (exception instanceof UsernameNotFoundException) {
            response.sendRedirect("/login?error=username");
        } else if (exception instanceof BadCredentialsException) {
            response.sendRedirect("/login?error=bad_credentials");
        } else if (exception instanceof CredentialsExpiredException) {
            response.sendRedirect("/login?error=email_credentials_expired");
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
