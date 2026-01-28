package com.test.monitoringService.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class GetMetricDto {

    @JsonIgnoreProperties
    data class Metric(
        val measurements: List<Measurements?> = emptyList(),
    )

    data class Measurements(
        val statistic: String = "UNKNOWN",
        val value: Double = 0.0,
    )
}