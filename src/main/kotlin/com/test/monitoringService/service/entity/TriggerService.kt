package com.test.monitoringService.service.entity

import com.test.monitoringService.model.entity.FieldType
import com.test.monitoringService.model.entity.TriggerEntity
import com.test.monitoringService.model.entity.TriggerField
import com.test.monitoringService.model.entity.TriggerOperator
import com.test.monitoringService.repository.interfaces.MetricsEntityRepository
import com.test.monitoringService.repository.interfaces.TriggerRepository
import com.test.monitoringService.service.TelegramSenderService
import com.test.monitoringService.service.interfaces.TriggerInterface
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class TriggerService(
    val triggerRepository: TriggerRepository,
    val messageService: TelegramSenderService,
    val metricsRepository: MetricsEntityRepository
) : TriggerInterface {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun disableTrigger(chatId: String, triggerName: String) {
        val trigger: TriggerEntity? = triggerRepository.findByName(triggerName)
        if (trigger == null) {
            messageService.sendMessage(chatId, "Триггер с именем $triggerName не найден")
            return
        }
        trigger.enabled = false
        triggerRepository.save(trigger)
        messageService.sendMessage(chatId, "Триггер с именем $triggerName отключен")
    }

    override fun enableTrigger(chatId: String, triggerName: String) {
        val trigger: TriggerEntity? = triggerRepository.findByName(triggerName)
        if (trigger == null) {
            messageService.sendMessage(chatId, "Триггер с именем $triggerName не найден")
            return
        }
        trigger.enabled = true
        triggerRepository.save(trigger)
        messageService.sendMessage(chatId, "Триггер с именем $triggerName активирован")
    }

    @Transactional
    override fun deleteTrigger(chatId: String, triggerName: String) {
        if (triggerRepository.deleteByName(triggerName) == 0)
            messageService.sendMessage(chatId, "Триггер $triggerName не был обнаружен")
        else
            messageService.sendMessage(chatId, "Триггер $triggerName был удален")
    }

    @Transactional
    override fun createTrigger(chatId: String, createParam: String) {
        try {
            val param = createParam.split(";")
            if (param.size != 6) {
                messageService.sendMessage(chatId, "Заданы не все необходимые параметры. Пример:\n" +
                        "/create_triggerName;serviceName;CPU_USAGE;GT;80;5")
                return
            }
            val name = param[0]
            val serviceName = param[1].ifBlank { null }
            val metric = TriggerField.valueOf(param[2])
            val operator = TriggerOperator.valueOf(param[3])
            val threshold = param[4]
            val cooldown = param[5].toInt()

            if(serviceName != null && metricsRepository.findFirstByServiceName(serviceName).serviceName.isEmpty()){
                messageService.sendMessage(chatId, "Сервис с именем $serviceName не существует")
                return
            }

            if(triggerRepository.findByName(name) != null){
                messageService.sendMessage(chatId, "Триггер с именем $name уже существует")
                return
            }

            if (serviceName != null && metric.belongs == TriggerField.FieldBelongsDb.TRUE && checkDatabase(serviceName)) {
                messageService.sendMessage(chatId, "У сервиса нет базы данных")
                return
            }

            when {
                metric.type != operator.type -> {
                    messageService.sendMessage(chatId, "Метрика и оператор должны быть одного типа")
                    return
                }

                metric.type == FieldType.TEXT && threshold.isNumber() || metric.type == FieldType.NUMERIC && !threshold.isNumber() -> {
                    messageService.sendMessage(chatId, "Метрика и порог должны быть одного типа")
                    return
                }

                operator.type == FieldType.TEXT && threshold.isNumber() || operator.type == FieldType.NUMERIC && !threshold.isNumber() -> {
                    messageService.sendMessage(chatId, "Оператор и порог должны быть одного типа")
                    return
                }
            }

            val trigger = TriggerEntity(
                name = name,
                serviceName = serviceName,
                metric = metric,
                operator = operator,
                threshold = threshold,
                cooldownMinutes = cooldown
            )

            val saved = triggerRepository.save(trigger)
            messageService.sendMessage(
                chatId, """
                    *Триггер создан и активирован успешно*
                    ID: ${saved.id}
                    Название: ${saved.name}
                    Сервис: ${saved.serviceName ?: "Все"}
                    Условие: ${saved.metric} ${saved.operator} ${saved.threshold}
                    Cooldown: ${saved.cooldownMinutes} мин
                    Используйте /disable_${saved.name} чтобы выключить
                    """.trimIndent()
            )
        } catch (e: IllegalArgumentException) {
            messageService.sendMessage(chatId, "Ошибка: ${e.message}. Проверьте правильность ввода")
        } catch (_: EmptyResultDataAccessException) {
            messageService.sendMessage(chatId, "Сервиса с таким именем не существует")
        } catch (e: Exception) {
            log.warn( "Ошибка при создании триггера: ${e.message}")
        }
    }

    override fun editTrigger(chatId: String, triggerEdit: String) {
        try {
            val triggerName = triggerEdit.substringAfter("name:").substringBefore(";")
            val operator = triggerEdit.extractValue("operator:")?.let { TriggerOperator.valueOf(it) }
            val threshold = triggerEdit.extractValue("threshold:")
            val cooldown = triggerEdit.extractValue("cooldown:")

            val trigger = triggerRepository.findByName(triggerName)

            if (trigger == null) {
                messageService.sendMessage(chatId, "Триггер с именем $triggerName не найден")
                return
            }

            run block@{
                when {
                    operator == null -> {
                        return@block
                    }

                    operator.type != trigger.operator.type -> {
                        messageService.sendMessage(chatId, "Оператор не может менять тип")
                        return
                    }

                    threshold == null -> {
                        return@block
                    }

                    threshold.isNumber() && !trigger.threshold.isNumber() -> {
                        messageService.sendMessage(chatId, "Порог не может менять тип")
                        return
                    }

                    !threshold.isNumber() && trigger.threshold.isNumber() -> {
                        messageService.sendMessage(chatId, "Порог не может менять тип")
                        return
                    }

                    operator.type == FieldType.TEXT && threshold.isNumber() -> {
                        messageService.sendMessage(chatId, "Оператор и порог должны быть одного типа")
                        return
                    }

                    operator.type == FieldType.NUMERIC && !threshold.isNumber() -> {
                        messageService.sendMessage(chatId, "Оператор и порог должны быть одного типа")
                        return
                    }
                }
            }

            if( cooldown == null && threshold == null && operator == null ) {
                messageService.sendMessage(chatId, "Не заданы параметры для изменения. Пример:\n" +
                        "/edit_name:triggerName;operator:GT;threshold:90;cooldown:5")
                return
            }

            operator?.let { trigger.operator = operator }
            threshold?.let { trigger.threshold = threshold }
            cooldown?.let { trigger.cooldownMinutes = cooldown.toInt() }
            val updated = triggerRepository.save(trigger)
            messageService.sendMessage(
                chatId, """
                    *Триггер был изменен и теперь выглядит так*
                    ID: ${updated.id}
                    Название: ${updated.name}
                    Сервис: ${updated.serviceName ?: "Все"}
                    Условие: ${updated.metric} ${updated.operator} ${updated.threshold}
                    Cooldown: ${updated.cooldownMinutes} мин
                    """.trimIndent()
            )
        } catch (e: Exception) {
            log.warn( "Ошибка при изменении триггера: ${e.message}")
        }
    }

    override fun showAllTriggersForService(chatId: String, serviceName: String) {
        val triggers = triggerRepository.findActiveTriggers(serviceName)
        if (triggers.isEmpty()) {
            messageService.sendMessage(chatId, "Активных триггеров для сервиса $serviceName не обнаружено")
            return
        }
        messageService.sendMessage(chatId, "Все активные триггеры для сервиса $serviceName:")
        triggers.map {
            messageService.sendMessage(
                chatId, """
                    ID: ${it.id}
                    Название: ${it.name}
                    Сервис: ${it.serviceName ?: "Все"}
                    Условие: ${it.metric} ${it.operator} ${it.threshold}
                    Cooldown: ${it.cooldownMinutes} мин
                    """.trimIndent()
            )
        }
    }

    override fun showAllActiveTriggers(chatId: String) {
        val triggers = triggerRepository.findByEnabledTrue()
        if (triggers.isEmpty()) {
            messageService.sendMessage(chatId, "Активных триггеров не обнаружено")
            return
        }
        messageService.sendMessage(chatId, "Все активные триггеры:")
        triggers.map {
            messageService.sendMessage(
                chatId, """
                    ID: ${it.id}
                    Название: ${it.name}
                    Сервис: ${it.serviceName ?: "Все"}
                    Условие: ${it.metric} ${it.operator} ${it.threshold}
                    Cooldown: ${it.cooldownMinutes} мин
                    """.trimIndent()
            )
        }
    }

    override fun showAllTriggers(chatId: String) {
        val triggers = triggerRepository.findAll()
        if (triggers.isEmpty()) {
            messageService.sendMessage(chatId, "Никаких триггеров не обнаружено")
            return
        }
        messageService.sendMessage(chatId, "Все триггеры:")
        triggers.map {
            messageService.sendMessage(
                chatId, """
                    ID: ${it.id}
                    Название: ${it.name}
                    Сервис: ${it.serviceName ?: "Все"}
                    Условие: ${it.metric} ${it.operator} ${it.threshold}
                    Cooldown: ${it.cooldownMinutes} мин
                    Активен: ${it.enabled}
                    """.trimIndent()
            )
        }
    }

    private fun String.extractValue(key: String): String? {
        return if (this.contains(key)) {
            this.substringAfter(key)
                .substringBefore(";")
                .takeIf { it.isNotEmpty() }
        } else null
    }

    private fun String.isNumber() = this.toDoubleOrNull() != null

    private fun checkDatabase(serviceName: String): Boolean =
        metricsRepository.findFirstByServiceName(serviceName).databaseStatus == "Null"
}