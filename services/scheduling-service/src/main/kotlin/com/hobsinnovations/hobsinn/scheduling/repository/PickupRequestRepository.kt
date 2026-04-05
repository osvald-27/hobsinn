package com.hobsinnovations.hobsinn.scheduling.repository

import com.hobsinnovations.hobsinn.scheduling.domain.PickupRequest
import com.hobsinnovations.hobsinn.scheduling.domain.PickupStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface PickupRequestRepository : JpaRepository<PickupRequest, UUID> {

    fun findByIdempotencyKey(key: String): PickupRequest?

    fun findByRequestingUserIdAndStatusIn(
        userId: UUID,
        statuses: List<PickupStatus>
    ): List<PickupRequest>

    /** All PENDING special calls visible to pickup users */
    @Query("""
        SELECT r FROM PickupRequest r
        WHERE r.status = 'PENDING'
          AND r.requestType = 'SPECIAL_CALL'
          AND r.requestedTime > :now
        ORDER BY r.createdAt ASC
    """)
    fun findOpenSpecialCalls(@Param("now") now: Instant): List<PickupRequest>

    /** CONFIRMED requests where pickup user may be late */
    @Query("""
        SELECT r FROM PickupRequest r
        WHERE r.status = 'CONFIRMED'
          AND r.requestedTime BETWEEN :from AND :to
    """)
    fun findConfirmedInWindow(
        @Param("from") from: Instant,
        @Param("to") to: Instant
    ): List<PickupRequest>
}
