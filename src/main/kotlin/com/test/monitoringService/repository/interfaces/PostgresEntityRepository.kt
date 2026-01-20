package com.test.monitoringService.repository.interfaces

import com.test.monitoringService.model.entity.PostgresEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostgresEntityRepository : JpaRepository<PostgresEntity, Long>