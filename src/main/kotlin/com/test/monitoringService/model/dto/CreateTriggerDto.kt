package com.test.monitoringService.model.dto

import com.test.monitoringService.model.entity.TriggerEntity
import com.test.monitoringService.model.entity.TriggerField
import com.test.monitoringService.model.entity.TriggerOperator
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "Запрос на создание триггера")
data class CreateTriggerDto(
    @Schema(
        description = "Имя триггера (должно быть уникальным)",
        example = "triggerName"
    )
    val name: String = "",
    @Schema(
        description = "Имя сервиса",
        example = "serviceName"
    )
    val serviceName: String = "all",
    @Schema(
        description = "Метрика, которую триггер будет проверять",
        example = "HEALTH_STATUS",
        allowableValues = [
            "HEALTH_STATUS - Состояние сервиса",
            "AVAILABILITY - Доступность сервиса",
            "MEMORY_LOAD - Загрузка памяти сервиса",
            "CPU_USAGE - Использование СPU",
            "THREADS_LIVE - Потоки",
            "CONSUMPTION_DIFFERENCE - Рост потребления",
            "DATABASE_STATUS - Состояние БД (сервис должен иметь БД)",
            "DATABASE_LOAD - Нагрузка на БД (сервис должен иметь БД)",
            "TOTAL_QUERIES - Всего запросов SQL (сервис должен иметь БД)",
            "TOTAL_CALLS - Всего вызовов запросов SQL (сервис должен иметь БД)",
            "MAX_TOTAL_TIME_MS - Максимальное время выполнения запроса (сервис должен иметь БД)",
            "AVG_EXEC_TIME_MS - Среднее время выполнения запроса (сервис должен иметь БД)",
            "MAX_STDDEV_EXEC_TIME_MS - Максимальное отклонение от среднего времени выполнения запроса (сервис должен иметь БД)",
            "AVG_CACHE_HIT - Среднее попадание в кэш (сервис должен иметь БД)"
        ]
    )
    val metric: TriggerField = TriggerField.HEALTH_STATUS,
    @Schema(
        description = "Оператор сравнения метрики с пороговым значением",
        example = "CONTAINS",
        allowableValues = [
            "EQ - Равно (числовой)",
            "NE - Не равно (числовой)",
            "GT - Больше (числовой)",
            "LT - Меньше (числовой)",
            "GTE - Больше или равно (числовой)",
            "LTE - Меньше или равно (числовой)",
            "CONTAINS - Содержит (текстовый)",
            "NOT_CONTAINS - Не содержит (текстовый)",
        ]
    )
    val operator: TriggerOperator = TriggerOperator.CONTAINS,
    @Schema(
        description = "Пороговое значение для метрики",
        example = "UNKNOWN",
    )
    val threshold: String = "",
    @Schema(
        description = "Частота срабатывания триггера (в минутах)",
        example = "5",
        minimum = "1",
    )
    val cooldown: Int = 5
) : Serializable

fun CreateTriggerDto.triggerDtoToEntity() =
    TriggerEntity(
        name = this.name,
        serviceName = this.serviceName,
        metric = this.metric,
        operator = this.operator,
        threshold = this.threshold,
        cooldownMinutes = this.cooldown,
        )
