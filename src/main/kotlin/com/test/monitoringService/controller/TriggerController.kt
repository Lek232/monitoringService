package com.test.monitoringService.controller


import com.test.monitoringService.model.dto.CreateTriggerDto
import com.test.monitoringService.model.dto.TriggerResponseDto
import com.test.monitoringService.model.dto.UpdateTriggerDto
import com.test.monitoringService.service.controller.TriggerControllerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/triggers")
@Tag(name = "Triggers", description = "Управление триггерами")
class TriggerController(
    val triggerControllerService : TriggerControllerService,
) {

    @GetMapping
    @Operation(summary = "Получить все триггеры")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun getAllTriggers(): ResponseEntity<List<TriggerResponseDto>>{
       return triggerControllerService.showAllTriggers()
    }

    @GetMapping("/active")
    @Operation(summary = "Получить все активные триггеры")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            ]
    )
    fun getAllActiveTriggers(): ResponseEntity<List<TriggerResponseDto>>{
        return triggerControllerService.showAllActiveTriggers()
    }

    @GetMapping("/service/{serviceName}")
    @Operation(
        summary = "Получить все триггеры для сервиса",
        description = "Введите имя сервиса"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "404", description = "Не найден"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun getAllTriggersForService(
        @Parameter(
            description = "Имя сервиса",
            example = "serviceName",
            required = true,
        )
        @PathVariable serviceName: String): ResponseEntity<List<TriggerResponseDto>> {
        return triggerControllerService.showAllTriggersForService(serviceName)
    }

    @PatchMapping("/switch/{triggerName}")
    @Operation(
        summary = "Включение или отключение триггера",
        description = "Введите имя триггера"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "404", description = "Не найден"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun switchTrigger(
        @Parameter(
            description = "Имя триггера",
            example = "triggerName",
            required = true,
        )
        @PathVariable triggerName: String): ResponseEntity<String> {
        return triggerControllerService.switchTrigger(triggerName)
    }

    @DeleteMapping("/{triggerName}")
    @Operation(
        summary = "Удаление триггера",
        description = "Введите имя триггера подлежащего удалению"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Удаление успешно"),
            ApiResponse(responseCode = "404", description = "Не найден"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun deleteTrigger(
        @Parameter(
            description = "Имя сервиса",
            example = "serviceName",
            required = true,
        )
        @PathVariable triggerName: String): ResponseEntity<String> {
        return triggerControllerService.deleteTrigger(triggerName)
    }

    @PostMapping
    @Operation(
        summary = "Создание триггера",
        description = "Введите параметры создаваемого триггера"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Успешно"),
            ApiResponse(responseCode = "400", description = "Неверный запрос"),
            ApiResponse(responseCode = "404", description = "Не найден"),
            ApiResponse(responseCode = "409", description = "Триггер уже существует"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun createTrigger(@RequestBody createParam: CreateTriggerDto) {
        triggerControllerService.createTrigger(createParam)
    }

    @PatchMapping("/{triggerName}")
    @Operation(
        summary = "Изменение триггера",
        description = "Введите параметры для изменения триггера. Можно вводить не все параметры",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешно"),
            ApiResponse(responseCode = "400", description = "Неверный запрос"),
            ApiResponse(responseCode = "404", description = "Не найден"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
        ]
    )
    fun updateTrigger(@PathVariable triggerName: String,
                      @RequestBody triggerBody: UpdateTriggerDto): ResponseEntity<String> {
        return triggerControllerService.updateTrigger(triggerName, triggerBody)
    }
}
