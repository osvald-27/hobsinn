package com.hobsinnovations.hobsinn.notification

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping
    fun createNotification(@RequestBody request: CreateNotificationRequest): ResponseEntity<Notification> {
        val notification = notificationService.createNotification(request)
        return ResponseEntity.ok(notification)
    }

    @GetMapping("/{id}")
    fun getNotification(@PathVariable id: Long): ResponseEntity<Notification> {
        val notification = notificationService.getNotificationById(id)
        return if (notification != null) ResponseEntity.ok(notification) else ResponseEntity.notFound().build()
    }

    @GetMapping("/user/{userId}")
    fun getNotificationsByUser(@PathVariable userId: Long): ResponseEntity<List<Notification>> {
        val notifications = notificationService.getNotificationsByUserId(userId)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/user/{userId}/unread")
    fun getUnreadNotificationsByUser(@PathVariable userId: Long): ResponseEntity<List<Notification>> {
        val notifications = notificationService.getUnreadNotificationsByUserId(userId)
        return ResponseEntity.ok(notifications)
    }

    @PutMapping("/{id}/read")
    fun markAsRead(@PathVariable id: Long): ResponseEntity<Notification> {
        val notification = notificationService.markAsRead(id)
        return if (notification != null) ResponseEntity.ok(notification) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteNotification(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = notificationService.deleteNotification(id)
        return if (deleted) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }
}