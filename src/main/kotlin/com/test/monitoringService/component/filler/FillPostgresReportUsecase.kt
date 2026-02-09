package com.test.monitoringService.component.filler

import com.test.monitoringService.dao.PostgresDao
import com.test.monitoringService.model.dto.ReportPostgresDto
import org.springframework.stereotype.Component

@Component
class FillPostgresReportUsecase(
    val postgresDao: PostgresDao,
) {
    fun fillReportDto(services: List<String>): List<ReportPostgresDto> =
        services.map {
            postgresDao.getPostgres(it)
        }
}
