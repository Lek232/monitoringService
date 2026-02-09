package com.test.monitoringService.repository.interfaces

import com.test.monitoringService.model.dto.NotificationsDto
import com.test.monitoringService.model.entity.Notifications
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NotificationsRepository : JpaRepository<Notifications, Long>{

    @Query("SELECT N FROM Notifications N ORDER BY N.id DESC LIMIT 50")
    fun getLastFifty(): List<NotificationsDto>
}