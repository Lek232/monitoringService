package com.test.monitoringService.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
/**
 *
 * Сделать с помощью аннотации @ConfigurationProperties(prefix = "telegram.bot")
 * и сделать отдельно бин конфигурации, которая будет возвращать этот контейнер или использовать
 * @EnableConfigurationProperties(BotPropertiesConfig::class), где используется
 *
 */

@Configuration
@EnableConfigurationProperties(BotConfig.BotProperties::class)
class BotConfig {

    @ConfigurationProperties(prefix = "telegram.bot")
    data class BotProperties(
        var username: String = "",
        var token: String = ""
    )
}
