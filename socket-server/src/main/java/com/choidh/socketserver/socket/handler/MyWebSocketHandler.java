package com.choidh.socketserver.socket.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {
    /**
     * 클라이언트와 연결 성립 후 호출
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info("client - {} connected", webSocketSession.getId());
    }

    /**
     * 클라언트로부터 메시지가 수신시 호출
     * @param webSocketMessage 메시지의 타입에 따라 TextMessage, BinaryMessage, PongMessage, PingMessage 등
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) webSocketMessage;
            log.info("Received message: {}", textMessage.getPayload());

            webSocketSession.sendMessage(new TextMessage("Echo: " + textMessage.getPayload()));
        }
    }

    /**
     * 통신 중 오류가 발생시 호출
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.info("연결 오류 - {}", webSocketSession.getId());
    }

    /**
     * 연결이 종료된 후 호출
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed with client: {} with status: {}", webSocketSession.getId(), closeStatus);
    }

    /**
     * 부분 메시지 지원 여부
     * true를 반환하면 메시지가 여러 부분으로 나뉘어 전달될 수 있으며, 이를 처리할 수 있음을 나타낸다
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
