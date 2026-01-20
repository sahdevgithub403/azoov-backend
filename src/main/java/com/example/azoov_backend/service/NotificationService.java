package com.example.azoov_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to all users
     */
    public void sendToAll(String type, String title, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", type);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Send notification to specific user
     */
    public void sendToUser(String username, String type, String title, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", type);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }

    /**
     * Notification types
     */
    public static class NotificationType {
        public static final String SUCCESS = "success";
        public static final String ERROR = "error";
        public static final String WARNING = "warning";
        public static final String INFO = "info";
    }
}
