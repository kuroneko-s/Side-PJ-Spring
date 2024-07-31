package com.choidh.socketclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

// STOMP (Simple Text Oriented Messaging Protocol)

@Configuration
public class WebSocketClientConfig {
    @Bean
    public StompSessionHandler stompSessionHandler() {
        return new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.send("/app/hello", "Hello, Server!");
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                exception.printStackTrace();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
            }
        };
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public StompSession stompSession(WebSocketClient webSocketClient, StompSessionHandler stompSessionHandler) {
//        return new StompClient(webSocketClient).connect("ws://localhost:8080/ws/endpoint", stompSessionHandler).get();
        return null;
    }
}
