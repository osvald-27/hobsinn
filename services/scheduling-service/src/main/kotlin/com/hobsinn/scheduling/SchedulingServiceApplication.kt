package com.hobsinn.scheduling

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SchedulingServiceApplication

fun main(args: Array<String>) {
    runApplication<SchedulingServiceApplication>(*args)
}
