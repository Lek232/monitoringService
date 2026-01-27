package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.MetricsDao
import com.test.monitoringService.model.dto.ReportMetricsDto
import org.springframework.stereotype.Component

@Component
class FillMetricsReportUsecase(
    val metricsDao: MetricsDao
) {
    fun fillReportDto(services: List<String>): List<ReportMetricsDto> =
        services.map {
            val metrics = metricsDao.getMetrics(it)
            if (metrics.healthStatus == "UNKNOWN") {
                ReportMetricsDto(serviceName = it)
            } else {
                ReportMetricsDto(
                    serviceName = it,
                    healthStatus = metrics.healthStatus,
                    databaseStatus = metrics.databaseStatus,
                    availability = metrics.availability,
                    memoryLoad = metrics.memoryLoad,
                    cpuUsage = metrics.cpuUsage,
                    threadsLive = metrics.threadsLive,
                    databaseLoad = metrics.databaseLoad,
                    consumptionDifference = metrics.consumptionDifference,
                )
            }
        }
}
