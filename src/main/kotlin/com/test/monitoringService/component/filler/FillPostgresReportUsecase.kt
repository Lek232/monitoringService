package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.PostgresDao
import com.test.monitoringService.model.dto.ReportPostgresDto
import org.springframework.stereotype.Component

@Component
class FillPostgresReportUsecase(
    val postgresDao: PostgresDao,
) {
    fun fillReportDto(services: List<String>): List<ReportPostgresDto> =
        services.map {
            val postgres = postgresDao.getPostgres(it)
            ReportPostgresDto(
                serviceName = postgres.serviceName,
                totalQueries = postgres.totalQueries,
                totalCalls = postgres.totalCalls,
                maxTotalTimeMs = postgres.maxTotalTimeMs,
                avgExecTimeMs = postgres.avgExecTimeMs,
                maxStddevExecTimeMs = postgres.maxStddevExecTimeMs,
                avgCacheHit = postgres.avgCacheHit,
            )
        }
}
