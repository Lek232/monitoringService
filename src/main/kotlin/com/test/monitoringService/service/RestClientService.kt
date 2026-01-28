package com.test.monitoringService.service

import com.test.monitoringService.model.dto.GetHealthDto.Health
import com.test.monitoringService.model.dto.GetMetricDto.Metric
import com.test.monitoringService.model.dto.GetPostgresMetricsDto
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

/**
 * TODO Используй [jakarta.persistence.EntityManager] и всместо Map<String, Any> Возращай DTO
 */
/**
 * TODO Вместо .body(String::class.java) Необходимо определить интерфейс который мы ожидаем и который будем маппить
 */

@Service
class RestClientService {

    private val restClient = RestClient.builder()
        .build()

    fun getHealth(serviceUrl: String, apiKey: String): Health =
        restClient.get()
            .uri("${serviceUrl}/actuator/health")
            .header("X-API-Key", apiKey)
            .retrieve()
            .body(Health::class.java)
            ?: Health()

    fun getMetric(serviceUrl: String, metric: String, statistic: String, apiKey: String): Double {
        val metric = restClient.get()
            .uri("${serviceUrl}/actuator/metrics/${metric}")
            .header("X-API-Key", apiKey)
            .retrieve()
            .body(Metric::class.java) ?: Metric()

        val value = metric.measurements
            .firstOrNull {
                it?.statistic == statistic
            }
            ?.value ?: 0.0

        return if (value < 0) 0.0 else value
    }

    fun getPostgres(serviceUrl: String, apiKey: String): GetPostgresMetricsDto {
        return restClient.get()
            .uri("${serviceUrl}/api/postgres-metrics")
            .header("X-API-Key", apiKey)
            .retrieve()
            .body(GetPostgresMetricsDto::class.java)
            ?: GetPostgresMetricsDto()
    }
}