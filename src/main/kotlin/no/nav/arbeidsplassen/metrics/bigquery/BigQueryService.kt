package no.nav.arbeidsplassen.metrics.bigquery

import com.google.cloud.bigquery.BigQueryException
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.StandardTableDefinition
import com.google.cloud.bigquery.TableId
import com.google.cloud.bigquery.TableInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class BigQueryService(
    @param:Value("\${gcp.projectId}") private val projectId: String,
    @param:Value("\${gcp.datasetId}") private val datasetId: String,
) {
    private val logger = LoggerFactory.getLogger(BigQueryService::class.java)
    private val bigQuery = BigQueryOptions.newBuilder().setProjectId(projectId).build().service

    fun createTable(tableDefinition: TableDefinition) {
        try {
            val tableId = TableId.of(datasetId, tableDefinition.tableName)
            val tableDefinition = StandardTableDefinition.of(tableDefinition.schema)
            val tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build()

            bigQuery.create(tableInfo)
            logger.info("Table created successfully")
        } catch (e: BigQueryException) {
            logger.error("Table was not created. \n$e")
        }
    }
}
