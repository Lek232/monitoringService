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