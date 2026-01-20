package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.PostgresEntity

data class CreatePostgresMetricsEntityDto(

    val serviceName: String = "UNKNOWN",

    val totalQueries: Long = 0,

    val totalCalls: Long = 0,

    val maxTotalTime: Double = 0.0,

    val avgExecTime: Double = 0.0,

    val maxStddevExecTime: Double = 0.0,

    val avgCacheHit: Double = 0.0,
){
    fun toPostgresEntity(): PostgresEntity =
        PostgresEntity(
            serviceName = this.serviceName,
            totalQueries = this.totalQueries,
            totalCalls = this.totalCalls,
            maxTotalTime = this.maxTotalTime,
            avgExecTime = this.avgExecTime,
            maxStddevExecTime = this.maxStddevExecTime,
            avgCacheHit = this.avgCacheHit,
        )
}