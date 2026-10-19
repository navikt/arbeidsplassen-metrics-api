package no.nav.arbeidsplassen.metrics.service

import no.nav.arbeidsplassen.metrics.bigquery.BigQueryService
import no.nav.arbeidsplassen.metrics.bigquery.EnrichmentTableDefinition
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition
import no.nav.arbeidsplassen.metrics.bigquery.SurveyTableDefinition
import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MetricsService(private val bigQueryService: BigQueryService) {
    private val logger = LoggerFactory.getLogger(MetricsService::class.java)
    private val metricsTable = MetricsTableDefinition()
    private val enrichmentTable = EnrichmentTableDefinition()
    private val surveyTable = SurveyTableDefinition()

    fun processEvent(event: MetricsEvent) {
        val biqQueryRow = event.toBigQueryRow()
        logger.info("Try to insert event into BigQuery")
        bigQueryService.tableInsertRow(metricsTable.tableName, biqQueryRow)
    }

    fun processEnrichmentEvent(event: MetricsEvent) {
        val biqQueryRow = event.toEnrichmentBigQueryRow()
        logger.info("Try to insert enrichment event into BigQuery")
        bigQueryService.tableInsertRow(enrichmentTable.tableName, biqQueryRow)
    }

    fun processSurveyEvent(event: MetricsEvent) {
        val biqQueryRow = event.toSurveyBigQueryRow()
        logger.info("Try to insert survey event into BigQuery")
        bigQueryService.tableInsertRow(surveyTable.tableName, biqQueryRow)
    }
}
