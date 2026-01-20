package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.MetricsEntity

data class CreateMetricsEntityDto(

    var serviceName: String = "UNKNOWN",

    var healthStatus: String = "UNKNOWN",

    var databaseName: String = "UNKNOWN",

    var databaseStatus: String = "UNKNOWN",

    var serverRequestsCount: Long = 0,

    var serverRequestsSuccessCount: Long = 0,

    var sessionsActiveCurrent: Long = 0,

    var memoryUsedHeap: Long = 0,

    var memoryMaxHeap: Long = 0,

    var processCpuUsage: Double = 0.0,

    var threadsLive: Long = 0,

    var jdbcConnectionsActive: Long = 0,

    var jdbcConnectionsMax: Long = 0
){
    fun toMetricsEntity(): MetricsEntity =
        MetricsEntity(
            serviceName = this.serviceName,
            healthStatus = this.healthStatus,
            databaseName = this.databaseName,
            databaseStatus = this.databaseStatus,
            serverRequestsCount = this.serverRequestsCount,
            serverRequestsSuccessCount = this.serverRequestsSuccessCount,
            sessionsActiveCurrent = this.sessionsActiveCurrent,
            memoryUsedHeap = this.memoryUsedHeap,
            memoryMaxHeap = this.memoryMaxHeap,
            processCpuUsage = this.processCpuUsage,
            threadsLive = this.threadsLive,
            jdbcConnectionsActive = this.jdbcConnectionsActive,
            jdbcConnectionsMax = this.jdbcConnectionsMax,
        )
}

