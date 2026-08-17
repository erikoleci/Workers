package com.buildcrew.notification;

public class NotificationDTO {
    public String id;
    public String type;
    public String message;
    public boolean isRead;
    public String createdAt;

    public static NotificationDTO from(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.id = n.id.toString();
        dto.type = n.type;
        dto.message = n.message;
        dto.isRead = n.isRead;
        dto.createdAt = n.createdAt.toString();
        return dto;
    }
}
