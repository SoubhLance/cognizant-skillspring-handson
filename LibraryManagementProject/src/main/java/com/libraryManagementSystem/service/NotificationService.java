package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getUserNotifications(Long userId);
    void markAsRead(Long notificationId);
    void sendNotification(Long userId, String message, String type);
}
