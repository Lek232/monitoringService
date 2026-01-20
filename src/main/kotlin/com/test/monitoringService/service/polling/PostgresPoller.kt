package com.test.monitoringService.service.polling

import com.test.monitoringService.model.dto.CreatePostgresMetricsEntityDto
import com.test.monitoringService.service.RestClientService
import org.springframework.stereotype.Service
import tools.jackson.module.kotlin.jacksonObjectMapper

@Service
class PostgresPoller(
    val response: RestClientService,
) {
    val mapper = jacksonObjectMapper()

    fun pollingPostgres(serviceName: String, url: String, apiKey: String): CreatePostgresMetricsEntityDto {
        val postgres = mapper.readTree(
            response.getPostgres(url, apiKey)
        )
        return CreatePostgresMetricsEntityDto(
            serviceName = serviceName,
            totalQueries = postgres["total_queries"]?.asLong() ?: 0,
            totalCalls = postgres["total_calls"]?.asLong() ?: 0,
            maxTotalTime = postgres["max_total_time_ms"]?.asDouble() ?: 0.0,
            avgExecTime = postgres["avg_exec_time_ms"]?.asDouble() ?: 0.0,
            maxStddevExecTime = postgres["max_stddev_exec_time_ms"]?.asDouble() ?: 0.0,
            avgCacheHit = postgres["avg_cache_hit"]?.asDouble() ?: 0.0,
        )
    }
}