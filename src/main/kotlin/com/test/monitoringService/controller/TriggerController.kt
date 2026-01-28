package com.test.monitoringService.controller


import com.test.monitoringService.service.interfaces.TriggerInterface
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TriggerController(
    val triggerInterface: TriggerInterface,
) {

    @GetMapping("/all")
    fun getAllTriggers():String{
       return htmlWrap(triggerInterface.showAllTriggers())
    }

    @GetMapping("/allActive")
    fun getAllActiveTriggers():String{
        return htmlWrap(triggerInterface.showAllActiveTriggers())
    }

    @GetMapping("/allForService_{serviceName}")
    fun getAllTriggersForService(@PathVariable serviceName: String): String {
        return htmlWrap(triggerInterface.showAllTriggersForService(serviceName))
    }

    @PostMapping("/create_{createParam}")
    fun createTrigger(@PathVariable createParam: String): String {
        return htmlWrap(triggerInterface.createTrigger(createParam))
    }

    @PutMapping("/disable_{triggerName}")
    fun disableTrigger(@PathVariable triggerName: String): String {
        return htmlWrap(triggerInterface.disableTrigger(triggerName))
    }

    @PutMapping("/enable_{triggerName}")
    fun enableTrigger(@PathVariable triggerName: String): String {
        return htmlWrap(triggerInterface.enableTrigger(triggerName))
    }

    @DeleteMapping("/delete_{triggerName}")
    fun deleteTrigger(@PathVariable triggerName: String): String {
        return htmlWrap(triggerInterface.deleteTrigger(triggerName))
    }

    @PutMapping("/edit_{triggerName}")
    fun editTrigger(@PathVariable triggerName: String): String {
        return htmlWrap(triggerInterface.editTrigger(triggerName))
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