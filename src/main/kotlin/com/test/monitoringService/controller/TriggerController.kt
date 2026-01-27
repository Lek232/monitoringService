package com.test.monitoringService.controller


import com.test.monitoringService.service.interfaces.TriggerInterface
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TriggerController(
    val triggerInterface: TriggerInterface,
) {

    @GetMapping("/all")
    fun getAllTriggers():String{
       return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.showAllTriggers()}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/allActive")
    fun getAllActiveTriggers():String{
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.showAllActiveTriggers()}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/allForService_{serviceName}")
    fun getAllTriggersForService(@PathVariable serviceName: String): String {
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.showAllTriggersForService(serviceName)}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/create_{createParam}")
    fun createTrigger(@PathVariable createParam: String): String {
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.createTrigger(createParam)}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/disable_{triggerName}")
    fun disableTrigger(@PathVariable triggerName: String): String {
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.disableTrigger(triggerName)}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/enable_{triggerName}")
    fun enableTrigger(@PathVariable triggerName: String): String {
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.enableTrigger(triggerName)}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/delete_{triggerName}")
    fun deleteTrigger(@PathVariable triggerName: String): String {
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.deleteTrigger(triggerName)}
            </pre>
            </div>""".trimIndent()
    }

    @GetMapping("/edit_{triggerName}")
    fun editTrigger(@PathVariable triggerName: String): String {
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
${triggerInterface.editTrigger(triggerName)}
            <pre>
            </div>""".trimIndent()
    }
}