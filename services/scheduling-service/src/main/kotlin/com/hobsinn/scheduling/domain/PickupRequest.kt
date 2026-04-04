package com.hobsinn.scheduling.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

enum class PickupStatus {
    PENDING, INVALID, MATCHED, CONFIRMED,
    IN_PROGRESS, COMPLETED, CANCELLED, REASSIGNED
}

enum class RequestType { SPECIAL_CALL, GENERAL }

@Entity
@Table(name = "pickup_requests")
class PickupRequest(

    @Id
    @Column(name = "request_id")
    val requestId: UUID = UUID.randomUUID(),

    @Column(name = "requesting_user_id", nullable = false)
    val requestingUserId: UUID,

    @Column(name = "assigned_provider_id")
    var assignedProviderId: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    val requestType: RequestType = RequestType.SPECIAL_CALL,

    @Column(name = "location_lat", nullable = false)
    val locationLat: BigDecimal,

    @Column(name = "location_lng", nullable = false)
    val locationLng: BigDecimal,

    @Column(name = "bag_count", nullable = false)
    val bagCount: Int,

    @Column(name = "estimated_volume_m3")
    val estimatedVolumeM3: BigDecimal? = null,

    @Column(name = "estimated_cost_xaf", nullable = false)
    val estimatedCostXaf: BigDecimal,

    @Column(name = "platform_fee_xaf", nullable = false)
    val platformFeeXaf: BigDecimal,

    @Column(name = "total_cost_xaf", nullable = false)
    val totalCostXaf: BigDecimal,

    @Column(name = "requested_time", nullable = false)
    val requestedTime: Instant,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PickupStatus = PickupStatus.PENDING,

    @Column(name = "idempotency_key", nullable = false, unique = true)
    val idempotencyKey: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    /** True if we are within 1 hour of the agreed pickup time */
    fun isInDangerZone(): Boolean =
        Instant.now().isAfter(requestedTime.minusSeconds(3600))

    /** Transition with guard — throws if the transition is illegal */
    fun transitionTo(newStatus: PickupStatus) {
        val allowed = when (status) {
            PickupStatus.PENDING -> setOf(PickupStatus.MATCHED, PickupStatus.CANCELLED, PickupStatus.INVALID)
            PickupStatus.MATCHED -> setOf(PickupStatus.CONFIRMED, PickupStatus.PENDING, PickupStatus.CANCELLED)
            PickupStatus.CONFIRMED -> setOf(PickupStatus.IN_PROGRESS, PickupStatus.REASSIGNED, PickupStatus.CANCELLED)
            PickupStatus.IN_PROGRESS-> setOf(PickupStatus.COMPLETED)
            else -> emptySet()
        }
        require(newStatus in allowed) {
            "Illegal transition $status → $newStatus for request $requestId"
        }
        status = newStatus
        updatedAt = Instant.now()
    }
}
