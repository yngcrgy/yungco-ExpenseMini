package edu.cit.yungco.expensemini.features.notification;

import edu.cit.yungco.expensemini.features.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);
}
