package com.test.monitoringService.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.module.kotlin.jacksonObjectMapper

@Service
class RestClientService {

    val mapper = jacksonObjectMapper()

    private val restClient = RestClient.builder()
        .build()

    fun getHealth(serviceUrl: String, apiKey: String): String =
        restClient.get()
            .uri("${serviceUrl}/actuator/health")
            .header("X-API-Key", apiKey)
            .retrieve()
            .body<String>() ?: ""


    /**
     * TODO Вместо .body(String::class.java) Необходимо определить интерфейс который мы ожидаем и который будем маппить
     */
    fun getMetric(serviceUrl: String, metric: String, statistic: String, apiKey: String): Double {
        val rootNode = mapper.readTree(
            restClient.get()
                .uri("${serviceUrl}/actuator/metrics/${metric}")
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(String::class.java)
        )
        val value = rootNode["measurements"]
            .firstOrNull { it["statistic"].asString() == statistic }
            ?.get("value")
            ?.asDouble() ?: 0.0
        return if (value < 0) 0.0 else value
    }

    fun getPostgres(serviceUrl: String, apiKey: String): String {
        return restClient.get()
            .uri("${serviceUrl}/api/postgres-metrics")
            .header("X-API-Key", apiKey)
            .retrieve()
            .body<String>() ?: ""
    }
}
