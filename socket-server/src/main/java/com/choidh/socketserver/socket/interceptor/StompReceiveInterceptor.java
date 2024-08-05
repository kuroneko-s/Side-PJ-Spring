package com.choidh.socketserver.socket.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompReceiveInterceptor implements ChannelInterceptor {
    private final String SUB_LINE_PREFIX = "/sub/line/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

        log.info("send - {}", StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand()));

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
