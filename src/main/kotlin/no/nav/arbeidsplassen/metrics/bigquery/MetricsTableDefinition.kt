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

@Component
class EnrichmentTableDefinition: TableDefinition {
    companion object {
        const val EVENT_ID = "event_id"
        const val CREATED_AT = "created_at"
        const val EVENT_NAME = "event_name"
        const val EVENT_DATA = "event_data"
        const val AD_ID = "ad_id"
        const val ENRICHMENT_TYPE = "enrichment_type"
        const val IS_APPLICABLE = "is_applicable"
    }

    override val tableName = "enrichment_events"
    override val schema: Schema = Schema.of(
        Field.of(EVENT_ID, StandardSQLTypeName.STRING),
        Field.of(CREATED_AT, StandardSQLTypeName.DATETIME),
        Field.of(EVENT_NAME, StandardSQLTypeName.STRING),
        Field.of(EVENT_DATA, StandardSQLTypeName.JSON),
        Field.newBuilder(AD_ID, StandardSQLTypeName.STRING).setMode(Field.Mode.NULLABLE).build(),
        Field.newBuilder(ENRICHMENT_TYPE, StandardSQLTypeName.STRING).setMode(Field.Mode.NULLABLE).build(),
        Field.newBuilder(IS_APPLICABLE, StandardSQLTypeName.BOOL).setMode(Field.Mode.NULLABLE).build()
    )
}

@Component
class SurveyTableDefinition: TableDefinition {
    companion object {
        const val EVENT_ID = "event_id"
        const val CREATED_AT = "created_at"
        const val EVENT_NAME = "event_name" // survey type
        const val EVENT_DATA = "event_data"
        const val SURVEY_ID = "survey_id"
        const val AD_ID = "ad_id"
        const val METADATA = "metadata"
        const val ANSWER = "answer"
        const val ANSWERED_AT = "answered_at"
    }

    override val tableName = "survey_events"
    override val schema: Schema = Schema.of(
        Field.of(EVENT_ID, StandardSQLTypeName.STRING),
        Field.of(CREATED_AT, StandardSQLTypeName.DATETIME),
        Field.of(EVENT_NAME, StandardSQLTypeName.STRING),
        Field.of(EVENT_DATA, StandardSQLTypeName.JSON),
        Field.of(SURVEY_ID, StandardSQLTypeName.STRING),
        Field.of(AD_ID, StandardSQLTypeName.STRING),
        Field.of(METADATA, StandardSQLTypeName.JSON),
        Field.of(ANSWER, StandardSQLTypeName.JSON),
        Field.of(ANSWERED_AT, StandardSQLTypeName.DATETIME),
    )
}
