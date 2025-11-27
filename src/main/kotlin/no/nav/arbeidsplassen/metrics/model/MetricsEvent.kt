package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import java.time.OffsetDateTime

data class MetricsEvent(
    @JsonProperty("event_id")
    val eventId: String,

    @JsonProperty("created_at")
    val createdAt: String,

    @JsonProperty("event_name")
    val eventName: String,

    @JsonProperty("event_data")
    val eventData: Map<String, Any>?
) {
    fun toBigQueryRow() = hashMapOf<String, Any?>(
        "event_id" to eventId,
        "created_at" to OffsetDateTime.parse(createdAt).toBigQueryDateTime(),
        "event_name" to eventName,
        "event_data" to eventData
    )
}

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: String? = null
)
