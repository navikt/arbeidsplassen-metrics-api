package no.nav.arbeidsplassen.metrics.bigquery

import com.google.cloud.bigquery.Field
import com.google.cloud.bigquery.Schema
import com.google.cloud.bigquery.StandardSQLTypeName
import org.springframework.stereotype.Component

@Component
class MetricsTableDefinition: TableDefinition {
    companion object {
        const val EVENT_ID = "event_id"
        const val CREATED_AT = "created_at"
        const val EVENT_NAME = "event_name"
        const val EVENT_DATA = "event_data"
    }
    override val tableName = "metrics_events"
    override val schema: Schema = Schema.of(
        Field.of(EVENT_ID, StandardSQLTypeName.STRING),
        Field.of(CREATED_AT, StandardSQLTypeName.DATETIME),
        Field.of(EVENT_NAME, StandardSQLTypeName.STRING),
        Field.of(EVENT_DATA, StandardSQLTypeName.JSON)
        )
}
