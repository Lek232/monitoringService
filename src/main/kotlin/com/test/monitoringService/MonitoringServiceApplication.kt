package com.test.monitoringService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
class MonitoringServiceApplication

fun main(args: Array<String>) {
    runApplication<MonitoringServiceApplication>(*args)
}
