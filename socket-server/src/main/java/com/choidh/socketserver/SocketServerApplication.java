package com.choidh.socketserver;

import com.choidh.service.ServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication(scanBasePackageClasses = {SocketServerApplication.class, ServiceApplication.class})
public class SocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketServerApplication.class, args);
    }

}
