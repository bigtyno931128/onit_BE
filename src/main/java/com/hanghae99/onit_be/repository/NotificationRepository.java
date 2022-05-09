package com.hanghae99.onit_be.repository;


import com.hanghae99.onit_be.noti.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
