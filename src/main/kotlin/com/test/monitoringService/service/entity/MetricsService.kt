package com.test.monitoringService.service.entity

import com.test.monitoringService.model.entity.MetricsEntity
import com.test.monitoringService.repository.interfaces.MetricsEntityRepository
import org.springframework.stereotype.Service

@Service
class MetricsService(
    val dataRepository: MetricsEntityRepository
) {
    fun saveMetrics(data: MetricsEntity): MetricsEntity = dataRepository.save(data)
}