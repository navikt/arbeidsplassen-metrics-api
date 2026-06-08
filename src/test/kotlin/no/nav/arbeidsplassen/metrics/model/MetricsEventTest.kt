package no.nav.arbeidsplassen.metrics.model

import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.AD_ID
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition.Companion.ENRICHMENT_TYPE
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.EVENT_DATA
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class MetricsEventTest {

    private val sampleEventData = mapOf(
        "adId" to "975c898a-3171-460b-b888-6f37cb28019a",
        "adVersion" to 1492537,
        "enrichmentId" to "056b1609-d533-46fb-84f8-d982d48c8be1",
        "enrichmentType" to "UNDER_18",
        "isUnder18" to false,
        "isUnder18Reason" to "Some reason"
    )

    private val event = MetricsEvent(
        eventId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        createdAt = OffsetDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.UTC),
        eventName = "Opprettet - Tilleggsdata",
        eventData = sampleEventData
    )

    @Test
    fun `toEnrichmentBigQueryRow extracts adId from eventData`() {
        val row = event.toEnrichmentBigQueryRow()
        assertEquals("975c898a-3171-460b-b888-6f37cb28019a", row[AD_ID])
    }

    @Test
    fun `toEnrichmentBigQueryRow extracts enrichmentType from eventData`() {
        val row = event.toEnrichmentBigQueryRow()
        assertEquals("UNDER_18", row[ENRICHMENT_TYPE])
    }

    @Test
    fun `toEnrichmentBigQueryRow still includes full eventData as JSON`() {
        val row = event.toEnrichmentBigQueryRow()
        val eventDataJson = row[EVENT_DATA] as String
        assert(eventDataJson.contains("adId"))
        assert(eventDataJson.contains("enrichmentType"))
    }

    @Test
    fun `toEnrichmentBigQueryRow handles null eventData gracefully`() {
        val eventWithNoData = event.copy(eventData = null)
        val row = eventWithNoData.toEnrichmentBigQueryRow()
        assertNull(row[AD_ID])
        assertNull(row[ENRICHMENT_TYPE])
    }

    @Test
    fun `toEnrichmentBigQueryRow handles missing adId and enrichmentType in eventData`() {
        val eventWithPartialData = event.copy(eventData = mapOf("adVersion" to 123))
        val row = eventWithPartialData.toEnrichmentBigQueryRow()
        assertNull(row[AD_ID])
        assertNull(row[ENRICHMENT_TYPE])
    }
}

