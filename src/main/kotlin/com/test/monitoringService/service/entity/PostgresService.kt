package com.test.monitoringService.service.entity

import com.test.monitoringService.model.entity.PostgresEntity
import com.test.monitoringService.repository.interfaces.PostgresEntityRepository
import org.springframework.stereotype.Service

@Service
class PostgresService(
    val dataRepository: PostgresEntityRepository
) {
    fun savePostgres(dataPostgres: PostgresEntity): PostgresEntity = dataRepository.save(dataPostgres)
}