package com.hobsinnovations.hobsinn.events

import com.hobsinnovations.hobsinn.domain.GeoPoint
import java.time.Instant
import java.util.UUID

// ── Base ──────────────────────────────────────────────────────────────
sealed class HobsinnEvent {
    abstract val eventId:    UUID
    abstract val occurredAt: Instant
}

// ── User Events ───────────────────────────────────────────────────────
data class UserRegisteredEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val userId:       UUID,
    val phoneNumber:  String,
    val role:         String,
    val language:     String
) : HobsinnEvent()

data class UserRatingUpdatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val userId:       UUID,
    val newScore:     Double,
    val starRating:   Double
) : HobsinnEvent()

data class BadgeAwardedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val userId:     UUID,
    val badgeType:  String,
    val reason:     String
) : HobsinnEvent()

// ── Pickup / Scheduling Events ────────────────────────────────────────
data class PickupRequestCreatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:       UUID,
    val requestingUserId: UUID,
    val location:        GeoPoint,
    val requestedTime:   Instant,
    val bagCount:        Int,
    val totalCostXaf:    Long,
    val idempotencyKey:  String
) : HobsinnEvent()

data class PickupRequestMatchedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:   UUID,
    val providerId:  UUID,
    val homeUserId:  UUID
) : HobsinnEvent()

data class PickupRequestConfirmedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:     UUID,
    val transactionId: UUID
) : HobsinnEvent()

data class PickupRequestInProgressEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:  UUID,
    val providerId: UUID
) : HobsinnEvent()

data class PickupRequestCompletedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:   UUID,
    val providerId:  UUID,
    val homeUserId:  UUID,
    val jobScorePct: Double
) : HobsinnEvent()

data class PickupRequestCancelledEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:  UUID,
    val cancelledBy: String,
    val reason:      String,
    val refundXaf:   Long
) : HobsinnEvent()

data class PickupRequestReassignedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val requestId:   UUID,
    val oldProviderId: UUID,
    val reason:      String
) : HobsinnEvent()

// ── Payment Events ────────────────────────────────────────────────────
data class PaymentInitiatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val transactionId:  UUID,
    val sourceType:     String,
    val sourceId:       UUID,
    val amountXaf:      Long,
    val payerMsisdn:    String
) : HobsinnEvent()

data class PaymentConfirmedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val transactionId:       UUID,
    val sourceId:            UUID,
    val amountXaf:           Long,
    val gatewayTransactionId: String
) : HobsinnEvent()

data class EscrowReleasedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val transactionId: UUID,
    val recipientId:   UUID,
    val amountXaf:     Long
) : HobsinnEvent()

data class PaymentRefundedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val transactionId: UUID,
    val payerId:       UUID,
    val amountXaf:     Long,
    val reason:        String
) : HobsinnEvent()

// ── Reporting Events ──────────────────────────────────────────────────
data class DumpReportCreatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val reportId:    UUID,
    val reporterId:  UUID,
    val location:    GeoPoint,
    val ambassadorId: UUID
) : HobsinnEvent()

data class DumpReportValidatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val reportId:      UUID,
    val ambassadorId:  UUID,
    val volumeM3:      Double,
    val cleanupCostXaf: Long
) : HobsinnEvent()

data class IncidentReportCreatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val incidentId:   UUID,
    val reporterId:   UUID,
    val location:     GeoPoint,
    val severityLevel: String
) : HobsinnEvent()

data class HotspotDetectedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val hotspotId:       UUID,
    val centroidLat:     Double,
    val centroidLng:     Double,
    val radiusMeters:    Double,
    val reportCount:     Int,
    val dominantSeverity: String
) : HobsinnEvent()

// ── Campaign Events ───────────────────────────────────────────────────
data class CampaignCreatedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val campaignId:     UUID,
    val campaignType:   String,
    val title:          String,
    val fundingTargetXaf: Long?
) : HobsinnEvent()

data class CampaignFundedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val campaignId:      UUID,
    val totalRaisedXaf:  Long,
    val contributorCount: Int
) : HobsinnEvent()

data class CampaignCompletedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val campaignId:    UUID,
    val ambassadorId:  UUID,
    val celebrationText: String
) : HobsinnEvent()

data class VolunteerRegisteredEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val campaignId:  UUID,
    val userId:      UUID,
    val fullName:    String,
    val committedDate: Instant
) : HobsinnEvent()

data class CertificateIssuedEvent(
    override val eventId:    UUID    = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val userId:          UUID,
    val campaignId:      UUID,
    val volunteerProgram: String,
    val communityName:   String,
    val ambassadorName:  String,
    val completionDate:  Instant
) : HobsinnEvent()
