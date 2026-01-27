package com.test.monitoringService.model.entity

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

enum class TriggerField {
    HEALTH_STATUS(FieldType.TEXT, FieldBelongsDb.FALSE),
    AVAILABILITY(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    MEMORY_LOAD(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    CPU_USAGE(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    THREADS_LIVE(FieldType.NUMERIC, FieldBelongsDb.FALSE),
    CONSUMPTION_DIFFERENCE(FieldType.NUMERIC, FieldBelongsDb.FALSE),

    DATABASE_STATUS(FieldType.TEXT, FieldBelongsDb.TRUE),
    DATABASE_LOAD(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    TOTAL_QUERIES(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    TOTAL_CALLS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    MAX_TOTAL_TIME_MS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    AVG_EXEC_TIME_MS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
    MAX_STDDEV_EXEC_TIME_MS(FieldType.NUMERIC, FieldBelongsDb.TRUE),
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

enum class TriggerOperator {
    EQ(FieldType.NUMERIC),
    NE(FieldType.NUMERIC),
    GT(FieldType.NUMERIC),
    LT(FieldType.NUMERIC),
    GTE(FieldType.NUMERIC),
    LTE(FieldType.NUMERIC),
    CONTAINS(FieldType.TEXT),
    NOT_CONTAINS(FieldType.TEXT);

    val type: FieldType

    constructor(type: FieldType) {
        this.type = type
    }
}
