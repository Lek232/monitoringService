package com.test.monitoringService.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BotProperties::class)
class BotConfig {

    data class Bot(
        var username: String,
        var token: String,
    )

    @Bean
    fun botFromYaml(botProperties: BotProperties): Bot {
        return botProperties.bot
    }
}

@ConfigurationProperties(prefix = "telegram")
data class BotProperties(
    val bot: BotConfig.Bot
)


