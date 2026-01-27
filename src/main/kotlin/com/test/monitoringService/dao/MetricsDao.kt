package com.test.monitoringService.dao

import com.test.monitoringService.model.dto.ReportMetricsDto
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class MetricsDao(
    val entityManager: EntityManager
) {

    /**
     * TODO Используй [jakarta.persistence.EntityManager] и всместо Map<String, Any> Возращай DTO
     *
     *         entityManager.createNativeQuery(sql, IMetricResponse::class.java)
     *             .setParameter("service_name", serviceName)
     *             .singleResultOrNull as List<IMetricResponse>
     */
    fun getMetrics(serviceName: String): ReportMetricsDto {
        val sql = """
            WITH last_minute_data AS (
            SELECT *
            FROM metrics_table
            WHERE service_name = :service_name
              AND created_at >= NOW() - INTERVAL '1 minute'
        ),
        last_record AS (
            SELECT  service_name, 
                    health_status,
                    database_status,
                    sessions_active_current
            FROM last_minute_data ORDER BY created_at DESC LIMIT 1
        ),
        first_record AS (
            SELECT sessions_active_current 
            FROM last_minute_data ORDER BY created_at ASC LIMIT 1
        ),
        aggregates AS (
            SELECT
                ROUND(AVG(server_requests_success_count * 100 / NULLIF(server_requests_count, 0)), 2) AS availability,
                ROUND(AVG((memory_used_heap * 100) / NULLIF(memory_max_heap, 0)), 2) AS memory_load,
                ROUND(AVG(process_cpu_usage * 100), 2) AS cpu_usage,
                ROUND(AVG(threads_live), 0) AS threads_live,
                ROUND(AVG(jdbc_connections_active * 100 / NULLIF(jdbc_connections_max, 0)), 2) AS db_load
            FROM last_minute_data
        )
        SELECT
            COALESCE(lr.service_name, 'UNKNOWN') AS serviceName,
            COALESCE(lr.health_status, 'UNKNOWN') AS healthStatus,
            COALESCE(lr.database_status, 'UNKNOWN') AS databaseStatus,
            COALESCE(ag.availability, 0.0) AS availability,
            COALESCE(ag.memory_load, 0.0) AS memoryLoad,
            COALESCE(ag.cpu_usage, 0.0) AS cpuUsage,
            COALESCE(ag.threads_live, 0) AS threadsLive,
            COALESCE(ag.db_load, 0.0) AS dbLoad,
            COALESCE(lr.sessions_active_current - fr.sessions_active_current, 0) AS consumptionDifference
        FROM last_record lr
        CROSS JOIN aggregates ag
        CROSS JOIN first_record fr;
        """.trimIndent()

        val metricsQueryResult = entityManager
            .createNativeQuery(sql, MetricsQueryToReport::class.java)
            .setParameter("service_name", serviceName)
            .singleResultOrNull as MetricsQueryToReport

        return ReportMetricsDto(
            serviceName = serviceName,
            healthStatus = metricsQueryResult.healthStatus,
            databaseStatus = metricsQueryResult.databaseStatus,
            availability = metricsQueryResult.availability.toDouble(),
            memoryLoad = metricsQueryResult.memoryLoad.toDouble(),
            cpuUsage = metricsQueryResult.cpuUsage.toDouble(),
            threadsLive = metricsQueryResult.threadsLive.toLong(),
            databaseLoad = metricsQueryResult.dbLoad.toDouble(),
            consumptionDifference = metricsQueryResult.consumptionDifference.toLong(),
            )
    }
}

data class MetricsQueryToReport(
    val serviceName: String,
    val healthStatus: String,
    val databaseStatus: String,
    val availability: BigDecimal,
    val memoryLoad: BigDecimal,
    val cpuUsage: BigDecimal,
    val threadsLive: BigDecimal,
    val dbLoad: BigDecimal,
    val consumptionDifference: BigDecimal,
)

//interface IMetricResponse {
//    fun getHealthStatus(): String?
//    fun getDatabaseStatus(): String?
//    fun getAvailability(): Double?
//    fun getMemoryLoad(): Double?
//    fun getCpuUsage(): Double?
//    fun getThreadsLive(): Int?
//    fun getDbLoad(): Double?
//    fun getConsumptionDifference(): Int?
//}

