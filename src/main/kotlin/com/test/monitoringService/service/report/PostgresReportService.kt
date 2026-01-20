package com.test.monitoringService.service.report

import com.test.monitoringService.component.filler.FillPostgresReportDto
import org.springframework.stereotype.Service

@Service
class PostgresReportService(
    val fillPostgresReport: FillPostgresReportDto,
) {
    fun generateHtmlPostgres(services: List<String>): String {
        val reportDataList = fillPostgresReport.fillReportDto(services).filter { it.serviceName.isNotEmpty() }
        val header1 = String.format(
            "%-15s %-10s %-10s %-13s %-10s %-16s %-14s",
            "Сервис", "|Всего", "|Всего", "|Самый долгий", "|Ср.время", "|Ср.стабильность", "|Ср.кэширование"
        )
        val header2 = String.format(
            "%-15s %-10s %-10s %-13s %-10s %-16s %-14s",
            "", "|запросов", "|вызовов", "|запрос", "|запроса", "|запросов", "|БД"
        )
        val separator = "=".repeat(100)
        val rows = reportDataList.joinToString("\n") {
            String.format(
                "%-15s %-10s %-10s %-13s %-10s %-16s %-14s",
                it.serviceName,
                "|${it.totalQueries}",
                "|${it.totalCalls}",
                "|${it.maxTotalTimeMs} ms",
                "|${it.avgExecTimeMs} ms",
                "|${it.maxStddevExecTimeMs} ms",
                "|${it.avgCacheHit}%",
            )
        }
        return "<pre>$separator\n$header1\n$header2\n$separator\n$rows</pre>"
    }
}