package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MetricsEvent(
    @JsonProperty("event_id")
    val eventId: String,

    @JsonProperty("created_at")
    val createdAt: String,

    @JsonProperty("event_name")
    val eventName: String,

    @JsonProperty("event_data")
    val eventData: Map<String, Any>?
)

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: String? = null
)
