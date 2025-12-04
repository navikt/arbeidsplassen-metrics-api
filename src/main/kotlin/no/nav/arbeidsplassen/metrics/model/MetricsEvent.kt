package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.annotation.JsonProperty
//import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import java.time.OffsetDateTime
import java.util.UUID

data class MetricsEvent(
    @param:JsonProperty("event_id")
    val eventId: UUID,

    @param:JsonProperty("created_at")
    val createdAt: OffsetDateTime,

    @param:JsonProperty("event_name")
    val eventName: String,

    @param:JsonProperty("event_data")
    val eventData: Map<String, Any>?
) {
    fun toBigQueryRow() = hashMapOf<String, Any?>(
        "event_id" to eventId,
        //"created_at" to createdAt,
        "event_name" to eventName,
        "event_data" to eventData
    )
}

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: UUID? = null
)
