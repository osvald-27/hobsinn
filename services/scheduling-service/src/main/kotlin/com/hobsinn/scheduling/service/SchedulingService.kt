package com.hobsinn.scheduling.service

import com.hobsinn.scheduling.domain.*
import com.hobsinn.scheduling.repository.PickupRequestRepository
import com.hobsinn.scheduling.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.UUID

data class CreateSpecialCallCommand(
    val requestingUserId: UUID,
    val locationLat: BigDecimal,
    val locationLng: BigDecimal,
    val bagCount: Int,
    val requestedTime: Instant
)

data class SpecialCallResponse(
    val requestId: UUID,
    val status: PickupStatus,
    val estimatedCostXaf: BigDecimal,
    val platformFeeXaf: BigDecimal,
    val totalCostXaf: BigDecimal
)

@Service
@Transactional
class SchedulingService(
    private val requestRepo: PickupRequestRepository,
    private val userRepo: UserRepository
) {
    private val log = LoggerFactory.getLogger(SchedulingService::class.java)

    companion object {
        private val PLATFORM_BASE_FEE = BigDecimal("100")
        private val PLATFORM_PCT_THRESHOLD = BigDecimal("1100")
        private val PLATFORM_PCT = BigDecimal("0.05")
        private val GRACE_WINDOW_SECONDS = 20 * 60L // 20 minutes
    }

    /**
     * Create a special call pickup request.
     * Cost = estimatedCost + 100 XAF + (5% of estimatedCost if estimatedCost > 1100 XAF).
     * requestedTime must be > 1 hour from now.
     */
    fun createSpecialCall(cmd: CreateSpecialCallCommand): SpecialCallResponse {
        require(cmd.bagCount > 0) { "bagCount must be > 0" }
        require(cmd.requestedTime.isAfter(Instant.now().plusSeconds(3600))) {
            "requestedTime must be at least 1 hour in the future"
        }

        val estimatedCost = BigDecimal(cmd.bagCount * 500) // 500 XAF per bag baseline
        val platformFee = calculatePlatformFee(estimatedCost)
        val totalCost = estimatedCost + platformFee
        val idempotencyKey = "special_call:${cmd.requestingUserId}:${cmd.requestedTime.epochSecond}"

        // Idempotency: return existing if already created
        requestRepo.findByIdempotencyKey(idempotencyKey)?.let { existing ->
            log.info("Idempotent return for key=$idempotencyKey")
            return SpecialCallResponse(
                existing.requestId, existing.status,
                existing.estimatedCostXaf, existing.platformFeeXaf, existing.totalCostXaf
            )
        }

        val request = PickupRequest(
            requestingUserId = cmd.requestingUserId,
            locationLat = cmd.locationLat,
            locationLng = cmd.locationLng,
            bagCount = cmd.bagCount,
            estimatedCostXaf = estimatedCost,
            platformFeeXaf = platformFee,
            totalCostXaf = totalCost,
            requestedTime = cmd.requestedTime,
            idempotencyKey = idempotencyKey
        )
        requestRepo.save(request)
        log.info("Special call created: requestId=${request.requestId} cost=${totalCost}XAF")

        return SpecialCallResponse(
            request.requestId, request.status,
            estimatedCost, platformFee, totalCost
        )
    }

    /**
     * Pickup user accepts a special call → MATCHED.
     */
    fun matchRequest(requestId: UUID, pickupUserId: UUID): PickupRequest {
        val request = requestRepo.findById(requestId)
            .orElseThrow { IllegalArgumentException("Request $requestId not found") }
        require(request.status == PickupStatus.PENDING) {
            "Request $requestId is not PENDING (current: ${request.status})"
        }
        request.transitionTo(PickupStatus.MATCHED)
        request.assignedProviderId = pickupUserId
        return requestRepo.save(request)
    }

    /**
     * MoMo payment confirmed → CONFIRMED.
     * Must be called at least 1 hour before requestedTime.
     */
    fun confirmPayment(requestId: UUID): PickupRequest {
        val request = requestRepo.findById(requestId)
            .orElseThrow { IllegalArgumentException("Request $requestId not found") }
        require(request.status == PickupStatus.MATCHED) {
            "Request must be MATCHED to confirm payment (current: ${request.status})"
        }
        require(!request.isInDangerZone()) {
            "Payment confirmation window expired — request is in danger zone"
        }
        request.transitionTo(PickupStatus.CONFIRMED)
        return requestRepo.save(request)
    }

    /**
     * Home user acknowledges pickup user arrival → IN_PROGRESS.
     */
    fun startJob(requestId: UUID, homeUserId: UUID): PickupRequest {
        val request = requestRepo.findById(requestId)
            .orElseThrow { IllegalArgumentException("Request $requestId not found") }
        require(request.requestingUserId == homeUserId) { "User mismatch" }
        request.transitionTo(PickupStatus.IN_PROGRESS)
        return requestRepo.save(request)
    }

    /**
     * Pickup user submits completion photo → COMPLETED.
     */
    fun completeJob(requestId: UUID, pickupUserId: UUID): PickupRequest {
        val request = requestRepo.findById(requestId)
            .orElseThrow { IllegalArgumentException("Request $requestId not found") }
        require(request.assignedProviderId == pickupUserId) { "Provider mismatch" }
        request.transitionTo(PickupStatus.COMPLETED)
        return requestRepo.save(request)
    }

    /**
     * Cancel a request. Full refund if > 1hr away, partial if in danger zone.
     */
    fun cancelRequest(requestId: UUID, actorId: UUID): PickupRequest {
        val request = requestRepo.findById(requestId)
            .orElseThrow { IllegalArgumentException("Request $requestId not found") }
        require(request.status !in setOf(PickupStatus.IN_PROGRESS, PickupStatus.COMPLETED)) {
            "Cannot cancel a request that is IN_PROGRESS or COMPLETED"
        }
        request.transitionTo(PickupStatus.CANCELLED)
        return requestRepo.save(request)
    }

    /**
     * Reassign: called when pickup user is > 20 minutes late.
     */
    fun reassignRequest(requestId: UUID): PickupRequest {
        val request = requestRepo.findById(requestId)
            .orElseThrow { IllegalArgumentException("Request $requestId not found") }
        require(request.status == PickupStatus.CONFIRMED) {
            "Can only reassign CONFIRMED requests"
        }
        request.transitionTo(PickupStatus.REASSIGNED)
        request.assignedProviderId = null
        return requestRepo.save(request)
    }

    /**
     * Scheduled every minute: check CONFIRMED requests whose pickup time
     * has passed the 20-minute grace window and auto-reassign.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    fun checkLatePickups() {
        val now = Instant.now()
        val lateThreshold = now.minusSeconds(GRACE_WINDOW_SECONDS)
        val at_risk = requestRepo.findConfirmedInWindow(
            from = now.minusSeconds(7200), // 2hr window to check
            to = lateThreshold
        )
        at_risk.forEach { request ->
            log.warn("Auto-reassigning late pickup: requestId=${request.requestId}")
            reassignRequest(request.requestId)
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private fun calculatePlatformFee(estimatedCost: BigDecimal): BigDecimal {
        val base = PLATFORM_BASE_FEE
        val pct = if (estimatedCost > PLATFORM_PCT_THRESHOLD)
            estimatedCost.multiply(PLATFORM_PCT).setScale(2, RoundingMode.HALF_UP)
        else BigDecimal.ZERO
        return base + pct
    }
}
