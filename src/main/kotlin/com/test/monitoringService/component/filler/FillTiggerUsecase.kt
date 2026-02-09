package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.MetricsDao
import com.test.monitoringService.dao.PostgresDao
import com.test.monitoringService.model.dto.TriggerDto
import org.springframework.stereotype.Component

@Component
class FillTiggerUsecase(
    val postgresDao: PostgresDao,
    val metricsDao: MetricsDao,
) {
    fun fillTriggerDto(services: List<String>): List<TriggerDto> =
        services.map {
            val metrics = metricsDao.getMetrics(it)
            if (metrics.healthStatus == "UNKNOWN") {
                TriggerDto(serviceName = it)
            }
            else {
                val postgres = postgresDao.getPostgres(it)
                TriggerDto(
                    serviceName = metrics.serviceName,
                    healthStatus = metrics.healthStatus,
                    databaseStatus = metrics.databaseStatus,
                    availability = metrics.availability,
                    memoryLoad = metrics.memoryLoad,
                    cpuUsage = metrics.cpuUsage,
                    threadsLive = metrics.threadsLive,
                    databaseLoad = metrics.databaseLoad,
                    consumptionDifference = metrics.consumptionDifference,
                    totalQueries = postgres.totalQueries,
                    totalCalls = postgres.totalCalls,
                    maxTotalTimeMs = postgres.maxTotalTimeMs,
                    avgExecTimeMs = postgres.avgExecTimeMs,
                    maxStddevExecTimeMs = postgres.maxStddevExecTimeMs,
                    avgCacheHit = postgres.avgCacheHit,
                )
            }
        }
}