package no.nav.arbeidsplassen.metrics.bigquery

import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService.Companion.toBigQueryDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class BigQueryServiceTest {
    @Test
    fun `OffsetDateTime toBigQueryDateTime formats correctly`() {
        val dateTime = OffsetDateTime.of(2024, 6, 1, 12, 34, 56, 123456000, ZoneOffset.UTC)
        val formatted = dateTime.toBigQueryDateTime()
        //docs.cloud.google.com/bigquery/docs/reference/standard-sql/data-types?hl=en#datetime_type "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        assertEquals("2024-06-01T12:34:56.123456", formatted)
    }
}

