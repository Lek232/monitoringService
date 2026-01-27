package com.test.monitoringService.dao

import com.test.monitoringService.model.dto.ReportPostgresDto
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PostgresDao(
    val entityManager: EntityManager
) {

    /**
     * TODO Используй [jakarta.persistence.EntityManager] и всместо Map<String, Any> Возращай DTO
     */
    fun getPostgres(serviceName: String): ReportPostgresDto {
        val sql = """
        SELECT 
            service_name,
            total_queries,
            total_calls,
            ROUND(max_total_time_ms, 2) AS max_total_time_ms,
            ROUND(avg_exec_time_ms, 2) AS avg_exec_time_ms,
            ROUND(max_stddev_exec_time_ms, 2) AS max_stddev_exec_time_ms,
            ROUND(avg_cache_hit, 2) AS avg_cache_hit
        FROM postgres_metrics WHERE id = (SELECT MAX(id) FROM postgres_metrics) AND service_name = :service_name
               """.trimIndent()

        val postgresQueryResult = entityManager
            .createNativeQuery(sql, PostgresQueryToReport::class.java)
            .setParameter("service_name", serviceName)
            .singleResultOrNull as? PostgresQueryToReport

        return ReportPostgresDto(
            serviceName = postgresQueryResult?.serviceName ?: "",
            totalQueries = postgresQueryResult?.totalQueries?.toLong() ?: 0L,
            totalCalls = postgresQueryResult?.totalCalls?.toLong() ?: 0L,
            maxTotalTimeMs = postgresQueryResult?.maxTotalTimeMs?.toDouble() ?: 0.0,
            avgExecTimeMs = postgresQueryResult?.avgExecTimeMs?.toDouble() ?: 0.0,
            maxStddevExecTimeMs = postgresQueryResult?.maxStddevExecTimeMs?.toDouble() ?: 0.0,
            avgCacheHit = postgresQueryResult?.avgCacheHit?.toDouble() ?: 0.0,
        )
    }
}

data class PostgresQueryToReport(
    val serviceName: String?,
    val totalQueries: BigDecimal?,
    val totalCalls: BigDecimal?,
    val maxTotalTimeMs: BigDecimal?,
    val avgExecTimeMs: BigDecimal?,
    val maxStddevExecTimeMs: BigDecimal?,
    val avgCacheHit: BigDecimal?,
)
//interface IPostgresResponse {
//
//    fun getServiceName(): String
//    fun getTotalQueries(): Long
//    fun getTotalCalls(): Long
//    fun getMaxTotalTimeMs(): Double
//    fun getAvgExecTimeMs(): Double
//    fun getMaxStddevExecTimeMs(): Double
//    fun getAvgCacheHit(): Double
//}
