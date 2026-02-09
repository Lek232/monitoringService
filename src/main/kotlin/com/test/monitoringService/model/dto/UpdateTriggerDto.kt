package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.TriggerOperator
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "Запрос на частичное обновление триггера")
data class UpdateTriggerDto(
    @Schema(
        description = "Оператор сравнения (только если нужно изменить)",
        example = "GT",
        nullable = true,
        allowableValues = [
            "EQ - Равно (числовой)",
            "NE - Не равно (числовой)",
            "GT - Больше (числовой)",
            "LT - Меньше (числовой)",
            "GTE - Больше или равно (числовой)",
            "LTE - Меньше или равно (числовой)",
            "CONTAINS - Содержит (текстовый)",
            "NOT_CONTAINS - Не содержит (текстовый)",
        ],
    )
    val operator: TriggerOperator? = null,
    @Schema(
        description = "Пороговое значение (только если нужно изменить)",
        example = "65",
        nullable = true,
    )
    val threshold: String? = null,
    @Schema(
        description = "Время в минутах (только если нужно изменить)",
        example = "10",
        nullable = true,
        minimum = "1"
    )
    val cooldown: Int? = null
)
