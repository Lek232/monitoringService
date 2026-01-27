package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.Notifications
import java.time.OffsetDateTime

class NotificationsDto (
    var triggerName: String,
    var serviceName: String,
    var triggerMetricName: String,
    var triggerOperatorName: String,
    var triggerThreshold: String,
    var currentValue: String,
    var time: OffsetDateTime,
    var healthStatus: String,
    var databaseStatus: String,
    var availability: String,
    var cpuUsage: String,
    var memory: String,
)
{
    fun toNotificationsEntity(): Notifications =
        Notifications(
            triggerName = this.triggerName,
            serviceName = this.serviceName,
            triggerMetricName = this.triggerMetricName,
            triggerOperatorName = this.triggerOperatorName,
            triggerThreshold = this.triggerThreshold,
            currentValue = this.currentValue,
            time = this.time,
            healthStatus = this.healthStatus,
            databaseStatus = this.databaseStatus,
            availability = this.availability,
            cpuUsage = this.cpuUsage,
            memory = this.memory,
        )
}