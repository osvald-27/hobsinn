package com.hobsinnovations.hobsinn.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserId(userId: Long): List<Notification>
    fun findByUserIdAndIsRead(userId: Long, isRead: Boolean): List<Notification>
}