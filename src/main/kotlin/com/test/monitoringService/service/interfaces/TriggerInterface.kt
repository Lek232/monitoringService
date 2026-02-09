package com.test.monitoringService.service.interfaces

interface TriggerInterface {

    fun disableTrigger(triggerName: String): String
    fun enableTrigger(triggerName: String): String
    fun deleteTrigger(triggerName: String): String
    fun createTrigger(createParam: String): String
    fun editTrigger(triggerEditWithWhiteSpaces: String): String
    fun showAllTriggersForService(serviceName: String): String
    fun showAllActiveTriggers(): String
    fun showAllTriggers(): String
}