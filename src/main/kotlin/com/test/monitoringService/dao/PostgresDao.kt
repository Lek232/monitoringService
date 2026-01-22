package com.test.monitoringService.dao

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class PostgresDao(
    val jdbcTemplate: NamedParameterJdbcTemplate
) {

    /**
     * TODO Используй [jakarta.persistence.EntityManager] и всместо Map<String, Any> Возращай DTO
     */
    fun getPostgres(serviceName: String): Map<String, Any>? {
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

        val param = mapOf("service_name" to serviceName)

        return jdbcTemplate.query(sql, param) { rs, _ ->
            mapOf(
                "service_name" to rs.getString("service_name"),
                "total_queries" to rs.getLong("total_queries"),
                "total_calls" to rs.getLong("total_calls"),
                "max_total_time_ms" to rs.getDouble("max_total_time_ms"),
                "avg_exec_time_ms" to rs.getDouble("avg_exec_time_ms"),
                "max_stddev_exec_time_ms" to rs.getDouble("max_stddev_exec_time_ms"),
                "avg_cache_hit" to rs.getDouble("avg_cache_hit")
            )
        }.firstOrNull()
    }
}
