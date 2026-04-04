package com.hobsinnovations.hobsinn.domain

import java.time.Instant

/**
 * Represents a pickup time window.
 * Business rules enforced at construction:
 *   - end must be after start
 *   - minimum window of 1 hour (design spec: special call must be >= 1hr in future)
 *   - start cannot be in the past (60 second tolerance for network latency)
 */
data class TimeWindow(val start: Instant, val end: Instant) {
    init {
        require(end.isAfter(start)) {
            "end must be after start"
        }
        val hours = (end.epochSecond - start.epochSecond) / 3600.0
        require(hours >= 1.0) {
            "Minimum pickup window is 1 hour, got %.2f".format(hours)
        }
        require(start.isAfter(Instant.now().minusSeconds(60))) {
            "Pickup start time cannot be in the past"
        }
    }

    fun durationHours(): Double = (end.epochSecond - start.epochSecond) / 3600.0

    /**
     * Returns true if the given instant falls within this window.
     */
    fun contains(instant: Instant): Boolean =
        !instant.isBefore(start) && !instant.isAfter(end)

    /**
     * Returns true if this window is within the danger zone —
     * i.e. the start is less than dangerMinutes from now.
     * Default danger zone is 60 minutes (1 hour) per spec.
     */
    fun isInDangerZone(dangerMinutes: Long = 60): Boolean =
        Instant.now().plusSeconds(dangerMinutes * 60).isAfter(start)
}
