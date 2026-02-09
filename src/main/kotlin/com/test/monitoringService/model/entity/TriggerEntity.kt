package com.test.monitoringService.model.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
@Table(name = "triggers", indexes = [
    Index(name = "idx_triggers_name", columnList = "name", unique = true),

    Index(name = "idx_triggers_service_name", columnList = "service_name"),

    Index(name = "idx_triggers_serviceName_enabled", columnList = "service_name, enabled"),

    Index(name = "idx_triggers_enabled", columnList = "enabled"),
])

class TriggerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @Column(name = "name", nullable = false, length = 100, unique = true)
    var name: String = "",

    @Column(name = "service_name", nullable = false, length = 100)
    var serviceName: String = "all",

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false, length = 30)
    var metric: TriggerField = TriggerField.HEALTH_STATUS,

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 20)
    var operator: TriggerOperator = TriggerOperator.CONTAINS,

    @Column(name = "threshold", nullable = false)
    var threshold: String = "",

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @Column(name = "cooldown_minutes")
    var cooldownMinutes: Int = 5,

    @Column(name = "last_triggered", columnDefinition = "TIMESTAMPTZ DEFAULT (NOW() AT TIME ZONE 'UTC')")
    var lastTriggered: OffsetDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ DEFAULT (NOW() AT TIME ZONE 'UTC')")
    val createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ DEFAULT (NOW() AT TIME ZONE 'UTC')")
    var updatedAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
)

@Schema(description = "Доступные метрики")
enum class TriggerField {
    @Schema(description = "Состояние сервиса")
    HEALTH_STATUS(FieldType.TEXT, FieldBelongsDb.FALSE),
    @Schema(description = "Доступность сервиса")
    AVAILABILITY(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    @Schema(description = "Загрузка памяти сервиса")
    MEMORY_LOAD(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    @Schema(description = "Использование СPU")
    CPU_USAGE(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    @Schema(description = "Потоки")
    THREADS_LIVE(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    @Schema(description = "Рост потребления")
    CONSUMPTION_DIFFERENCE(FieldType.NUMERIC, FieldBelongsDb.FALSE),

    @Schema(description = "Состояние БД")
    DATABASE_STATUS(FieldType.TEXT, FieldBelongsDb.TRUE),
    @Schema(description = "Нагрузка на БД")
    DATABASE_LOAD(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    @Schema(description = "Всего запросов SQL")
    TOTAL_QUERIES(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    @Schema(description = "Всего вызовов запросов SQL")
    TOTAL_CALLS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    @Schema(description = "Максимальное время выполнения запроса")
    MAX_TOTAL_TIME_MS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    @Schema(description = "Среднее время выполнения запроса")
    AVG_EXEC_TIME_MS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    @Schema(description = "Максимальное отклонение от среднего времени выполнения запроса")
    MAX_STDDEV_EXEC_TIME_MS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    @Schema(description = "Среднее попадание в кэш")
    AVG_CACHE_HIT(FieldType.NUMERIC, FieldBelongsDb.TRUE);

    enum class FieldBelongsDb {
        TRUE,
        FALSE
    }

    val type: FieldType
    val belongs: FieldBelongsDb

    constructor(type: FieldType, belongs: FieldBelongsDb) {
        this.type = type
        this.belongs = belongs
    }
}

enum class FieldType {
    NUMERIC,
    TEXT
}

@Schema(description = "Операторы сравнения")
enum class TriggerOperator {
    @Schema(description = "Равно (числовой)")
    EQ(FieldType.NUMERIC),
    @Schema(description = "Не равно (числовой)")
    NE(FieldType.NUMERIC),
    @Schema(description = "Больше (числовой)")
    GT(FieldType.NUMERIC),
    @Schema(description = "Меньше (числовой)")
    LT(FieldType.NUMERIC),
    @Schema(description = "Больше или равно (числовой)")
    GTE(FieldType.NUMERIC),
    @Schema(description = "Меньше или равно (числовой)")
    LTE(FieldType.NUMERIC),
    @Schema(description = "Содержит (текстовый)")
    CONTAINS(FieldType.TEXT),
    @Schema(description = "Не содержит (текстовый)")
    NOT_CONTAINS(FieldType.TEXT);

    val type: FieldType

    constructor(type: FieldType) {
        this.type = type
    }
}
