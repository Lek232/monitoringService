package com.test.monitoringService.service

import com.test.monitoringService.component.telegramBot.TelegramBot
import com.test.monitoringService.model.dto.NotificationsDto
import com.test.monitoringService.model.dto.TriggerDto
import com.test.monitoringService.model.entity.FieldType
import com.test.monitoringService.model.entity.TriggerEntity
import com.test.monitoringService.model.entity.TriggerField
import com.test.monitoringService.model.entity.TriggerOperator
import com.test.monitoringService.repository.interfaces.NotificationsRepository
import com.test.monitoringService.repository.interfaces.TriggerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class TriggerCheckService(
    val triggerRepository: TriggerRepository,
    val notificationsRepository: NotificationsRepository,

    @Autowired(required = false)
    val telegramBot: TelegramBot? = null,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun checkTriggers(dto: TriggerDto) {
        val triggers = triggerRepository.findActiveTriggers(dto.serviceName)
        if (triggers.isEmpty()) {
            log.info("Нет триггеров для сервиса: ${dto.serviceName}")
            return
        }
        triggers.map { trigger ->
            try {
                if (shouldTrigger(trigger, dto)) {
                    handleTrigger(trigger, dto)
                }
            } catch (e: Exception) {
                log.error("Ошибка проверки триггера: ${trigger.name}: ${e.message}")
            }
        }
    }

    private fun shouldTrigger(trigger: TriggerEntity, dto: TriggerDto): Boolean {
        trigger.lastTriggered?.let { lastTriggered ->
            val cooldownDuration = Duration.ofMinutes(trigger.cooldownMinutes.toLong())
            val timeSinceLastTrigger = Duration.between(lastTriggered, OffsetDateTime.now())
            if (timeSinceLastTrigger < cooldownDuration) {
                return false
            }
        }
        return checkCondition(trigger, dto)
    }

    private fun checkCondition(trigger: TriggerEntity, dto: TriggerDto): Boolean {
        val value = getFieldValue(trigger.metric, dto)

        return when (trigger.operator) {
            TriggerOperator.EQ -> value == trigger.threshold
            TriggerOperator.NE -> value != trigger.threshold
            TriggerOperator.GT -> ((value as? Number)?.toDouble() ?: 0.0) > trigger.threshold.toDouble()
            TriggerOperator.LT -> ((value as? Number)?.toDouble() ?: 0.0) < trigger.threshold.toDouble()
            TriggerOperator.GTE -> ((value as? Number)?.toDouble() ?: 0.0) >= trigger.threshold.toDouble()
            TriggerOperator.LTE -> ((value as? Number)?.toDouble() ?: 0.0) <= trigger.threshold.toDouble()
            TriggerOperator.CONTAINS -> value.toString().contains(trigger.threshold)
            TriggerOperator.NOT_CONTAINS -> !value.toString().contains(trigger.threshold)
        }
    }

    private fun getFieldValue(field: TriggerField, dto: TriggerDto): Any {
        return when (field) {
            TriggerField.HEALTH_STATUS -> dto.healthStatus
            TriggerField.DATABASE_STATUS -> dto.databaseStatus
            TriggerField.AVAILABILITY -> dto.availability
            TriggerField.MEMORY_LOAD -> dto.memoryLoad
            TriggerField.CPU_USAGE -> dto.cpuUsage
            TriggerField.THREADS_LIVE -> dto.threadsLive
            TriggerField.DATABASE_LOAD -> dto.databaseLoad
            TriggerField.CONSUMPTION_DIFFERENCE -> dto.consumptionDifference
            TriggerField.TOTAL_QUERIES -> dto.totalQueries
            TriggerField.TOTAL_CALLS -> dto.totalCalls
            TriggerField.MAX_TOTAL_TIME_MS -> dto.maxTotalTimeMs
            TriggerField.AVG_EXEC_TIME_MS -> dto.avgExecTimeMs
            TriggerField.MAX_STDDEV_EXEC_TIME_MS -> dto.maxStddevExecTimeMs
            TriggerField.AVG_CACHE_HIT -> dto.avgCacheHit
        }
    }

    private fun handleTrigger(trigger: TriggerEntity, dto: TriggerDto) {
        log.info("Триггер сработал: ${trigger.name} для сервиса ${dto.serviceName}")

        trigger.lastTriggered = OffsetDateTime.now(ZoneOffset.UTC)
        triggerRepository.save(trigger)

        val valueStr = when (val value = getFieldValue(trigger.metric, dto)) {
            FieldType.NUMERIC -> String.format("%.2f", value)
            else -> value.toString()
        }

        val message = createAlertMessage(valueStr, trigger, dto)

        notificationsRepository.save(writeNotifications(valueStr, trigger, dto).toNotificationsEntity())

        telegramBot?.sendAlert(message)
    }

    private fun createAlertMessage(valueStr: String, trigger: TriggerEntity, dto: TriggerDto): String {
        return """
            Оповещение: ${trigger.name}
            Сервис: ${dto.serviceName}
            Условие: ${trigger.metric.name} ${trigger.operator.name} ${trigger.threshold}
            Текущее значение: $valueStr
            Time: ${OffsetDateTime.now(ZoneOffset.UTC).withNano(0)}
            
            Статус сервиса:
            - Health: ${dto.healthStatus}
            - Database: ${dto.databaseStatus}
            - Availability: ${String.format("%.2f", if (dto.availability > 100) 100.0 else dto.availability)}%
            - CPU: ${String.format("%.2f", dto.cpuUsage)}%
            - Memory: ${String.format("%.2f", dto.memoryLoad)}%
        """.trimIndent()
    }

    fun writeNotifications (valueStr: String, trigger: TriggerEntity, dto: TriggerDto): NotificationsDto {
        return NotificationsDto(
            triggerName = trigger.name,
            serviceName = dto.serviceName,
            triggerMetricName = trigger.metric.name,
            triggerOperatorName = trigger.operator.name,
            triggerThreshold = trigger.threshold,
            currentValue = valueStr,
            time = OffsetDateTime.now(ZoneOffset.UTC).withNano(0),
            healthStatus = dto.healthStatus,
            databaseStatus = dto.databaseStatus,
            availability = String.format("%.2f", if (dto.availability > 100) 100.0 else dto.availability),
            cpuUsage = String.format("%.2f", dto.cpuUsage),
            memory = String.format("%.2f", dto.memoryLoad),
        )
    }
}
