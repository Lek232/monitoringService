package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.Notifications
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
@Schema(description = "Уведомления о срабатывании триггеров")
class NotificationsDto (
    @Schema(description = "Имя триггера")
    val triggerName: String,
    @Schema(description = "Имя сервиса")
    val serviceName: String,
    @Schema(description = "Метрика")
    val triggerMetricName: String,
    @Schema(description = "Оператор сравнения")
    val triggerOperatorName: String,
    @Schema(description = "Пороговое значение")
    val triggerThreshold: String,
    @Schema(description = "Значение при котором сработал триггер")
    val currentValue: String,
    @Schema(description = "Дата и время (по Гринвичу)")
    val time: OffsetDateTime,
    @Schema(description = "Состояние сервиса")
    val healthStatus: String,
    @Schema(description = "Состояние БД")
    val databaseStatus: String,
    @Schema(description = "Доступность сервиса в %")
    val availability: String,
    @Schema(description = "Использование CPU в %")
    val cpuUsage: String,
    @Schema(description = "Загрузка памяти в %")
    val memory: String,
)

    fun NotificationsDto.toNotificationsEntity(): Notifications =
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
