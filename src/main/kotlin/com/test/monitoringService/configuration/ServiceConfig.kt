package com.test.monitoringService.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(ServiceConfig.ServicesToList::class)
class ServiceConfig {

    data class Service(
        var name: String,
        var url: String,
        var apiKey: String
    )

    @ConfigurationProperties(prefix = "services")
    data class ServicesToList(
        var list: List<Service>
    ) {
        @Bean
        fun servicesListFromYaml(): List<Service> {
            return list
        }
    }
}
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