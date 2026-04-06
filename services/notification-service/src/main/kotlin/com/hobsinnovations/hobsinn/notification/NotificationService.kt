package com.hobsinnovations.hobsinn.notification

import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun createNotification(request: CreateNotificationRequest): Notification {
        val notification = Notification(
            userId = request.userId,
            title = request.title,
            message = request.message,
            type = request.type
        )
        return notificationRepository.save(notification)
    }

    fun getNotificationById(id: Long): Notification? {
        return notificationRepository.findById(id).orElse(null)
    }

    fun getNotificationsByUserId(userId: Long): List<Notification> {
        return notificationRepository.findByUserId(userId)
    }

    fun getUnreadNotificationsByUserId(userId: Long): List<Notification> {
        return notificationRepository.findByUserIdAndIsRead(userId, false)
    }

    fun markAsRead(id: Long): Notification? {
        val notification = notificationRepository.findById(id).orElse(null) ?: return null

        val updatedNotification = notification.copy(isRead = true)
        return notificationRepository.save(updatedNotification)
    }

    fun deleteNotification(id: Long): Boolean {
        if (!notificationRepository.existsById(id)) return false
        notificationRepository.deleteById(id)
        return true
    }
}

data class CreateNotificationRequest(
    val userId: Long,
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.INFO
)