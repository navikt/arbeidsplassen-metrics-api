package no.nav.arbeidsplassen.metrics.bigquery

import com.google.cloud.bigquery.BigQueryException
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.InsertAllRequest
import com.google.cloud.bigquery.InsertAllResponse
import com.google.cloud.bigquery.Schema
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
import java.time.temporal.ChronoUnit

@Service
class BigQueryService(
    @param:Value("\${gcp.projectId}") private val projectId: String,
    @param:Value("\${gcp.datasetId}") private val datasetId: String,
) {
    private val LOG = LoggerFactory.getLogger(BigQueryService::class.java)
    private val bigQuery = BigQueryOptions.newBuilder().setProjectId(projectId).build().service
    private val metricsTable = MetricsTableDefinition()
    private val enrichmentTable = EnrichmentTableDefinition()

    companion object {
        private val bigQueryDatetimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        fun OffsetDateTime.toBigQueryDateTime(): String = truncatedTo(ChronoUnit.MICROS).format(bigQueryDatetimeFormatter)
    }

    init {
        try {
            createTableIfNotExists(metricsTable)
        } catch (e: Exception) {
            LOG.error("Something failed when trying to fetch/create metrics table - $e")
            throw e
        }

        try {
            createTableIfNotExists(enrichmentTable)
        } catch (e: Exception) {
            LOG.error("Something failed when trying to fetch/create enrichment table - $e")
            throw e
        }
    }

    private fun createTableIfNotExists(tableDefinition: TableDefinition) {
        try {
            val table = bigQuery.getTable(TableId.of(datasetId, tableDefinition.tableName))
            if (table != null && table.exists()) {
                LOG.info("Table ${tableDefinition.tableName} already exists in project $projectId")
                updateSchemaIfNeeded(tableDefinition, table)
            } else {
                LOG.info("Table ${tableDefinition.tableName} does not exist. Create table for $projectId")
                createTableWithPartition(tableDefinition)
            }
        } catch (e: BigQueryException) {
            LOG.error("Table not found. \n$e")
        }

    }

    private fun updateSchemaIfNeeded(tableDefinition: TableDefinition, table: com.google.cloud.bigquery.Table) {
        val existingFieldNames = table.getDefinition<StandardTableDefinition>().schema?.fields
            ?.map { it.name }?.toSet() ?: emptySet()
        val newFields = tableDefinition.schema.fields.filter { it.name !in existingFieldNames }

        if (newFields.isEmpty()) {
            LOG.info("Schema for ${tableDefinition.tableName} is up to date")
            return
        }

        LOG.info("Adding ${newFields.size} new field(s) to ${tableDefinition.tableName}: ${newFields.map { it.name }}")
        val updatedFields = (table.getDefinition<StandardTableDefinition>().schema?.fields?.toList() ?: emptyList()) + newFields
        val updatedSchema = Schema.of(updatedFields)
        val updatedTableInfo = table.toBuilder()
            .setDefinition(StandardTableDefinition.newBuilder().setSchema(updatedSchema).build())
            .build()
        bigQuery.update(updatedTableInfo)
        LOG.info("Schema for ${tableDefinition.tableName} updated successfully")
    }

    private fun createTableWithPartition(tableDefinition: TableDefinition) {
        try {
            val tableId = TableId.of(datasetId, tableDefinition.tableName)
            val partitioning = TimePartitioning.newBuilder(TimePartitioning.Type.MONTH).setField(CREATED_AT).build()
            val partitionedTableDefinition = StandardTableDefinition.newBuilder().setSchema(tableDefinition.schema).setTimePartitioning(partitioning).build()
            val tableInfo = TableInfo.newBuilder(tableId, partitionedTableDefinition).build()

            bigQuery.create(tableInfo)
            LOG.info("Table ${tableDefinition.tableName} created successfully")
        } catch (e: BigQueryException) {
            LOG.error("Table was not created. \n$e")
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
                    LOG.error("Response error: \n${entry.value}")
                }
            } else {
                LOG.info("Row successfully inserted into table")
            }
        } catch (e: BigQueryException) {
            LOG.error("Insert operation not performed \n$e")
        }
    }

}
