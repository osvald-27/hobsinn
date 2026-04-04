package com.hobsinn.scheduling.api

import com.hobsinn.scheduling.domain.PickupRequest
import com.hobsinn.scheduling.domain.PickupStatus
import com.hobsinn.scheduling.service.CreateSpecialCallCommand
import com.hobsinn.scheduling.service.SchedulingService
import com.hobsinn.scheduling.service.SpecialCallResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class CreateSpecialCallRequest(
    val requestingUserId: UUID,
    val locationLat: BigDecimal,
    val locationLng: BigDecimal,
    val bagCount: Int,
    val requestedTime: Instant
)

data class MatchRequest(val pickupUserId: UUID)

data class PickupRequestSummary(
    val requestId: UUID,
    val status: PickupStatus,
    val bagCount: Int,
    val totalCostXaf: BigDecimal,
    val requestedTime: Instant,
    val assignedProviderId: UUID?
)

fun PickupRequest.toSummary() = PickupRequestSummary(
    requestId = requestId,
    status = status,
    bagCount = bagCount,
    totalCostXaf = totalCostXaf,
    requestedTime = requestedTime,
    assignedProviderId = assignedProviderId
)

@RestController
@RequestMapping("/v1/pickups")
class SchedulingController(private val schedulingService: SchedulingService) {

    @PostMapping("/special-call")
    fun createSpecialCall(
        @RequestBody body: CreateSpecialCallRequest
    ): ResponseEntity<SpecialCallResponse> {
        val response = schedulingService.createSpecialCall(
            CreateSpecialCallCommand(
                requestingUserId = body.requestingUserId,
                locationLat = body.locationLat,
                locationLng = body.locationLng,
                bagCount = body.bagCount,
                requestedTime = body.requestedTime
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/open")
    fun getOpenSpecialCalls(): ResponseEntity<List<PickupRequestSummary>> {
        val open = schedulingService.getOpenSpecialCalls()
        return ResponseEntity.ok(open.map { it.toSummary() })
    }

    @PostMapping("/{requestId}/match")
    fun matchRequest(
        @PathVariable requestId: UUID,
        @RequestBody body: MatchRequest
    ): ResponseEntity<PickupRequestSummary> {
        val updated = schedulingService.matchRequest(requestId, body.pickupUserId)
        return ResponseEntity.ok(updated.toSummary())
    }

    @PostMapping("/{requestId}/confirm-payment")
    fun confirmPayment(
        @PathVariable requestId: UUID
    ): ResponseEntity<PickupRequestSummary> {
        val updated = schedulingService.confirmPayment(requestId)
        return ResponseEntity.ok(updated.toSummary())
    }

    @PostMapping("/{requestId}/start")
    fun startJob(
        @PathVariable requestId: UUID,
        @RequestParam homeUserId: UUID
    ): ResponseEntity<PickupRequestSummary> {
        val updated = schedulingService.startJob(requestId, homeUserId)
        return ResponseEntity.ok(updated.toSummary())
    }

    @PostMapping("/{requestId}/complete")
    fun completeJob(
        @PathVariable requestId: UUID,
        @RequestParam pickupUserId: UUID
    ): ResponseEntity<PickupRequestSummary> {
        val updated = schedulingService.completeJob(requestId, pickupUserId)
        return ResponseEntity.ok(updated.toSummary())
    }

    @PostMapping("/{requestId}/cancel")
    fun cancelRequest(
        @PathVariable requestId: UUID,
        @RequestParam actorId: UUID
    ): ResponseEntity<PickupRequestSummary> {
        val updated = schedulingService.cancelRequest(requestId, actorId)
        return ResponseEntity.ok(updated.toSummary())
    }

    @GetMapping("/health")
    fun health() = ResponseEntity.ok(mapOf("status" to "UP", "service" to "scheduling-service"))
}
