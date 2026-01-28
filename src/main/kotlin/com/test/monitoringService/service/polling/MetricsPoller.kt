package com.test.monitoringService.service.polling

import com.test.monitoringService.model.dto.CreateMetricsEntityDto
import com.test.monitoringService.service.RestClientService
import org.springframework.stereotype.Service

@Service
class MetricsPoller(
    val response: RestClientService,
) {

    fun pollingMetrics(url: String, serviceName: String, apiKey: String): CreateMetricsEntityDto {
// TODO Маппим не в дерево, а в DTO
        val health = response.getHealth(url, apiKey)

        val healthStatus = health.status

        val dbName = health.components?.db?.details?.database ?: "UNKNOWN"

        val databaseStatus = when {
            health.components?.db != null ->
                health.components.db.status
            else -> "Null"
        }

        var jdbcConnectionsActive: Long = 0

        var jdbcConnectionsMax: Long = 0

        if (dbName == "PostgreSQL" && databaseStatus == "UP") {
            jdbcConnectionsActive = response.getMetric(
                url,
                "jdbc.connections.active",
                "VALUE",
                apiKey
            ).toLong()
            jdbcConnectionsMax = response.getMetric(
                url,
                "jdbc.connections.max",
                "VALUE",
                apiKey
            ).toLong()
        }
        return CreateMetricsEntityDto(
            serviceName = serviceName,
            databaseName = dbName,
            healthStatus = healthStatus,
            databaseStatus = databaseStatus,
            jdbcConnectionsActive = jdbcConnectionsActive,
            jdbcConnectionsMax = jdbcConnectionsMax,
            serverRequestsCount = response.getMetric(
                url,
                "http.server.requests",
                "COUNT",
                apiKey
            ).toLong(),
            serverRequestsSuccessCount = response.getMetric(
                url,
                "http.server.requests?tag=outcome:SUCCESS",
                "COUNT",
                apiKey
            ).toLong(),
            sessionsActiveCurrent = response.getMetric(
                url,
                "tomcat.sessions.active.current",
                "VALUE",
                apiKey
            ).toLong(),
            memoryUsedHeap = response.getMetric(
                url,
                "jvm.memory.used?tag=area:heap",
                "VALUE",
                apiKey
            ).toLong(),
            memoryMaxHeap = response.getMetric(
                url,
                "jvm.memory.max?tag=area:heap",
                "VALUE",
                apiKey
            ).toLong(),
            processCpuUsage = response.getMetric(
                url,
                "process.cpu.usage",
                "VALUE",
                apiKey
            ),
            threadsLive = response.getMetric(
                url,
                "jvm.threads.live",
                "VALUE",
                apiKey
            ).toLong(),
        )
    }
}
