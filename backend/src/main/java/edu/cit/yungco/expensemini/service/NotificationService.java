package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.model.Notification;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public void sendNotification(User user, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // Push via WebSocket to specific user's topic
        messagingTemplate.convertAndSend("/topic/users/" + user.getId() + "/updates", notification);
    }
}
