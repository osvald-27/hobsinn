package com.hobsinnovations.hobsinn.reporting

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports")
class ReportingController {

    @GetMapping("/health")
    fun health(): String {
        return "Reporting service is running"
    }
}