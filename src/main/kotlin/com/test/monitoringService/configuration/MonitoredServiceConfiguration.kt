package com.test.monitoringService.configuration

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.io.File

data class Service(
    val name: String,
    val url: String,
    val apiKey: String
)

@Configuration
class EnvServiceConfig {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun loadEnvFile() {
        val envVar = System.getenv("SERVICES")
        if (envVar != null) {
            System.setProperty("SERVICES", envVar)
            log.info("Сервисы загружены из переменной окружения")
            return
        }

        val envFile = File(".env")
        if (envFile.exists()) {
            log.info("Найден .env файл")
            envFile.readLines()
                .filter { it.isNotBlank() && it.contains("=") }
                .forEach { line ->
                    val (key, value) = line.split("=", limit = 2)
                    System.setProperty(key.trim(), value.trim())
                }
        } else {
            log.warn("Не найден .env файл или переменная окружения")
        }
    }

    fun serviceConfig(): List<Service> {
        val rawServices = System.getProperty("SERVICES", "")

        return if (rawServices.isBlank()) {
            emptyList()
        } else {
            rawServices.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .map { item ->
                    val parts = item.split("|")
                    Service(
                        parts.getOrElse(0) { "" },
                        parts.getOrElse(1) { "" },
                        parts.getOrElse(2) { "" }
                    )
                }
        }
    }
}