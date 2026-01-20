package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.PostgresDao
import com.test.monitoringService.model.dto.ReportPostgresDto
import org.springframework.stereotype.Component

@Component
class FillPostgresReportDto(
    val postgresDao: PostgresDao,
) {
    fun fillReportDto(services: List<String>): List<ReportPostgresDto> =
        services.map {
            val postgres = postgresDao.getPostgres(it)
            ReportPostgresDto(
                serviceName = (postgres?.get("service_name") ?: "") as String,
                totalQueries = (postgres?.get("total_queries") ?: 0L) as Long,
                totalCalls = (postgres?.get("total_calls") ?: 0L) as Long,
                maxTotalTimeMs = (postgres?.get("max_total_time_ms") ?: 0.0) as Double,
                avgExecTimeMs = (postgres?.get("avg_exec_time_ms") ?: 0.0) as Double,
                maxStddevExecTimeMs = (postgres?.get("max_stddev_exec_time_ms") ?: 0.0) as Double,
                avgCacheHit = (postgres?.get("avg_cache_hit") ?: 0.0) as Double,
            )
        }
}