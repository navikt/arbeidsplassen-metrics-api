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
        Field.of("website_id", StandardSQLTypeName.STRING),
        Field.of("session_id", StandardSQLTypeName.STRING),
        Field.of("created_at", StandardSQLTypeName.DATETIME),
        Field.of("url_path", StandardSQLTypeName.STRING),
        Field.of("url_query", StandardSQLTypeName.STRING),
        Field.of("referrer_path", StandardSQLTypeName.STRING),
        Field.of("referrer_query", StandardSQLTypeName.STRING),
        Field.of("referrer_domain", StandardSQLTypeName.STRING),
        Field.of("page_title", StandardSQLTypeName.STRING),
        Field.of("event_type", StandardSQLTypeName.INT64),
        Field.of("event_name", StandardSQLTypeName.STRING),
        Field.of("visit_id", StandardSQLTypeName.STRING),
        Field.of("tag", StandardSQLTypeName.STRING),
        Field.of("utm_source", StandardSQLTypeName.STRING),
        Field.of("utm_content", StandardSQLTypeName.STRING),
        Field.of("fbclid", StandardSQLTypeName.STRING),
        Field.of("ttclid", StandardSQLTypeName.STRING),
        Field.of("msclkid", StandardSQLTypeName.STRING),
        Field.of("utm_campaign", StandardSQLTypeName.STRING),
        Field.of("utm_medium", StandardSQLTypeName.STRING),
        Field.of("gclid", StandardSQLTypeName.STRING),
        Field.of("twclid", StandardSQLTypeName.STRING),
        Field.of("li_fat_id", StandardSQLTypeName.STRING),
        Field.of("utm_term", StandardSQLTypeName.STRING),
        Field.of("hostname", StandardSQLTypeName.STRING),
        Field.of("datastream_metadata", StandardSQLTypeName.JSON) //TODO: type

        )
}
