package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.AD_ID
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.ENRICHMENT_TYPE
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.IS_APPLICABLE
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.CREATED_AT
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_DATA
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_ID
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_NAME
import no.nav.arbeidsplassen.metrics.bigquery.SurveyTableDefinition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException
import java.util.UUID

class MetricsEventTest {

    private val sampleEnrichmentEventData = mapOf(
        "adId" to "975c898a-3171-460b-b888-6f37cb28019a",
        "adVersion" to 1492537,
        "enrichmentId" to "056b1609-d533-46fb-84f8-d982d48c8be1",
        "enrichmentType" to "UNDER_18",
        "isUnder18" to false,
        "isUnder18Reason" to "Some reason"
    )

    private val enrichmentEvent = MetricsEvent(
        eventId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        createdAt = OffsetDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC),
        eventName = "Opprettet - Tilleggsdata",
        eventData = sampleEnrichmentEventData
    )

    @Test
    fun `toEnrichmentBigQueryRow extracts adId from eventData`() {
        val row = enrichmentEvent.toEnrichmentBigQueryRow()
        assertEquals("975c898a-3171-460b-b888-6f37cb28019a", row[AD_ID])
    }

    @Test
    fun `toEnrichmentBigQueryRow extracts enrichmentType from eventData`() {
        val row = enrichmentEvent.toEnrichmentBigQueryRow()
        assertEquals("UNDER_18", row[ENRICHMENT_TYPE])
    }

    @Test
    fun `toEnrichmentBigQueryRow extracts is_applicable for UNDER_18`() {
        val row = enrichmentEvent.toEnrichmentBigQueryRow()
        assertEquals(false, row[IS_APPLICABLE])
    }

    @Test
    fun `toEnrichmentBigQueryRow extracts is_applicable for SUMMER_JOB`() {
        val summerJobEvent = enrichmentEvent.copy(eventData = mapOf(
            "adId" to "abc123",
            "enrichmentType" to "SUMMER_JOB",
            "isSummerJob" to true
        ))
        val row = summerJobEvent.toEnrichmentBigQueryRow()
        assertEquals(true, row[IS_APPLICABLE])
    }

    @Test
    fun `toEnrichmentBigQueryRow sets is_applicable to null for unknown enrichment type`() {
        val unknownTypeEvent = enrichmentEvent.copy(eventData = mapOf(
            "adId" to "abc123",
            "enrichmentType" to "UNKNOWN_TYPE",
            "someOtherBool" to true
        ))
        val row = unknownTypeEvent.toEnrichmentBigQueryRow()
        assertNull(row[IS_APPLICABLE])
    }

    @Test
    fun `toEnrichmentBigQueryRow still includes full eventData as JSON`() {
        val row = enrichmentEvent.toEnrichmentBigQueryRow()
        val eventDataJson = row[EVENT_DATA] as String
        assert(eventDataJson.contains("adId"))
        assert(eventDataJson.contains("enrichmentType"))
    }

    @Test
    fun `toEnrichmentBigQueryRow handles null eventData gracefully`() {
        val eventWithNoData = enrichmentEvent.copy(eventData = null)
        val row = eventWithNoData.toEnrichmentBigQueryRow()
        assertNull(row[AD_ID])
        assertNull(row[ENRICHMENT_TYPE])
        assertNull(row[IS_APPLICABLE])
    }

    @Test
    fun `toEnrichmentBigQueryRow handles missing adId and enrichmentType in eventData`() {
        val eventWithPartialData = enrichmentEvent.copy(eventData = mapOf("adVersion" to 123))
        val row = eventWithPartialData.toEnrichmentBigQueryRow()
        assertNull(row[AD_ID])
        assertNull(row[ENRICHMENT_TYPE])
        assertNull(row[IS_APPLICABLE])
    }

    private val surveyEventData = mapOf(
        "surveyId" to "survey-123",
        "adId" to "ad-123",
        "metadata" to mapOf("hasSuperraskSoknad" to true, "hasApplicationEmail" to false, "hasApplicationUrl" to false, "positionCount" to 2),
        "answer" to mapOf("question 1" to "Bra", "question 2" to "Kjempe bra!"),
        "answeredAt" to "2024-06-01T12:30:45Z"
    )

    private val surveyEvent = MetricsEvent(
        eventId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        createdAt = OffsetDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC),
        eventName = "Survey Answered",
        eventData = surveyEventData
    )

    @Test
    fun `toSurveyBigQueryRow extracts surveyId and adId`() {
        val row = surveyEvent.toSurveyBigQueryRow()
        assertEquals("survey-123", row[SurveyTableDefinition.SURVEY_ID])
        assertEquals("ad-123", row[SurveyTableDefinition.AD_ID])
    }

    private val jsonMapper = jacksonObjectMapper()

    @Test
    fun `toSurveyBigQueryRow serializes metadata and answer as JSON`() {
        val row = surveyEvent.toSurveyBigQueryRow()
        assertEquals(
            jsonMapper.readTree("""{"hasSuperraskSoknad":true,"hasApplicationEmail":false,"hasApplicationUrl":false,"positionCount":2}"""),
            jsonMapper.readTree(row[SurveyTableDefinition.METADATA] as String)
        )
        assertEquals(
            jsonMapper.readTree("""{"question 1":"Bra","question 2":"Kjempe bra!"}"""),
            jsonMapper.readTree(row[SurveyTableDefinition.ANSWER] as String)
        )
    }

    @Test
    fun `toSurveyBigQueryRow converts answeredAt to BigQuery datetime`() {
        val row = surveyEvent.toSurveyBigQueryRow()
        assertEquals("2024-06-01T12:30:45", row[SurveyTableDefinition.ANSWERED_AT])
    }


    @Test
    fun `toSurveyBigQueryRow includes base metrics fields and full eventData`() {
        val row = surveyEvent.toSurveyBigQueryRow()
        assertEquals(surveyEvent.eventId.toString(), row[EVENT_ID])
        assertEquals("Survey Answered", row[EVENT_NAME])
        assertEquals("2024-06-01T12:00:00", row[CREATED_AT])
        val eventDataJson = row[EVENT_DATA] as String
        assert(eventDataJson.contains("surveyId"))
        assert(eventDataJson.contains("adId"))
        assert(eventDataJson.contains("metadata"))
        assert(eventDataJson.contains("answer"))
        assert(eventDataJson.contains("answeredAt"))
    }

    @Test
    fun `toSurveyBigQueryRow handles null eventData gracefully`() {
        val bigQueryRow = surveyEvent.copy(eventData = null).toSurveyBigQueryRow()
        assertNull(bigQueryRow[SurveyTableDefinition.SURVEY_ID])
        assertNull(bigQueryRow[SurveyTableDefinition.AD_ID])
        assertNull(bigQueryRow[SurveyTableDefinition.METADATA])
        assertNull(bigQueryRow[SurveyTableDefinition.ANSWER])
        assertNull(bigQueryRow[SurveyTableDefinition.ANSWERED_AT])
    }

    @Test
    fun `toSurveyBigQueryRow throws when answeredAt is not a valid datetime`() {
        val invalidEvent = surveyEvent.copy(
            eventData = surveyEventData + ("answeredAt" to "not-a-date")
        )
        assertThrows(DateTimeParseException::class.java) { invalidEvent.toSurveyBigQueryRow() }
    }
}

