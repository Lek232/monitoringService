package com.test.monitoringService.controller

import com.test.monitoringService.model.dto.NotificationsDto
import com.test.monitoringService.service.controller.TriggerControllerService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Notifications", description = "Уведомления о срабатывании триггеров")
class NotificationController(
    val triggerControllerService : TriggerControllerService,
) {

    @GetMapping("/notifications")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun getNotifications(): ResponseEntity<List<NotificationsDto>>{
        return triggerControllerService.notify()
    }
}