package com.test.monitoringService.service.polling

import com.test.monitoringService.model.dto.CreatePostgresMetricsEntityDto
import com.test.monitoringService.service.RestClientService
import org.springframework.stereotype.Service

@Service
class PostgresPoller(
    val response: RestClientService,
) {

    fun pollingPostgres(serviceName: String, url: String, apiKey: String): CreatePostgresMetricsEntityDto {

        val postgres = response.getPostgres(url, apiKey)

        return CreatePostgresMetricsEntityDto(
            serviceName = serviceName,
            totalQueries = postgres.totalQueries,
            totalCalls = postgres.totalCalls,
            maxTotalTime = postgres.maxTotalTimeMs,
            avgExecTime = postgres.avgExecTimeMs,
            maxStddevExecTime = postgres.maxStddevExecTimeMs,
            avgCacheHit = postgres.avgCacheHit,
        )
    }
}
