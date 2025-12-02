package no.nav.arbeidsplassen.metrics.model

import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.CREATED_AT
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_DATA
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_ID
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_NAME
import java.time.OffsetDateTime

data class MetricsEvent(
    val eventId: String,
    val createdAt: String,
    val eventName: String,
    val eventData: Map<String, Any>?
) {
    fun toBigQueryRow() = hashMapOf<String, Any?>(
        EVENT_ID to eventId,
        CREATED_AT to OffsetDateTime.parse(createdAt).toBigQueryDateTime(),
        EVENT_NAME to eventName,
        EVENT_DATA to eventData
    )
}

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: String? = null
)
