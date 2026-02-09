package com.test.monitoringService.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ServicesToList::class)
class ServiceConfig {

    data class Service(
        var name: String,
        var url: String,
        var apiKey: String
    )

    @Bean
    fun servicesListFromYaml(services: ServicesToList): List<Service> {
        return services.list
    }
}

@ConfigurationProperties(prefix = "services")
data class ServicesToList(
    var list: List<ServiceConfig.Service>
)