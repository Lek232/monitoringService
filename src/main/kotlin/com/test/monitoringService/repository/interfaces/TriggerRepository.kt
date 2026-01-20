package com.test.monitoringService.repository.interfaces

import com.test.monitoringService.model.entity.TriggerEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TriggerRepository : JpaRepository<TriggerEntity, Long> {

    @Query("SELECT t FROM TriggerEntity t WHERE t.enabled = true AND (t.serviceName IS NULL OR t.serviceName = :serviceName)")
    fun findActiveTriggers(@Param("serviceName") serviceName: String): List<TriggerEntity>

    fun findByEnabledTrue(): List<TriggerEntity>

    @Query("SELECT t FROM TriggerEntity t WHERE t.name = :name")
    fun findByName(@Param("name") name: String): TriggerEntity?

    @Modifying
    @Transactional
    @Query("DELETE FROM TriggerEntity t WHERE t.name = :name")
    fun deleteByName(@Param("name") name: String): Int
}