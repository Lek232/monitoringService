package com.test.monitoringService.dao

import com.test.monitoringService.component.telegramBot.BotProperties
import jakarta.persistence.EntityManager
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class MetricsDao(
    val jdbcTemplate: NamedParameterJdbcTemplate,
    val entityManager: EntityManager
) {

    /**
     * TODO Используй [jakarta.persistence.EntityManager] и всместо Map<String, Any> Возращай DTO
     *
     *         entityManager.createNativeQuery(sql, IMetricResponse::class.java)
     *             .setParameter("service_name", serviceName)
     *
     *             .singleResultOrNull as List<IMetricResponse>
     */
    fun getMetrics(serviceName: String): Map<String, Any> {
        val sql = """
        WITH last_minute_data AS (
            SELECT *
            FROM metrics
            WHERE service_name = :service_name
              AND created_at >= NOW() - INTERVAL '1 minute'
        ),
        last_record AS (
            SELECT health_status,
                   created_at,
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
            ROUND(AVG(memory_used_heap * 100 / NULLIF(memory_max_heap, 0)), 2) AS memory_load,
            ROUND(AVG(process_cpu_usage * 100), 2) AS cpu_usage,
            ROUND(AVG(threads_live), 0) AS threads_live,
            ROUND(AVG(jdbc_connections_active * 100 / NULLIF(jdbc_connections_max, 0)), 2) AS db_load
        FROM last_minute_data
        )
        SELECT
            lr.*,
            ag.*,
            lr.sessions_active_current - fr.sessions_active_current AS consumption_growth
        FROM last_record lr
        CROSS JOIN aggregates ag
        CROSS JOIN first_record fr;
                """.trimIndent()

      val a =   entityManager.createQuery("", BotProperties::class.java).resultList
        val param = mapOf("service_name" to serviceName)

        return jdbcTemplate.query(sql, param) { rs, _ ->
            mapOf(
                "health_status" to rs.getString("health_status"),
                "database_status" to rs.getString("database_status"),
                "availability" to rs.getDouble("availability"),
                "memory_load" to rs.getDouble("memory_load"),
                "cpu_usage" to rs.getDouble("cpu_usage"),
                "threads_live" to rs.getInt("threads_live"),
                "db_load" to rs.getDouble("db_load"),
                "consumption_growth" to rs.getInt("consumption_growth")
            )
        }.first()
    }
}

interface IMetricResponse {

    fun getHealthStatus()
    fun getDatabaseStatus()
    fun getAvailability()
    fun getMemoryLoad()
    fun getCpuUsage()
    fun getThreadsLive()
    fun getDbLoad()
    fun getConsumptionGrowth()
}

