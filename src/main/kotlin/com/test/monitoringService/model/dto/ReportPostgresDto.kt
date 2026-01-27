package com.test.monitoringService.model.dto

class ReportPostgresDto(
    val serviceName: String = "",
    val totalQueries: Long = 0,
    val totalCalls: Long = 0,
    val maxTotalTimeMs: Double = 0.0,
    val avgExecTimeMs: Double = 0.0,
    val maxStddevExecTimeMs: Double = 0.0,
    val avgCacheHit: Double = 0.0,
)

