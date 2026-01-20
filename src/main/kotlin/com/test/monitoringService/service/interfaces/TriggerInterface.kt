package com.test.monitoringService.service.interfaces

interface TriggerInterface {

    fun disableTrigger(chatId: String, triggerName: String)

    fun enableTrigger(chatId: String, triggerName: String)

    fun deleteTrigger(chatId: String, triggerName: String)

    fun createTrigger(chatId: String, createParam: String)

    fun editTrigger(chatId: String, triggerEdit: String)

    fun showAllTriggersForService(chatId: String, serviceName: String)

    fun showAllActiveTriggers(chatId: String)

    fun showAllTriggers(chatId: String)
}