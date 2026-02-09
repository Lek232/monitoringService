package com.test.monitoringService.service.controller

import com.test.monitoringService.model.dto.CreateTriggerDto
import com.test.monitoringService.model.dto.NotificationsDto
import com.test.monitoringService.model.dto.TriggerResponseDto
import com.test.monitoringService.model.dto.UpdateTriggerDto
import com.test.monitoringService.model.dto.triggerDtoToEntity
import com.test.monitoringService.model.dto.triggerEntityToResponseDto
import com.test.monitoringService.model.entity.FieldType
import com.test.monitoringService.model.entity.TriggerEntity
import com.test.monitoringService.model.entity.TriggerField
import com.test.monitoringService.repository.interfaces.MetricsEntityRepository
import com.test.monitoringService.repository.interfaces.NotificationsRepository
import com.test.monitoringService.repository.interfaces.TriggerRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TriggerControllerService(
    val triggerRepository: TriggerRepository,
    val notificationsRepository: NotificationsRepository,
    val metricsRepository: MetricsEntityRepository,
) {
    fun switchTrigger(triggerName: String): ResponseEntity<String> {
        try {
            val trigger: TriggerEntity =
                checkTriggerExists(triggerName)
                    ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Триггер с именем $triggerName не найден")
            trigger.enabled = !trigger.enabled
            triggerRepository.save(trigger)
            return if (trigger.enabled)
                ResponseEntity.status(HttpStatus.OK).body("Триггер активирован")
            else
                ResponseEntity.status(HttpStatus.OK).body("Триггер отключен")
        } catch(e: Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка: ${e.message}")
        }
    }

    fun deleteTrigger(triggerName: String): ResponseEntity<String> {
        return try {
            if (triggerRepository.deleteByName(triggerName) == 0) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Триггер $triggerName не обнаружен")
            } else {
                ResponseEntity.status(HttpStatus.OK)
                    .body("Триггер $triggerName был удален")
            }
        } catch(e: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка: ${e.message}")
        }
    }

    fun createTrigger(param: CreateTriggerDto): ResponseEntity<String> {
        try {
            if(checkServiceExist(param.serviceName)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Сервис с именем ${param.serviceName} не существует")
            }

            if(triggerRepository.findByName(param.name) != null){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Триггер с именем ${param.name} уже существует")
            }

            if (param.metric.belongs == TriggerField.FieldBelongsDb.TRUE && checkDatabase(param.serviceName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("У сервиса нет базы данных")
            }

            when {
                param.metric.type != param.operator.type -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Метрика и оператор должны быть одного типа")
                }

                param.metric.type == FieldType.TEXT && param.threshold.isNumber()
                        || param.metric.type == FieldType.NUMERIC && !param.threshold.isNumber() -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Метрика и порог должны быть одного типа")
                }

                param.operator.type == FieldType.TEXT && param.threshold.isNumber()
                        || param.operator.type == FieldType.NUMERIC && !param.threshold.isNumber() -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Оператор и порог должны быть одного типа")
                }
            }

            val trigger = param.triggerDtoToEntity()

            triggerRepository.save(trigger)
            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Триггер создан и активирован успешно")

        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: ${e.message}. Проверьте правильность ввода")
        } catch (_: EmptyResultDataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Сервис с таким именем не существует")
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при создании триггера: ${e.message}")
        }
    }

    fun updateTrigger(triggerName:String, triggerUpdate: UpdateTriggerDto): ResponseEntity<String> {
        try {
            val trigger = checkTriggerExists(triggerName)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Не найден триггер $triggerName")
            run block@{
                when {
                    triggerUpdate.operator == null -> {
                        return@block
                    }

                    triggerUpdate.operator.type != trigger.operator.type -> {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Оператор не может менять тип")
                    }

                    triggerUpdate.threshold == null -> {
                        return@block
                    }

                    triggerUpdate.threshold.isNumber() && !trigger.threshold.isNumber()
                            || !triggerUpdate.threshold.isNumber() && trigger.threshold.isNumber() -> {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Порог не может менять тип")
                    }

                    triggerUpdate.operator.type == FieldType.TEXT && triggerUpdate.threshold.isNumber()
                            ||triggerUpdate.operator.type == FieldType.NUMERIC && !triggerUpdate.threshold.isNumber() -> {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Оператор и порог должны быть одного типа")
                    }
                }
            }
            if (triggerUpdate.cooldown == null && triggerUpdate.threshold == null && triggerUpdate.operator == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Не заданы параметры для изменения")
            }
            triggerUpdate.operator?.let { trigger.operator = triggerUpdate.operator }
            triggerUpdate.threshold?.let { trigger.threshold = triggerUpdate.threshold }
            triggerUpdate.cooldown?.let { trigger.cooldownMinutes = triggerUpdate.cooldown }
            triggerRepository.save(trigger)
            return ResponseEntity.status(HttpStatus.OK)
                .body("Триггер изменен")
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при изменении триггера: ${e.message}")
        }
    }

    fun showAllTriggersForService(serviceName: String): ResponseEntity<List<TriggerResponseDto>>{
        try {
            if (checkServiceExist(serviceName)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(emptyList())
            }
            val triggers = triggerRepository.findActiveTriggers(serviceName)
            return ResponseEntity.status(HttpStatus.OK)
                .body(triggers.map {
                    it.triggerEntityToResponseDto()
                })
        } catch(_: Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }

    fun showAllActiveTriggers():  ResponseEntity<List<TriggerResponseDto>> {
        try {
            val triggers = triggerRepository.findByEnabledTrue()
            return ResponseEntity.status(HttpStatus.OK)
                .body(triggers.map {
                    it.triggerEntityToResponseDto()
                })
        } catch(_: Exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(emptyList())
    }
    }

    fun showAllTriggers():  ResponseEntity<List<TriggerResponseDto>>{
        try {
            val triggers = triggerRepository.findAll()
            return ResponseEntity.status(HttpStatus.OK)
                .body(triggers.map {
                    it.triggerEntityToResponseDto()
                })
        } catch(_: Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }

    fun notify(): ResponseEntity<List<NotificationsDto>> {
        try {
            val notify: List<NotificationsDto> = notificationsRepository.getLastFifty()
            return ResponseEntity.status(HttpStatus.OK)
                .body(notify)
        } catch(_: Exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }

    private fun checkServiceExist(serviceName: String): Boolean{
        return metricsRepository.findFirstByServiceName(serviceName)?.serviceName?.isEmpty() ?: true
    }

    private fun String.isNumber() = this.toDoubleOrNull() != null

    private fun checkDatabase(serviceName: String): Boolean =
        metricsRepository.findFirstByServiceName(serviceName)?.databaseStatus == "Null"

    private fun checkTriggerExists(triggerName: String): TriggerEntity?{
        val trigger: TriggerEntity =
            triggerRepository.findByName(triggerName)
                ?: return null
        return trigger
    }
}