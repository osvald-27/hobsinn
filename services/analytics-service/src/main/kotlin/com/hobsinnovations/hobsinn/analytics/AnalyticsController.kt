package com.hobsinnovations.hobsinn.analytics

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController {

    @GetMapping("/health")
    fun health(): String {
        return "Analytics service is running"
    }
}