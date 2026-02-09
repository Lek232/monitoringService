package com.test.monitoringService.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
@Table(
    name = "metrics_table", indexes = [
        Index(name = "idx_metrics_time_service", columnList = "service_name, created_at DESC"),
    ]
)
class MetricsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT (NOW() AT TIME ZONE 'UTC')")
    val created: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),

    @Column(name = "service_name", columnDefinition = "VARCHAR(100)", nullable = false)
    var serviceName: String = "UNKNOWN",

    @Column(name = "health_status", columnDefinition = "VARCHAR(50)", nullable = false)
    var healthStatus: String = "UNKNOWN",

    @Column(name = "database_name", columnDefinition = "VARCHAR(100)", nullable = false)
    var databaseName: String = "UNKNOWN",

    @Column(name = "database_status", columnDefinition = "VARCHAR(50)", nullable = false)
    var databaseStatus: String = "UNKNOWN",

    @Column(name = "server_requests_count", columnDefinition = "NUMERIC(15, 0)", nullable = false)
    var serverRequestsCount: Long = 0,

    @Column(name = "server_requests_success_count", columnDefinition = "NUMERIC(15, 0)", nullable = false)
    var serverRequestsSuccessCount: Long = 0,

    @Column(name = "sessions_active_current", columnDefinition = "NUMERIC(6, 0)", nullable = false)
    var sessionsActiveCurrent: Long = 0,

    @Column(name = "memory_used_heap", columnDefinition = "NUMERIC(15, 0)", nullable = false)
    var memoryUsedHeap: Long = 0,

    @Column(name = "memory_max_heap", columnDefinition = "NUMERIC(15, 0)", nullable = false)
    var memoryMaxHeap: Long = 0,

    @Column(name = "process_cpu_usage", columnDefinition = "NUMERIC(5, 4)", nullable = false)
    var processCpuUsage: Double = 0.0,

    @Column(name = "threads_live", columnDefinition = "NUMERIC(10, 0)", nullable = false)
    var threadsLive: Long = 0,

    @Column(name = "jdbc_connections_active", columnDefinition = "NUMERIC(3, 0)", nullable = false)
    var jdbcConnectionsActive: Long = 0,

    @Column(name = "jdbc_connections_max", columnDefinition = "NUMERIC(3, 0)", nullable = false)
    var jdbcConnectionsMax: Long = 0,

    @Column(name = "error_collect", columnDefinition = "TEXT", nullable = false)
    var errorCollect: String = "-"
)
