package com.test.monitoringService.component.telegramBot

import com.test.monitoringService.configuration.EnvServiceConfig
import com.test.monitoringService.service.TelegramSenderService
import com.test.monitoringService.service.interfaces.TriggerInterface
import com.test.monitoringService.service.report.MetricsReportService
import com.test.monitoringService.service.report.PostgresReportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * TODO Необходимо сделать REST контроллер, для взаимодействия с сервисом, через телеграм хорошо, но нужно по HTTP ходить
 */
@Component
class TelegramBot(
    val botProperties: BotProperties,
    val reportService: MetricsReportService,
    // Не тянем конфигурацию, тянем сразу список сервисов
    fill: EnvServiceConfig,
    val postgresReportService: PostgresReportService,
    private val triggerInterface: TriggerInterface,
    val senderService: TelegramSenderService,
) : SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {


    val servicesList = fill.serviceConfig().map { it.name }

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val reportChatIds = mutableSetOf<String>()

    private val alertChatIds = mutableSetOf<String>()

    override fun getBotToken() = botProperties.token

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer {
        return this
    }

    override fun consume(update: Update) {
        if (!update.hasMessage() || !update.message.hasText()) return

        val messageText = update.message.text.trim()
        val chatId = update.message.chatId.toString()

        when (messageText) {
            "/start" -> {
                if (reportChatIds.add(chatId)) {
                    log.info("Добавлен получатель $chatId")
                    senderService.sendMessage(chatId, "Начата отправка отчетов")
                    senderService.sendHtml(chatId, reportService.generateHtmlMetrics(servicesList))
                } else {
                    senderService.sendMessage(chatId, "Вы уже получаете отчеты")
                }
            }

            "/stop" -> {
                if (reportChatIds.remove(chatId)) {
                    log.info("Убран получатель $chatId")
                    senderService.sendMessage(chatId, "Отправка отчетов окончена")
                } else {
                    senderService.sendMessage(chatId, "Отправка отчетов уже была окончена")
                }
            }

            "/help" -> {
                senderService.sendMessage(
                    chatId, """ Команды:
                /start - начать получать отчеты
                /stop - прекратить получать отчеты
                /postgres - отчет по SQL запросам
                /report - получить отчет сейчас
                /triggers - триггеры"""
                )
            }

            "/postgres" -> {
                senderService.sendHtml(chatId, postgresReportService.generateHtmlPostgres(servicesList))
                log.info("Запрошен отчет по SQL запросам $chatId")
            }

            "/report" -> {
                if (reportChatIds.add(chatId)) {
                    senderService.sendHtml(chatId, reportService.generateHtmlMetrics(servicesList))
                    reportChatIds.remove(chatId)
                } else {
                    senderService.sendHtml(chatId, reportService.generateHtmlMetrics(servicesList))
                }
                log.info("Запрошен отчет вручную $chatId")
            }

            "/triggers" -> {
                senderService.sendMessage(
                    chatId, """
                    /create_{triggerName} - создать триггер
                    /enable_{triggerName} - активировать триггер
                    /disable_{triggerName} - выключить триггер
                    /edit_{triggerName} - изменить триггер
                    /delete_{triggerName} - удалить триггер
                    /allForService_{serviceName} - список всех триггеров сервиса
                    /all - список всех триггеров
                    /allActive - список всех активных триггеров
                    /stopTriggers - остановить получение триггеров
                    /continueTriggers - возобновить получение триггеров
                    /triggersHelp - помощь
                """.trimIndent()
                )
            }

            "/triggersHelp" -> {
                showHelp(chatId)
            }

            "/stopTriggers" -> {
                alertChatIds.remove(chatId)
                senderService.sendMessage(chatId, "Получение триггеров остановлено")
            }

            "/continueTriggers" -> {
                alertChatIds.add(chatId)
                senderService.sendMessage(chatId, "Получение триггеров возобновлено")
            }

            "/all" -> {
                triggerInterface.showAllTriggers(chatId)
            }

            "/allActive" -> {
                triggerInterface.showAllActiveTriggers(chatId)
            }

            else -> {
                when {
                    messageText.startsWith("/enable_") -> {
                        val triggerName = messageText.substringAfter("_")
                        triggerInterface.enableTrigger(chatId, triggerName)
                        alertChatIds.add(chatId)
                    }

                    messageText.startsWith("/disable_") -> {
                        val triggerName = messageText.substringAfter("_")
                        triggerInterface.disableTrigger(chatId, triggerName)
                    }

                    messageText.startsWith("/create_") -> {
                        val createParam = messageText.substringAfter("_")
                        triggerInterface.createTrigger(chatId, createParam)
                        alertChatIds.add(chatId)
                    }

                    messageText.startsWith("/edit_") -> {
                        val triggerParam = messageText.substringAfter("_")
                        triggerInterface.editTrigger(chatId, triggerParam)
                    }

                    messageText.startsWith("/delete_") -> {
                        val triggerName = messageText.substringAfter("_")
                        triggerInterface.deleteTrigger(chatId, triggerName)
                    }

                    messageText.startsWith("/allForService_") -> {
                        val serviceName = messageText.substringAfter("_")
                        triggerInterface.showAllTriggersForService(chatId, serviceName)
                    }

                    else -> {
                        senderService.sendMessage(
                            chatId, """Вы написали: { $messageText }, доступные команды:
                            /start - начать получать отчеты
                            /stop - прекратить получать отчеты
                            /postgres - отчет по SQL запросам
                            /report - получить отчет сейчас
                            /triggers - триггеры"""
                        )
                    }
                }
            }
        }
    }

    fun sendReport() {
        if (reportChatIds.isEmpty()) {
            log.info("Нет получателя для отправки отчета")
            return
        }
        log.info("Начинаю рассылку отчета")
        for (chatId in reportChatIds) {
            try {
                senderService.sendHtml(chatId, reportService.generateHtmlMetrics(servicesList))
            } catch (e: Exception) {
                log.info("Ошибка отправки в чат $chatId: ${e.message}")
            }
        }
    }

    fun sendAlert(message: String) {
        for (chatId in alertChatIds) {
            try {
                senderService.sendMessage(chatId, message)
            } catch (e: Exception) {
                log.info("Ошибка отправки уведомления о срабатывании триггера $chatId: ${e.message}")
            }
        }
    }

    private fun showHelp(chatId: String) {
        val message = """<pre>
*Доступные метрики для триггеров сервиса:*
 HEALTH_STATUS - состояние сервиса text
 AVAILABILITY - доступность сервиса %
 MEMORY_LOAD - нагрузка на память % 
 CPU_USAGE - использование ЦПУ %
 THREADS_LIVE - потоки
 CONSUMPTION_DIFFERENCE - рост потребления сервиса

*Доступные метрики для триггеров базы данных:*
 DATABASE_STATUS - состояние БД text
 DATABASE_LOAD - нагрузка на БД %
 TOTAL_QUERIES - всего запросов
 TOTAL_CALLS - всего вызовов
 MAX_TOTAL_TIME_MS - время самого долгого запроса ms
 AVG_EXEC_TIME_MS - среднее время выполнения запроса ms
 MAX_STDDEV_EXEC_TIME_MS - самое высокое отклонение ms 
 AVG_CACHE_HIT - попадание в кэш %

*Операторы для чисел:*
EQ - равно
NE - не равно  
GT - больше
LT - меньше
GTE - больше или равно
LTE - меньше или равно

*Операторы для текста:*
CONTAINS - содержит
NOT_CONTAINS - не содержит

*Пример создания триггера:*
```
Имя триггера: triggerName !!! Имя должно быть уникальным !!!
Сервис: serviceName или пустое для триггера по всем сервисам
Метрика: CPU_USAGE
Оператор: GT
Порог: 80
Cooldown: 5
```

*Формат команды создания:*
[create_{triggerName};{сервис};{метрика};{оператор};{порог};{cooldown}]

*Пример:*
[/create_triggerName;serviceName;CPU_USAGE;GT;80;5]

*Пример изменения триггера:*

*Формат команды изменения:*
[edit_name:{triggerName};operator:{оператор};threshold:{порог};cooldown:{cooldown}]

*Пример:*
[/edit_name:triggerName;operator:GT;threshold:90;cooldown:5]

*Можно указывать конкретные поля, которые нужно изменить. Имя триггера, сервис, метрика, не изменяются
*Имя указывается обязательно*
</pre>""".trimIndent()
        senderService.sendHtml(chatId, message)
    }
}
