package com.test.monitoringService.controller

import com.test.monitoringService.configuration.ServiceConfig
import com.test.monitoringService.service.report.MetricsReportService
import com.test.monitoringService.service.report.PostgresReportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ReportController(
    val postgresReportService: PostgresReportService,
    services:  List<ServiceConfig.Service>,
    val reportService: MetricsReportService,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    val servicesList = services.map { it.name }

    @GetMapping("/postgres")
    fun getPostgresReportHtml(): String {
        log.info("Запрошен отчет по SQL запросам")
        return """ <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            ${postgresReportService.generateHtmlPostgres(servicesList)}
            </div>"""
    }

    @GetMapping("/report")
    fun getMetricsReportHtml(): String {
        log.info("Запрошен отчет по метрикам")
        return """<div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            ${reportService.generateHtmlMetrics(servicesList)}
            </div>"""
    }

    @GetMapping("/help")
    fun getHelp(): String {
        log.info("Запрошена помощь по командам")
        return showHelp()
    }

    fun showHelp(): String {
        return """ 
            <div style="
            display: flex;
            justify-content: center;
            align-items: center;
            ">
            <pre>
            Команды:
                /postgres - отчет по SQL запросам\n
                /report - получить отчет сейчас\n
                /create_{triggerName} - создать триггер\n
                /enable_{triggerName} - активировать триггер\n
                /disable_{triggerName} - выключить триггер\n
                /edit_{triggerName} - изменить триггер\n
                /delete_{triggerName} - удалить триггер\n
                /allForService_{serviceName} - список всех триггеров сервиса\n
                /all - список всех триггеров\n
                /allActive - список всех активных триггеров\n
                
               
                *Доступные метрики для триггеров сервиса:*
                 HEALTH_STATUS - состояние сервиса text
                 AVAILABILITY - доступность сервиса %
                 MEMORY_LOAD - нагрузка на память % 
                 CPU_USAGE - использование ЦПУ %
                 THREADS_LIVE - потоки
                 CONSUMPTION_DIFFERENCE - рост потребления сервиса
                
                *Доступные метрики для триггеров базы данных:*
                 DATABASE_STATUS - состояние БД text
                 DATABASE_LOAD - нагрузка на БД %
                 TOTAL_QUERIES - всего запросов
                 TOTAL_CALLS - всего вызовов
                 MAX_TOTAL_TIME_MS - время самого долгого запроса ms
                 AVG_EXEC_TIME_MS - среднее время выполнения запроса ms
                 MAX_STDDEV_EXEC_TIME_MS - самое высокое отклонение ms 
                 AVG_CACHE_HIT - попадание в кэш %
                
                *Операторы для чисел:*
                 EQ - равно
                 NE - не равно  
                 GT - больше
                 LT - меньше
                 GTE - больше или равно
                 LTE - меньше или равно
                 
                *Операторы для текста:*
                 CONTAINS - содержит
                 NOT_CONTAINS - не содержит
                
                *Пример создания триггера:*
                 ```
                 Имя триггера: triggerName !!! Имя должно быть уникальным !!!
                 Сервис: serviceName или all для триггера по всем сервисам
                 Метрика: CPU_USAGE
                 Оператор: GT
                 Порог: 80
                 Cooldown: 5
                 ```
                
                *Формат команды создания:*
                 [create_{triggerName},{сервис},{метрика},{оператор},{порог},{cooldown}]
                
                *Пример:*
                 [/create_triggerName,serviceName,CPU_USAGE,GT,80,5]
                
                *Пример изменения триггера:*
                
                *Формат команды изменения:*
                 [edit_name:{triggerName},operator:{оператор},threshold:{порог},cooldown:{cooldown}]
                
                *Пример:*
                 [/edit_name:triggerName,operator:GT,threshold:90,cooldown:5]
                
                *Можно указывать конкретные поля, которые нужно изменить. Имя триггера, сервис, метрика, не изменяются
                *Имя указывается обязательно*
            </pre>
            </div>
            """
    }
}