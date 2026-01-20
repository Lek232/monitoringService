package com.test.monitoringService.service

import com.test.monitoringService.component.telegramBot.BotProperties
import org.springframework.stereotype.Service
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class TelegramSenderService(
    val botProperties: BotProperties,
) {
    val telegramClient = OkHttpTelegramClient(getBotToken())

    fun getBotToken() = botProperties.token

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