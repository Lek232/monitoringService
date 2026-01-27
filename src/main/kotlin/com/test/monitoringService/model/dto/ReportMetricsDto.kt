package com.test.monitoringService.model.dto

class ReportMetricsDto(

    val serviceName: String = "UNKNOWN",
    val healthStatus: String = "UNKNOWN",
    val databaseStatus: String = "UNKNOWN",
    val availability: Double = 0.0,
    val memoryLoad: Double = 0.0,
    val cpuUsage: Double = 0.0,
    val threadsLive: Long = 0,
    val databaseLoad: Double = 0.0,
    val consumptionDifference: Long = 0,
)