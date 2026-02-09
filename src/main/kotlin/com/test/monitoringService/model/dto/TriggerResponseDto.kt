package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.TriggerEntity
import com.test.monitoringService.model.entity.TriggerField
import com.test.monitoringService.model.entity.TriggerOperator
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Запрос на просмотр триггеров")
data class TriggerResponseDto(
    @Schema(description = "id")
    val id: Long = 0,
    @Schema(description = "Имя триггера")
    val name: String = "",
    @Schema(description = "Имя сервиса")
    val serviceName: String = "all",
    @Schema(description = "Метрика, которую триггер будет проверять")
    val metric: TriggerField = TriggerField.HEALTH_STATUS,
    @Schema(description = "Оператор сравнения метрики с пороговым значением")
    val operator: TriggerOperator = TriggerOperator.CONTAINS,
    @Schema(description = "Пороговое значение для метрики")
    val threshold: String = "",
    @Schema(description = "Состояние триггера(включен или выключен)")
    val enabled: Boolean = true,
    @Schema(description = "Частота срабатывания триггера (в минутах)")
    val cooldownMinutes: Int = 5
)

fun TriggerEntity.triggerEntityToResponseDto() =
    TriggerResponseDto(
        id = this.id,
        name = this.name,
        serviceName = this.serviceName,
        metric = this.metric,
        operator = this.operator,
        threshold = this.threshold,
        enabled = this.enabled,
        cooldownMinutes = this.cooldownMinutes,
    )