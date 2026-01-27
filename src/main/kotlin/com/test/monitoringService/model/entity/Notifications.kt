package com.test.monitoringService.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "notifications")
class Notifications(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Long = 0,

    @Column(name = "trigger_name", nullable = false, length = 100)
    open var triggerName: String = "",

    @Column(name = "service_name", nullable = false, length = 100)
    open var serviceName: String = "",

    @Column(name = "trigger_metric_name", nullable = false, length = 100)
    open var triggerMetricName: String = "",

    @Column(name = "trigger_operator_name", nullable = false, length = 100)
    open var triggerOperatorName: String = "",

    @Column(name = "trigger_threshold", nullable = false, length = 100)
    open var triggerThreshold: String = "",

    @Column(name = "current_value", length = 100)
    open var currentValue: String = "",

    @Column(name = "time", nullable = false)
    open var time: OffsetDateTime,

    @Column(name = "health_status", nullable = false, length = 100)
    open var healthStatus: String = "",

    @Column(name = "database_status", length = 100)
    open var databaseStatus: String = "",

    @Column(name = "availability", nullable = false, length = 100)
    open var availability: String = "",

    @Column(name = "cpu_usage", nullable = false, length = 100)
    open var cpuUsage: String = "",

    @Column(name = "memory", nullable = false, length = 100)
    open var memory: String = "",
)