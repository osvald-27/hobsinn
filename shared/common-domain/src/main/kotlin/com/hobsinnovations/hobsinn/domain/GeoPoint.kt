package com.hobsinnovations.hobsinn.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Immutable GPS coordinate.
 * Validated against Cameroon's geographic bounding box on construction.
 * Haversine formula gives accurate surface distance between two points.
 */
data class GeoPoint @JsonCreator constructor(
    @JsonProperty("latitude")  val latitude:  Double,
    @JsonProperty("longitude") val longitude: Double
) {
    init {
        require(latitude  in 1.6..13.1) {
            "Latitude $latitude is outside Cameroon bounds [1.6, 13.1]"
        }
        require(longitude in 8.4..16.2) {
            "Longitude $longitude is outside Cameroon bounds [8.4, 16.2]"
        }
    }

    /**
     * Haversine distance in kilometres — accounts for Earth's curvature.
     * Used by MatchingEngine and HotspotDetector.
     */
    fun distanceKmTo(other: GeoPoint): Double {
        val r    = 6371.0
        val dLat = Math.toRadians(other.latitude  - latitude)
        val dLng = Math.toRadians(other.longitude - longitude)
        val a    = Math.sin(dLat / 2).let { it * it } +
                   Math.cos(Math.toRadians(latitude)) *
                   Math.cos(Math.toRadians(other.latitude)) *
                   Math.sin(dLng / 2).let { it * it }
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }

    fun distanceMetresTo(other: GeoPoint): Double = distanceKmTo(other) * 1000
}
