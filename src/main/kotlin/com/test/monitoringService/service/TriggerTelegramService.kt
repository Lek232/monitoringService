package com.test.monitoringService.service

import com.test.monitoringService.model.entity.FieldType
import com.test.monitoringService.model.entity.TriggerEntity
import com.test.monitoringService.model.entity.TriggerField
import com.test.monitoringService.model.entity.TriggerOperator
import com.test.monitoringService.repository.interfaces.MetricsEntityRepository
import com.test.monitoringService.repository.interfaces.TriggerRepository
import com.test.monitoringService.service.interfaces.TriggerInterface
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class TriggerTelegramService(
    val triggerRepository: TriggerRepository,
    val metricsRepository: MetricsEntityRepository
) : TriggerInterface {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun disableTrigger(triggerName: String): String {
        val trigger: TriggerEntity =
            triggerRepository.findByName(triggerName) ?: return "Триггер с именем $triggerName не найден"
        trigger.enabled = false
        triggerRepository.save(trigger)
        return "Триггер с именем $triggerName отключен"
    }

    override fun enableTrigger(triggerName: String): String {
        val trigger: TriggerEntity =
            triggerRepository.findByName(triggerName) ?: return "Триггер с именем $triggerName не найден"
        trigger.enabled = true
        triggerRepository.save(trigger)
        return "Триггер с именем $triggerName активирован"
    }

    @Transactional
    override fun deleteTrigger(triggerName: String): String {
        return if (triggerRepository.deleteByName(triggerName) == 0)
            "Триггер $triggerName не был обнаружен"
        else
            "Триггер $triggerName был удален"
    }

    @Transactional
    override fun createTrigger(createParam: String): String {
        try {
            val param = createParam.replace(" ", "").split(",")
            if (param.size != 6) {
                println(param)
                return "Заданы не все необходимые параметры. Пример:\n" +
                        "/create/triggerName,serviceName,CPU_USAGE,GT,80,5"
            }
            val name = param[0]
            val serviceName = param[1]
            val metric = TriggerField.valueOf(param[2])
            val operator = TriggerOperator.valueOf(param[3])
            val threshold = param[4]
            val cooldown = param[5].toInt()

            if(checkServiceExist(serviceName)){
                return "Сервис с именем $serviceName не существует"
            }

            if(triggerRepository.findByName(name) != null){
                return "Триггер с именем $name уже существует"
            }

            if (metric.belongs == TriggerField.FieldBelongsDb.TRUE && checkDatabase(serviceName)) {
                return "У сервиса нет базы данных"
            }

            when {
                metric.type != operator.type -> {
                    return "Метрика и оператор должны быть одного типа"
                }

                metric.type == FieldType.TEXT && threshold.isNumber() || metric.type == FieldType.NUMERIC && !threshold.isNumber() -> {
                    return "Метрика и порог должны быть одного типа"
                }

                operator.type == FieldType.TEXT && threshold.isNumber() || operator.type == FieldType.NUMERIC && !threshold.isNumber() -> {
                    return "Оператор и порог должны быть одного типа"
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
            return  """ 
                    *Триггер создан и активирован успешно*
                    ------------------------------------
                    ID: ${saved.id}
                    Название: ${saved.name}
                    Сервис: ${saved.serviceName}
                    Условие: ${saved.metric} ${saved.operator} ${saved.threshold}
                    Cooldown: ${saved.cooldownMinutes} мин
                    Используйте /disable_${saved.name} чтобы выключить
                    ------------------------------------
                    """.trimIndent()

        } catch (e: IllegalArgumentException) {
            return "Ошибка: ${e.message}. Проверьте правильность ввода"
        } catch (_: EmptyResultDataAccessException) {
            return "Сервиса с таким именем не существует"
        } catch (e: Exception) {
            log.warn( "Ошибка при создании триггера: ${e.message}")
        }
        return ""
    }

    override fun editTrigger(triggerEditWithWhiteSpaces: String): String {
        try {
            val triggerEdit = triggerEditWithWhiteSpaces.replace(" ", "")
            val triggerName = triggerEdit.substringAfter("name:").substringBefore(",")
            val operator = triggerEdit.extractValue("operator:")?.let { TriggerOperator.valueOf(it) }
            val threshold = triggerEdit.extractValue("threshold:")
            val cooldown = triggerEdit.extractValue("cooldown:")

            val trigger = triggerRepository.findByName(triggerName) ?: return "Триггер с именем $triggerName не найден"

            run block@{
                when {
                    operator == null -> {
                        return@block
                    }

                    operator.type != trigger.operator.type -> {
                        return "Оператор не может менять тип"
                    }

                    threshold == null -> {
                        return@block
                    }

                    threshold.isNumber() && !trigger.threshold.isNumber() -> {
                        return "Порог не может менять тип"
                    }

                    !threshold.isNumber() && trigger.threshold.isNumber() -> {
                        return "Порог не может менять тип"
                    }

                    operator.type == FieldType.TEXT && threshold.isNumber() -> {
                        return "Оператор и порог должны быть одного типа"
                    }

                    operator.type == FieldType.NUMERIC && !threshold.isNumber() -> {
                        return "Оператор и порог должны быть одного типа"
                    }
                }
            }

            if( cooldown == null && threshold == null && operator == null ) {
                return "Не заданы параметры для изменения. Пример:\n" +
                        "/edit/name:triggerName,operator:GT,threshold:90,cooldown:5"
            }

            operator?.let { trigger.operator = operator }
            threshold?.let { trigger.threshold = threshold }
            cooldown?.let { trigger.cooldownMinutes = cooldown.toInt() }
            val updated = triggerRepository.save(trigger)
            return  """ 
                    *Триггер был изменен и теперь выглядит так*
                    ------------------------------------
                    ID: ${updated.id}
                    Название: ${updated.name}
                    Сервис: ${updated.serviceName}
                    Условие: ${updated.metric} ${updated.operator} ${updated.threshold}
                    Cooldown: ${updated.cooldownMinutes} мин
                    ------------------------------------
                    """.trimIndent()
        } catch (e: Exception) {
            log.warn( "Ошибка при изменении триггера: ${e.message}")
        }
        return ""
    }

    override fun showAllTriggersForService(serviceName: String): String {
        val triggers = triggerRepository.findActiveTriggers(serviceName)
        if (triggers.isEmpty()) {
            return "Активных триггеров для сервиса $serviceName не обнаружено"
        }
        return "Все активные триггеры для сервиса $serviceName:\n------------------------------------\n" +
        triggers.map {
            "\n" +
                    """   
                    ID: ${it.id}
                    Название: ${it.name}
                    Сервис: ${it.serviceName}
                    Условие: ${it.metric} ${it.operator} ${it.threshold}
                    Cooldown: ${it.cooldownMinutes} мин
                    ------------------------------------
                    """.trimIndent() + "\n"
        }
    }

    override fun showAllActiveTriggers(): String {
        val triggers = triggerRepository.findByEnabledTrue()
        if (triggers.isEmpty()) {
            return "Активных триггеров не обнаружено"
        }
        return "Все активные триггеры:\n------------------------------------\n" +
        triggers.map {
            "\n" +
                    """
                    ID: ${it.id}
                    Название: ${it.name}
                    Сервис: ${it.serviceName}
                    Условие: ${it.metric} ${it.operator} ${it.threshold}
                    Cooldown: ${it.cooldownMinutes} мин
                    ------------------------------------
                    """.trimIndent() + "\n"
        }
    }

    override fun showAllTriggers(): String {
        val triggers = triggerRepository.findAll()
        if (triggers.isEmpty()) {
            return "Никаких триггеров не обнаружено"
        }
        return "Все триггеры:\n------------------------------------\n" +
        triggers.map {
            "\n" +
                    """ 
                    ID: ${it.id}
                    Название: ${it.name}
                    Сервис: ${it.serviceName}
                    Условие: ${it.metric} ${it.operator} ${it.threshold}
                    Cooldown: ${it.cooldownMinutes} мин
                    Активен: ${it.enabled}
                    ------------------------------------
                    """.trimIndent() + "\n"
        }.toString()
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
        metricsRepository.findFirstByServiceName(serviceName)?.databaseStatus == "Null"

    private fun checkServiceExist(serviceName: String): Boolean{
        return metricsRepository.findFirstByServiceName(serviceName)?.serviceName?.isEmpty() ?: true
    }
}