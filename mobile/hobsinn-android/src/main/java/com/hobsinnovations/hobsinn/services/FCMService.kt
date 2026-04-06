package com.hobsinnovations.hobsinn.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Mock checking offline drafts / pushing to local DB
        // Display notification
    }

    override fun onNewToken(token: String) {
        // Mock sending new token to user-service / notification-service
    }
}
