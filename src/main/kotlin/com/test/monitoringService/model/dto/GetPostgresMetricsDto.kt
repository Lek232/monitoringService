package com.test.monitoringService.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
class GetPostgresMetricsDto(
    val totalQueries: Long = 0,
    val totalCalls: Long = 0,
    val maxTotalTimeMs: Double = 0.0,
    val avgExecTimeMs: Double = 0.0,
    val maxStddevExecTimeMs: Double = 0.0,
    val avgCacheHit: Double = 0.0,
)