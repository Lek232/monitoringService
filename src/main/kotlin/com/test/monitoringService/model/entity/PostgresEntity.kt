package com.test.monitoringService.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
@Table(
    name = "postgres_metrics", indexes = [
        Index(name = "idx_postgres_id_service", columnList = "id DESC, service_name"),
    ]
)
class PostgresEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT (NOW() AT TIME ZONE 'UTC')")
    val created: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),

    @Column(name = "service_name", columnDefinition = "VARCHAR(100)", nullable = false)
    var serviceName: String = "UNKNOWN",

    @Column(name = "total_queries", columnDefinition = "NUMERIC(15, 0)", nullable = false)
    var totalQueries: Long = 0,

    @Column(name = "total_calls", columnDefinition = "NUMERIC(15, 0)", nullable = false)
    var totalCalls: Long = 0,

    @Column(name = "max_total_time_ms", columnDefinition = "NUMERIC(9, 2)", nullable = false)
    var maxTotalTime: Double = 0.0,

    @Column(name = "avg_exec_time_ms", columnDefinition = "NUMERIC(8, 2)", nullable = false)
    var avgExecTime: Double = 0.0,

    @Column(name = "max_stddev_exec_time_ms", columnDefinition = "NUMERIC(7, 2)", nullable = false)
    var maxStddevExecTime: Double = 0.0,

    @Column(name = "avg_cache_hit", columnDefinition = "NUMERIC(5, 2)", nullable = false)
    var avgCacheHit: Double = 0.0,
)