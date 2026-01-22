package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.MetricsDao
import com.test.monitoringService.model.dto.ReportMetricsDto
import org.springframework.stereotype.Component

// TODO нейминг, у нас DTO не может быть компонентом, переименуй в сервис наверное или в Usecase, будет корректнее
@Component
class FillMetricsReportDto(
    val metricsDao: MetricsDao
) {
    fun fillReportDto(services: List<String>): List<ReportMetricsDto> =
        services.map {
            val metrics = metricsDao.getMetrics(it)
            if (metrics["health_status"].toString() == "UNKNOWN") {
                ReportMetricsDto(serviceName = it)
            } else {
                ReportMetricsDto(
                    serviceName = it,
                    healthStatus = metrics["health_status"].toString(),
                    databaseStatus = metrics["database_status"].toString(),
                    availability = metrics["availability"] as Double,
                    memoryLoad = metrics["memory_load"] as Double,
                    cpuUsage = metrics["cpu_usage"] as Double,
                    threadsLive = metrics["threads_live"] as Int,
                    databaseLoad = metrics["db_load"] as Double,
                    consumptionDifference = metrics["consumption_growth"] as Int,
                )
            }
        }
}
