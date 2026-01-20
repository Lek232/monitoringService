package com.test.monitoringService.service.report

import com.test.monitoringService.component.filler.FillMetricsReportDto
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class MetricsReportService(
    val fillMetricsReport: FillMetricsReportDto,
) {
    fun generateHtmlMetrics(services: List<String>): String {
        val reportDataList = fillMetricsReport.fillReportDto(services)
        val time = "<b>Состояние  сервисов на: ${OffsetDateTime.now(ZoneOffset.UTC).withNano(0)}</b>"
        val header = String.format(
            "%-15s %-13s %-13s %-13s %-8s %-8s %-8s %-8s %-11s",
            "Сервис", "|Состояние", "|БД", "|Доступность", "|Heap", "|CPU", "|Потоки", "|Нагр.БД", "|Потребление"
        )
        val separator = "=".repeat(100)
        val rows = reportDataList.joinToString("\n") {
            String.format(
                "%-15s %-13s %-13s %-13s %-8s %-8s %-8s %-8s %-11s",
                it.serviceName,
                "|${it.healthStatus}",
                if (it.databaseStatus != "Null") {
                    "|${it.databaseStatus}"
                } else { "|-" },
                if (it.availability > 100) "|100.0%" else "|${it.availability}%",
                "|${it.memoryLoad}%",
                "|${it.cpuUsage}%",
                "|${it.threadsLive}",
                if (it.databaseStatus != "Null") {
                    "|${it.databaseLoad}%"
                } else { "|-" },
                "|${it.consumptionDifference}"
            )
        }
        return "<pre>$time\n$separator\n$header\n$separator\n$rows</pre>"
    }
}
