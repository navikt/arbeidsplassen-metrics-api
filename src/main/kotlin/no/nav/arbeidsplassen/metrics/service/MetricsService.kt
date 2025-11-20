package no.nav.arbeidsplassen.metrics.service

import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.InsertAllRequest
import com.google.cloud.bigquery.TableId
import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MetricsService(
    @Value("\${gcp.projectId}") private val projectId: String,
) {
    private val logger = LoggerFactory.getLogger(MetricsService::class.java)
    private val bigQuery = BigQueryOptions.newBuilder().setProjectId(projectId).build().service

    fun processEvent(event: MetricsEvent) {
        // TODO: Implement the logic to process and store the event in BigQuery
    }
}
