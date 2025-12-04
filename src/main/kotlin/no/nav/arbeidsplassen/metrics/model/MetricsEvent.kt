package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import java.time.OffsetDateTime
import java.util.UUID

data class MetricsEvent(
    val eventId: UUID,
    val createdAt: OffsetDateTime,
    val eventName: String,
    val eventData: Map<String, Any>?
) {
    fun toBigQueryRow() = hashMapOf<String, Any?>(
        "event_id" to eventId,
        "created_at" to createdAt.toBigQueryDateTime(),
        "event_name" to eventName,
        "event_data" to eventData
    )
}

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: UUID? = null
)
