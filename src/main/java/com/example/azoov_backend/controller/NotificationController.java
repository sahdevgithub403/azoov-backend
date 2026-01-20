package com.example.azoov_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to all users
     */
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Map<String, Object> sendNotification(Map<String, Object> notification) {
        return notification;
    }

    /**
     * REST endpoint to send notifications programmatically
     */
    @PostMapping("/api/notifications/send")
    @CrossOrigin(origins = "*")
    public void sendNotificationREST(@RequestBody Map<String, Object> notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Send notification to specific user
     */
    public void sendToUser(String username, String type, String message) {
        Map<String, Object> notification = Map.of(
                "type", type,
                "message", message,
                "timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }

    /**
     * Send notification to all users
     */
    public void sendToAll(String type, String message) {
        Map<String, Object> notification = Map.of(
                "type", type,
                "message", message,
                "timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
