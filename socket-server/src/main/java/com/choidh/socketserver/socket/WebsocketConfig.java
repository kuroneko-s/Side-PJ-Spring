package com.choidh.socketserver.socket;

import com.choidh.socketserver.socket.interceptor.StompReceiveInterceptor;
import com.choidh.socketserver.socket.interceptor.StompSendInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompReceiveInterceptor stompReceiveInterceptor;
    private final StompSendInterceptor stompSendInterceptor;

//    @Bean
//    public StompErrorHandler stompErrorHandler() {
//        return new StompErrorHandler();
//    }

    /**
     * STOMP 프로토콜을 연결할 엔드포인트 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/an-ws")
                .setAllowedOrigins("*");
        // registry.setErrorHandler(stompErrorHandler());
    }

    /**
     * broker - 메시지를 중간에 받아서 서버 - 클라이언트 간 전달해주는 타입
     * 애플리케이션에서 브로커에게 전송할 데이터를 처리하는 채널 (맞을 듯)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 브로커한테 넘겨줄때 prefix로 붙여서 브로커로 전달
        registry.enableSimpleBroker("/sub");
        // 애노테이션 기반으로 설정된 대상들을 필터링 해줘서 타게팅을 잡아주는 기준값
        registry.setApplicationDestinationPrefixes("/pub");
    }

    /**
     * 클라이언트로 받은 데이터를 처리하는 채널
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompReceiveInterceptor);
    }

    /**
     * 클라이언트로 전송될 데이터를 처리하는 채널
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompSendInterceptor);
    }
}
