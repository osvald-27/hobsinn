package com.hobsinnovations.hobsinn.notification

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, length = 1000)
    val message: String,

    @Enumerated(EnumType.STRING)
    val type: NotificationType = NotificationType.INFO,

    @Column(nullable = false)
    val isRead: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class NotificationType {
    INFO, WARNING, ERROR, SUCCESS
}