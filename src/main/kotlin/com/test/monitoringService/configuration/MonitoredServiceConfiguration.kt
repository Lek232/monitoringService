package com.test.monitoringService.configuration

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

data class Service(
    val name: String,
    val url: String,
    val apiKey: String
)
@Configuration
class EnvServiceConfig(
) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * TODO Используй данные из .properties или .yaml конфигурации spring, с помощью аннотации [org.springframework.beans.factory.annotation.Value]
     * вместо парсинга файла .env, не хорошая практика.
     *
     *
     * можно сделать так:
     * @ConfigurationProperties(prefix = "services")
     * public record ServicesEnvironment(
     *         List<Services> list
     * )
     *
     *
     * @Setter
     * @Getter
     * public class ServiceToken {
     *     private String name;
     *     private String apiKey;
     * }
     *
     * а в самом .yaml
     * services:
     *      list:
     *         - name: ""
     *           apiKey: ""
     *
     *
     * и далее в конструктор конфигурации ожидать ServicesEnvironment
     * также над конфигурацией повесить
     * @EnableConfigurationProperties(ServicesEnvironment.class)
     * без этого не будет подтягиваться
     */
    @PostConstruct
    fun loadEnvFile() {
        val envVar = System.getenv("SERVICES")
        if (envVar != null) {
            System.setProperty("SERVICES", envVar)
            log.info("Сервисы загружены из переменной окружения")
            return
        }

    }

    /**
     * TODO сделай это бином, что бы сервисы возвращались
     */
    @Bean
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
