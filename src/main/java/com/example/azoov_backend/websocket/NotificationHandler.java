package com.example.azoov_backend.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationHandler {

    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public String handleNotification(String message) {
        return message;
    }
}
