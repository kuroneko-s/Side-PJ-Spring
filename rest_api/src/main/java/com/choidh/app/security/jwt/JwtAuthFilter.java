package com.choidh.app.security.jwt;

import com.choidh.app.security.jwt.service.JWTService;
import com.choidh.service.account.entity.ApiAccount;
import com.choidh.service.account.repository.ApiAccountRepository;
import com.choidh.service.account.vo.api.ApiAccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Custom Security JWT Filter
 */

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {
    private final ApiAccountRepository apiAccountRepository;
    private final JWTService JWTService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // HttpServletRequest에서 헤더에 "X-AUTH-TOKEN"에 작성된 token을 가져옵니다.
        String token = JWTService.resolveToken((HttpServletRequest) request);

        // PermitAll 해도 여기에 걸칠땐 검증을하다보니 에러가 날수있다고함

        // 헤더에 작성된 토큰이 있는지 확인하고, 토큰이 만료되었는지 확인합니다.
        if (token != null && JWTService.validateToken(token)) {
            // 토큰에서 secret key를 사용하여 회원의 이메일을 가져옵니다.
            String email = JWTService.getEmail(token);

            ApiAccount apiAccount = apiAccountRepository.findByEmail(email);

            //인증된 회원의 정보를 SecurityContextHolder에 저장합니다.
            //현재는 역할이 ROLE_USER뿐이라서 권한을 직접 주는 형태로 하였으나, 권한이 여러개인 경우 변경해야 합니다.
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    apiAccount,
                    "",
                    List.of(new SimpleGrantedAuthority(ApiAccountType.USER.getKey()))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        //토큰이 없거나 만료된 토큰이라면, 다시 소셜 로그인을 진행하는 과정을 수행합니다.
        chain.doFilter(request, response);
    }
}
