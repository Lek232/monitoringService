package com.test.monitoringService.service

import com.test.monitoringService.component.filler.FillTiggerUsecase
import com.test.monitoringService.component.telegramBot.TelegramBot
import com.test.monitoringService.configuration.ServiceConfig
import com.test.monitoringService.model.dto.toMetricsEntity
import com.test.monitoringService.model.dto.toPostgresEntity
import com.test.monitoringService.model.entity.MetricsEntity
import com.test.monitoringService.service.entity.MetricsService
import com.test.monitoringService.service.entity.PostgresService
import com.test.monitoringService.service.polling.MetricsPoller
import com.test.monitoringService.service.polling.PostgresPoller
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import java.net.SocketException

@Service
@EnableScheduling
class SchedulingService(
    fill: List<ServiceConfig.Service>,
    val pollingMetrics: MetricsPoller,
    val metricsEntityService: MetricsService,
    val pollingPostgres: PostgresPoller,
    val postgresEntityService: PostgresService,
    val fillTiggerDto: FillTiggerUsecase,
    val triggerService: TriggerCheckService,

    @Autowired(required = false)
    val telegramBot: TelegramBot? = null,
) {
    val services = fill

    val serviceNamesList = services.map { it.name }

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 10_000)
    fun scheduledPolling() {

        log.info("Опрос сервисов: ${services.map { it.name }}")

        services.forEach {

            runCatching {
                val metricEntity = pollingMetrics.pollingMetrics(it.url, it.name, it.apiKey).toMetricsEntity()
                metricsEntityService.saveMetrics(metricEntity)
                if (metricEntity.databaseName == "PostgreSQL" && metricEntity.databaseStatus == "UP") {
                    val postgresEntity = pollingPostgres.pollingPostgres(it.name, it.url, it.apiKey).toPostgresEntity()
                    postgresEntityService.savePostgres(postgresEntity)
                }
            }.onFailure { e ->
                val message = when (e) {
                    is IllegalArgumentException -> "Некорректный запрос для сервиса '${it.name}': ${e.message}"
                    is HttpClientErrorException.Forbidden -> "Неверный apiKey для сервиса: '${it.name}': ошибка ${e.message}"
                    is HttpClientErrorException.NotFound -> "Сервис не обнаружен: '${it.name}': ошибка ${e.message}"
                    is SocketException -> "Соединение было разорвано для сервиса: '${it.name}': ${e.message}"
                    is ResourceAccessException -> "Ошибка чтения: '${it.name}': ${e.message}"
                    is HttpServerErrorException.ServiceUnavailable -> "Сервис не доступен: '${it.name}': ${e.message}"
                    else -> "Ошибка. Сервис: '${it.name}': ${e.message}"
                    }
                log.warn(message)
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name, errorCollect = message))
            }
        }
    }

    @Scheduled(fixedRate = 10_000)
    fun scheduledReport() {
        telegramBot?.sendReport()
    }

    @Scheduled(initialDelay = 10_000, fixedRate = 10_000)
    fun scheduledTrigger() {
        log.info("Проверка триггеров для сервисов: $serviceNamesList")
        fillTiggerDto.fillTriggerDto(serviceNamesList).map {
            triggerService.checkTriggers(it)
        }
    }
}
