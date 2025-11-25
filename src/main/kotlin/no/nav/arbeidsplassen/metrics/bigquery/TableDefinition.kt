package no.nav.arbeidsplassen.metrics.bigquery

import com.google.cloud.bigquery.Schema

interface TableDefinition {
    val tableName: String
    val schema: Schema
}
