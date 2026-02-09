package com.test.monitoringService.model.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Отчет по Postgres")
class ReportPostgresDto(
    @Schema(description = "Имя сервиса")
    val serviceName: String = "",
    @Schema(description = "Общее число запросов SQL")
    val totalQueries: Long = 0,
    @Schema(description = "Суммарное число вызовов SQL запросов")
    val totalCalls: Long = 0,
    @Schema(description = "Время выполнения самого долгого запроса в мс")
    val maxTotalTimeMs: Double = 0.0,
    @Schema(description = "Среднее время выполнения запросов в мс")
    val avgExecTimeMs: Double = 0.0,
    @Schema(description = "Максимальное отклонение от среднего времени выполнения запроса в мс")
    val maxStddevExecTimeMs: Double = 0.0,
    @Schema(description = "Среднее попадание в кэш в %")
    val avgCacheHit: Double = 0.0,
)

