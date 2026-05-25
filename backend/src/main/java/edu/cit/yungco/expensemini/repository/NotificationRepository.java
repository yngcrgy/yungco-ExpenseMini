package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Notification n WHERE n.user.id = :userId")
    void deleteAllByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
