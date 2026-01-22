package com.test.monitoringService.service

import com.test.monitoringService.component.filler.FillTiggerDto
import com.test.monitoringService.component.telegramBot.TelegramBot
import com.test.monitoringService.configuration.EnvServiceConfig
import com.test.monitoringService.model.entity.MetricsEntity
import com.test.monitoringService.service.entity.MetricsService
import com.test.monitoringService.service.entity.PostgresService
import com.test.monitoringService.service.polling.MetricsPoller
import com.test.monitoringService.service.polling.PostgresPoller
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    fill: EnvServiceConfig,
    val pollingMetrics: MetricsPoller,
    val metricsEntityService: MetricsService,
    val pollingPostgres: PostgresPoller,
    val postgresEntityService: PostgresService,
    val triggerService: TriggerCheckService,
    val fillTiggerDto: FillTiggerDto,
    val telegramBot: TelegramBot?,
) {
    val services = fill.serviceConfig()

    val serviceNamesList = services.map { it.name }

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 10_000)
    fun scheduledPolling() {
        log.info("Опрос сервисов: ${services.map { it.name }}")

        // TODO если мы не ожидаем данные дальше, то используй forEach
        services.map {

            /** TODO Если мы получаем ошибку, то можно вынести ее в finally
             *  и можно использовать runCatching от kotlin, т.е.
             *
             *  runCatching {
             *      *code
             *  }.onFailure { exception ->
             *     val message =  when(exception) {
             *          is IllegalArgumentException -> "log1"
             *          is HttpClientErrorException -> "log2"
             *          ...
             *      }
             *
             *      metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name, message))
             *
             *  }.getOrNull()
             *
             *  *  runCatching {
             *      *code
             *  }.getOrElse { exception ->
             *     val message =  when(exception) {
             *          is IllegalArgumentException -> "log1"
             *          is HttpClientErrorException -> "log2"
             *          ...
             *      }
             *
             *     MetricsEntity(serviceName = it.name, message)
             *  }
             *
             *
             *             runCatching {
             *                 metricEntity
             *             }.getOrElse { exception ->
             *                 val message = when (exception) {
             *                     is IllegalArgumentException -> "log1"
             *                     is HttpClientErrorException -> "log2"
             *                     else -> {"Неизвестная"}
             *                 }
             *                 MetricsEntity(serviceName = it.name)
             *             }.let(metricsEntityService::saveMetrics)
             */
            try {
                val metricDto = pollingMetrics.pollingMetrics(it.url, it.name, it.apiKey)
                val metricEntity = metricDto.toMetricsEntity()
                metricsEntityService.saveMetrics(metricEntity)
                if (metricDto.databaseName == "PostgreSQL" && metricDto.databaseStatus == "UP") {
                    val postgresDto = pollingPostgres.pollingPostgres(it.name, it.url, it.apiKey)
                    val postgresEntity = postgresDto.toPostgresEntity()
                    postgresEntityService.savePostgres(postgresEntity)
                }
            } catch (e: IllegalArgumentException) {
                log.warn("Некорректный запрос для сервиса '${it.name}': ${e.message}")
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name))
            } catch (e: HttpClientErrorException.Forbidden) {
                log.warn("Неверный apiKey для сервиса: '${it.name}': ошибка ${e.message}")
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name))
            } catch (e: HttpClientErrorException.NotFound) {
                log.warn("Сервис не обнаружен: '${it.name}': ошибка ${e.message}")
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name))
            } catch (e: SocketException) {
                log.warn("Соединение было разорвано для сервиса: '${it.name}': ${e.message}")
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name))
            } catch (e: ResourceAccessException) {
                log.warn("Ошибка чтения: '${it.name}': ${e.message}")
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name))
            } catch (e: HttpServerErrorException.ServiceUnavailable) {
                log.warn("Сервис не доступен: '${it.name}': ${e.message}")
                metricsEntityService.saveMetrics(MetricsEntity(serviceName = it.name))
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
