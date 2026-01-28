package com.test.monitoringService.service

import com.test.monitoringService.configuration.BotConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
@ConditionalOnProperty(
    name = ["telegram.bot.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class TelegramSenderService(
    bot: BotConfig.Bot,
) {
    val telegramClient = OkHttpTelegramClient(bot.token)

    fun sendMessage(chatId: String, text: String) {
        val message = SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .build()
        telegramClient.execute(message)
    }

    fun sendHtml(chatId: String, htmlReport: String) {
        val sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(htmlReport)
            .parseMode("html")
            .build()
        telegramClient.execute(sendMessage)
    }
}
