package com.test.monitoringService.model.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Отчет по метрикам")
class ReportMetricsDto(
    @Schema(description = "Имя сервиса")
    val serviceName: String = "UNKNOWN",
    @Schema(description = "Состояние сервиса")
    val healthStatus: String = "UNKNOWN",
    @Schema(description = "Состояние БД сервиса")
    val databaseStatus: String = "UNKNOWN",
    @Schema(description = "Доступность сервиса в %")
    availability: Double = 0.0,
    @Schema(description = "Загрузка памяти в %")
    memoryLoad: Double = 0.0,
    @Schema(description = "Использование CPU в %")
    cpuUsage: Double = 0.0,
    @Schema(description = "Количество потоков")
    threadsLive: Long = 0,
    @Schema(description = "Загрузка БД в %")
    databaseLoad: Double = 0.0,
    @Schema(description = "Рост потребления сервиса")
    val consumptionDifference: Long = 0,
) {
    val availability: Double = availability.coerceIn(0.0, 100.0)
    val memoryLoad: Double = memoryLoad.coerceIn(0.0, 100.0)
    val cpuUsage: Double = cpuUsage.coerceIn(0.0, 100.0)
    val databaseLoad: Double = databaseLoad.coerceIn(0.0, 100.0)
    val threadsLive: Long = threadsLive.coerceAtLeast(0)
}