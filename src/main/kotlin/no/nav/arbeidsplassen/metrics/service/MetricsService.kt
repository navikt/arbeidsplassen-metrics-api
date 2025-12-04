package no.nav.arbeidsplassen.metrics.service

import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition
import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MetricsService(private val bigQueryService: BigQueryService) {
    private val logger = LoggerFactory.getLogger(MetricsService::class.java)
    private val metricsTable = MetricsTableDefinition()

    fun processEvent(event: MetricsEvent) {
        val biqQueryRow = event.toBigQueryRow()
        logger.info("Try to insert event into BigQuery")
        bigQueryService.tableInsertRow(metricsTable.tableName, biqQueryRow)
    }
}
