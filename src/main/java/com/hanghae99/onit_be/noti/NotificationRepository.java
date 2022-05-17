package com.hanghae99.onit_be.noti;


import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByUser(User user);
}
