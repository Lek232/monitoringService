package com.test.monitoringService.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class GetHealthDto {

    @JsonIgnoreProperties
    data class Health(
        val status: String = "UNKNOWN",
        val components: Components? = null,
    )

    data class Components(
        val db: Db? = null,
    )

    data class Db(
        val status: String = "UNKNOWN",
        val details: Details? = null,
    )

    data class Details(
        val database: String = "UNKNOWN",
    )
}