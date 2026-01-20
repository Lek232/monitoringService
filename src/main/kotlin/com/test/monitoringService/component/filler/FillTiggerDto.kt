package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.MetricsDao
import com.test.monitoringService.dao.PostgresDao
import com.test.monitoringService.model.dto.TriggerDto
import org.springframework.stereotype.Component

@Component
class FillTiggerDto(
    val postgresDao: PostgresDao,
    val metricsDao: MetricsDao,
) {
    fun fillTriggerDto(services: List<String>): List<TriggerDto> =
        services.map {
            val metrics = metricsDao.getMetrics(it)
            val postgres = postgresDao.getPostgres(it)
            TriggerDto(
                serviceName = it,
                healthStatus = metrics["health_status"].toString(),
                databaseStatus = metrics["database_status"].toString(),
                availability = metrics["availability"] as Double,
                memoryLoad = metrics["memory_load"] as Double,
                cpuUsage = metrics["cpu_usage"] as Double,
                threadsLive = metrics["threads_live"] as Int,
                databaseLoad = metrics["db_load"] as Double,
                consumptionDifference = metrics["consumption_growth"] as Int,
                totalQueries = (postgres?.get("total_queries") ?: 0L) as Long,
                totalCalls = (postgres?.get("total_calls") ?: 0L) as Long,
                maxTotalTimeMs = (postgres?.get("max_total_time_ms") ?: 0.0) as Double,
                avgExecTimeMs = (postgres?.get("avg_exec_time_ms") ?: 0.0) as Double,
                maxStddevExecTimeMs = (postgres?.get("max_stddev_exec_time_ms") ?: 0.0) as Double,
                avgCacheHit = (postgres?.get("avg_cache_hit") ?: 0.0) as Double,
            )
        }
}