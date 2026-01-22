package com.test.monitoringService.component.telegramBot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 *
 * Сделать с помощью аннотации @ConfigurationProperties(prefix = "telegram.bot")
 * и сделать отдельно бин конфигурации, которая будет возвращать этот контейнер или использовать
 * @EnableConfigurationProperties(BotProperties::class), где используется
 *
 */
@Component
data class BotProperties(

    @Value($$"${telegram.bot.username}")
    val username: String,

    @Value($$"${telegram.bot.token}")
    val token: String,
)
