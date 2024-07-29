package com.choidh.app.security.oauth;

import com.choidh.app.security.oauth.vo.OAuthAttributes;
import com.choidh.service.account.entity.ApiAccount;
import com.choidh.service.account.repository.ApiAccountRepository;
import com.choidh.service.account.vo.api.ApiAccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 소셜 로그인 서비스 로직
 */

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final ApiAccountRepository accountRepository;

    /**
     * 사용자가 OAuth 인증을 진행하고, 인증에 성공하면 OAuth2UserRequest 객체에 제공자에 대한 정보, 엑세스 토큰 등과 같은 정보를 넣어서 CustomOAuth2UserService로 넘어오게 됩니다.
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        // OAuth2UserService를 사용하여 OAuth2User 정보를 가져온다.
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 로그인 진행중인 서비스를 구분하는 ID -> 여러 개의 소셜 로그인할 때 사용하는 ID. (소셜 로그인 구분용)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드값(Primary Key) -> 구글은 기본적으로 해당 값 지원("sub")
        // 그러나, 네이버, 카카오 로그인 시 필요한 값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserSevice를 통해 가져온 OAuth2User의 attribute를 담은 클래스
        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );

        // 우리의 서비스에 회원가입이나 기존 회원의 정보를 업데이트를 한다.
        ApiAccount apiAccount = saveOrUpdate(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(apiAccount.getType().name())),
                attributes.getAttributes(), attributes.getNameAttributeKey()
        );
    }

    private ApiAccount saveOrUpdate(OAuthAttributes attributes) {
        // 소셜 로그인의 회원 정보가 업데이트 되었다면, 기존 DB에 저장된 회원의 이름을 업데이트해줍니다.
        ApiAccount apiAccount = accountRepository.findByEmail(attributes.getEmail());

        if (apiAccount == null) {
            apiAccount = accountRepository.save(ApiAccount.builder()
                            .email(attributes.getEmail())
                            .name(attributes.getName())
                            .type(ApiAccountType.USER)
                    .build());
        }

        // 만약에 DB에 등록된 이메일이 아니라면, save하여 DB에 등록(회원가입)을 진행시켜준다.
        return accountRepository.save(apiAccount);
    }
}
