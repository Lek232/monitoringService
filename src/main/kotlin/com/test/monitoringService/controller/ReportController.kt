package com.test.monitoringService.controller

import com.test.monitoringService.component.filler.FillMetricsReportUsecase
import com.test.monitoringService.component.filler.FillPostgresReportUsecase
import com.test.monitoringService.configuration.ServiceConfig
import com.test.monitoringService.model.dto.ReportMetricsDto
import com.test.monitoringService.model.dto.ReportPostgresDto
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ReportController(
    val fillMetricsReport: FillMetricsReportUsecase,
    val fillPostgresReport: FillPostgresReportUsecase,
    services: List<ServiceConfig.Service>,
) {
    val servicesList = services.map { it.name }

    @GetMapping("/postgres")
    @Tag(name = "Report.Postgres", description = "Отчет по Postgres")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun getPostgresReport(): ResponseEntity<List<ReportPostgresDto>> {
        return try {
            ResponseEntity.status(HttpStatus.OK)
                .body(
                    fillPostgresReport.fillReportDto(servicesList)
                        .filter { it.serviceName.isNotEmpty() }
                )
        }catch (_: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }

    @GetMapping("/metrics")
    @Tag(name = "Report.Metrics", description = "Отчет по метрикам сервисов")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun getMetricsReport(): ResponseEntity<List<ReportMetricsDto>> {
        return try{
            ResponseEntity.status(HttpStatus.OK)
                .body(
                    fillMetricsReport.fillReportDto(servicesList))
        } catch (_: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }
}