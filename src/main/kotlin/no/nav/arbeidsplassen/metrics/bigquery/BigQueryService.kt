package no.nav.arbeidsplassen.metrics.bigquery

import com.google.cloud.bigquery.BigQueryException
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.InsertAllRequest
import com.google.cloud.bigquery.InsertAllResponse
import com.google.cloud.bigquery.StandardTableDefinition
import com.google.cloud.bigquery.TableId
import com.google.cloud.bigquery.TableInfo
import com.google.cloud.bigquery.TimePartitioning
import no.nav.arbeidsplassen.metrics.bigquery.MetricsTableDefinition.Companion.CREATED_AT
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Service
class BigQueryService(
    @param:Value("\${gcp.projectId}") private val projectId: String,
    @param:Value("\${gcp.datasetId}") private val datasetId: String,
) {
    private val logger = LoggerFactory.getLogger(BigQueryService::class.java)
    private val bigQuery = BigQueryOptions.newBuilder().setProjectId(projectId).build().service
    private val metricsTable = MetricsTableDefinition()

    companion object {
        private val bigQueryDatetimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        fun OffsetDateTime.toBigQueryDateTime(): String = format(bigQueryDatetimeFormatter)
    }

    init {
        try {
            createTableIfNotExists(metricsTable)
        } catch (e: Exception) {
            logger.error("Something failed when trying to fetch/create tables - $e")
            throw e
        }
    }

    private fun createTableIfNotExists(tableDefinition: TableDefinition) {
        try {
            val table = bigQuery.getTable(TableId.of(datasetId, tableDefinition.tableName))
            if (table != null && table.exists()) {
                logger.info("Table ${tableDefinition.tableName} already exists in project $projectId")
            } else {
                logger.info("Table ${tableDefinition.tableName} does not exist. Create table for $projectId")
                createTableWithPartition(tableDefinition)
            }
        } catch (e: BigQueryException) {
            logger.error("Table not found. \n$e")
        }

    }

    private fun createTableWithPartition(tableDefinition: TableDefinition) {
        try {
            val tableId = TableId.of(datasetId, tableDefinition.tableName)
            val partitioning = TimePartitioning.newBuilder(TimePartitioning.Type.MONTH).setField(CREATED_AT).build()
            val partitionedTableDefinition = StandardTableDefinition.newBuilder().setSchema(tableDefinition.schema).setTimePartitioning(partitioning).build()
            val tableInfo = TableInfo.newBuilder(tableId, partitionedTableDefinition).build()

            bigQuery.create(tableInfo)
            logger.info("Table ${tableDefinition.tableName} created successfully")
        } catch (e: BigQueryException) {
            logger.error("Table was not created. \n$e")
        }
    }

    fun tableInsertRow(tableName: String, rowContent: Map<String, Any?>) {
        try {
            val response: InsertAllResponse = bigQuery.insertAll(
                InsertAllRequest.newBuilder(TableId.of(datasetId, tableName))
                    .addRow(rowContent)
                    .build()
            )
            if (response.hasErrors()) {
                for (entry in response.insertErrors.entries) {
                    logger.error("Response error: \n${entry.value}")
                }
            } else {
                logger.info("Row successfully inserted into table")
            }
        } catch (e: BigQueryException) {
            logger.error("Insert operation not performed \n$e")
        }
    }

}
