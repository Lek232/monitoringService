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
                metrics
            }
        }
}
