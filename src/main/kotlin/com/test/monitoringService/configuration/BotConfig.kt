package com.test.monitoringService.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * Сделать с помощью аннотации @ConfigurationProperties(prefix = "telegram.bot")
 * и сделать отдельно бин конфигурации, которая будет возвращать этот контейнер или использовать
 * @EnableConfigurationProperties(BotProperties::class), где используется
 *
 */

@Configuration
@EnableConfigurationProperties(BotConfig.BotProperties::class)
class BotConfig {

    data class Bot(
        var username: String = "",
        var token: String = "",
    )

    @ConfigurationProperties(prefix = "telegram")
    data class BotProperties(
        val bot: Bot
    )

    @Bean
    fun botFromYaml(botProperties: BotProperties): Bot {
        return botProperties.bot
    }
}
