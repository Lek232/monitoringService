package com.test.monitoringService.controller


import com.test.monitoringService.service.interfaces.TriggerInterface
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class NotificationController(
    val triggerInterface: TriggerInterface,
) {

    @GetMapping("/notifications")
    fun getNotifications (): String{
        return htmlWrap(triggerInterface.notify())
    }

    fun htmlWrap(text: String): String{
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
$text
            <pre>
            </div>""".trimIndent()
    }
}