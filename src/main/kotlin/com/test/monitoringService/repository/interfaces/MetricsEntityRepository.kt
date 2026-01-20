package com.test.monitoringService.repository.interfaces


import com.test.monitoringService.model.entity.MetricsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MetricsEntityRepository : JpaRepository<MetricsEntity, Long> {

    fun findFirstByServiceName(serviceName: String): MetricsEntity
}