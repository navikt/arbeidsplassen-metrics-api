package no.nav.arbeidsplassen.metrics.bigquery

import com.google.cloud.bigquery.Field
import com.google.cloud.bigquery.Schema
import com.google.cloud.bigquery.StandardSQLTypeName
import org.springframework.stereotype.Component

@Component
class MetricsTableDefinition: TableDefinition {
    override val tableName = "metrics_events"
    override val schema: Schema = Schema.of(
        Field.of("event_id", StandardSQLTypeName.STRING),
        Field.of("created_at", StandardSQLTypeName.DATETIME),
        Field.of("event_name", StandardSQLTypeName.STRING),
        Field.of("event_data", StandardSQLTypeName.JSON)
        )
}
