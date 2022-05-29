package com.hanghae99.onit_be.noti;


import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    void deleteAllByUser(User user);

    List<Notification> findAllByUser(User user);

    Optional<Notification> findByUserAndPlanAndAndNotificationType(User user, Plan plan, NotificationType notificationType);
}
