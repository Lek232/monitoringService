package com.test.monitoringService.component.telegramBot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class BotProperties(

    @Value($$"${telegram.bot.username}")
    val username: String,

    @Value($$"${telegram.bot.token}")
    val token: String,
)