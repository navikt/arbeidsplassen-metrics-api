package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.CREATED_AT
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_DATA
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_ID
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_NAME
import no.nav.arbeidsplassen.metrics.bigquery.SurveyTableDefinition
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.UUID

data class MetricsEvent(
    val eventId: UUID,
    val createdAt: OffsetDateTime,
    val eventName: String,
    val eventData: Map<String, Any>?
) {
    val objectMapper = jacksonObjectMapper()

    fun toBigQueryRow(): HashMap<String, Any?> {
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
            row[EnrichmentTableDefinition.AD_ID] = eventData?.get("adId")?.toString()
            val enrichmentType = eventData?.get("enrichmentType")?.toString()
            row[EnrichmentTableDefinition.ENRICHMENT_TYPE] = enrichmentType
            row[EnrichmentTableDefinition.IS_APPLICABLE] = when (enrichmentType) {
                "UNDER_18" -> eventData["isUnder18"] as? Boolean
                "SUMMER_JOB" -> eventData["isSummerJob"] as? Boolean
                else -> null
            }
        }
    }

    fun toSurveyBigQueryRow(): HashMap<String, Any?> {
        val answeredAtToBQFormat = try {
            eventData?.get("answeredAt")
                ?.toString()
                ?.let(OffsetDateTime::parse)
                ?.toBigQueryDateTime()
        } catch (e: DateTimeParseException) { throw e }
        return toBigQueryRow().also { row ->
            row[SurveyTableDefinition.SURVEY_ID] = eventData?.get("surveyId")?.toString()
            row[SurveyTableDefinition.AD_ID] = eventData?.get("adId")?.toString()
            row[SurveyTableDefinition.METADATA] = eventData?.get("metadata")?.let { objectMapper.writeValueAsString(it) }
            row[SurveyTableDefinition.ANSWER] = eventData?.get("answer")?.let { objectMapper.writeValueAsString(it) }
            row[SurveyTableDefinition.ANSWERED_AT] = answeredAtToBQFormat
        }
    }
}

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: UUID? = null
)
