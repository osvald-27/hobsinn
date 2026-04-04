package com.hobsinnovations.hobsinn.events

/**
 * All Kafka topic names for the hobsinn platform.
 * Never hardcode topic strings in services — always use these constants.
 */
object HobsinnTopics {
    const val PICKUP_EVENTS       = "hobsinn.pickup.events"
    const val PAYMENT_EVENTS      = "hobsinn.payment.events"
    const val REPORTING_EVENTS    = "hobsinn.reporting.events"
    const val CAMPAIGN_EVENTS     = "hobsinn.campaign.events"
    const val USER_EVENTS         = "hobsinn.user.events"
    const val PROVIDER_EVENTS     = "hobsinn.provider.events"
    const val NOTIFICATION_EVENTS = "hobsinn.notification.events"
    const val ANALYTICS_EVENTS    = "hobsinn.analytics.events"
}
