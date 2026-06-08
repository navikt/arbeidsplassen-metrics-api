package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.AD_ID
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.ENRICHMENT_TYPE
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.IS_APPLICABLE
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.CREATED_AT
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_DATA
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_ID
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_NAME
import java.time.OffsetDateTime
import java.util.UUID

data class MetricsEvent(
    val eventId: UUID,
    val createdAt: OffsetDateTime,
    val eventName: String,
    val eventData: Map<String, Any>?
) {
    fun toBigQueryRow(): HashMap<String, Any?> {
        val objectMapper = jacksonObjectMapper()
        val eventDataJson = objectMapper.writeValueAsString(eventData)
        return hashMapOf(
            EVENT_ID to eventId.toString(),
            CREATED_AT to createdAt.toBigQueryDateTime(),
            EVENT_NAME to eventName,
            EVENT_DATA to eventDataJson
        )
    }

    fun toEnrichmentBigQueryRow(): HashMap<String, Any?> {
        return toBigQueryRow().also { row ->
            row[AD_ID] = eventData?.get("adId")?.toString()
            val enrichmentType = eventData?.get("enrichmentType")?.toString()
            row[ENRICHMENT_TYPE] = enrichmentType
            row[IS_APPLICABLE] = when (enrichmentType) {
                "UNDER_18" -> eventData["isUnder18"] as? Boolean
                "SUMMER_JOB" -> eventData["isSummerJob"] as? Boolean
                else -> null
            }
        }
    }
}

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: UUID? = null
)
