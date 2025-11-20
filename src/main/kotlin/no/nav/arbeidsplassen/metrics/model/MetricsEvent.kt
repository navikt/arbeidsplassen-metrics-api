package no.nav.arbeidsplassen.metrics.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MetricsEvent(
    @JsonProperty("event_id")
    val eventId: String,
    
    @JsonProperty("website_id")
    val websiteId: String,
    
    @JsonProperty("session_id")
    val sessionId: String,
    
    @JsonProperty("created_at")
    val createdAt: String,
    
    @JsonProperty("url_path")
    val urlPath: String,
    
    @JsonProperty("url_query")
    val urlQuery: String?,
    
    @JsonProperty("referrer_path")
    val referrerPath: String?,
    
    @JsonProperty("referrer_query")
    val referrerQuery: String?,
    
    @JsonProperty("referrer_domain")
    val referrerDomain: String?,
    
    @JsonProperty("page_title")
    val pageTitle: String,
    
    @JsonProperty("event_type")
    val eventType: Int,
    
    @JsonProperty("event_name")
    val eventName: String,
    
    @JsonProperty("visit_id")
    val visitId: String,
    
    @JsonProperty("tag")
    val tag: String?,
    
    @JsonProperty("utm_source")
    val utmSource: String?,
    
    @JsonProperty("utm_content")
    val utmContent: String?,
    
    @JsonProperty("fbclid")
    val fbclid: String?,
    
    @JsonProperty("ttclid")
    val ttclid: String?,
    
    @JsonProperty("msclkid")
    val msclkid: String?,
    
    @JsonProperty("utm_campaign")
    val utmCampaign: String?,
    
    @JsonProperty("utm_medium")
    val utmMedium: String?,
    
    @JsonProperty("gclid")
    val gclid: String?,
    
    @JsonProperty("twclid")
    val twclid: String?,
    
    @JsonProperty("li_fat_id")
    val liFatId: String?,
    
    @JsonProperty("utm_term")
    val utmTerm: String?,
    
    @JsonProperty("hostname")
    val hostname: String,
    
    @JsonProperty("datastream_metadata")
    val datastreamMetadata: Map<String, Any>?
)

data class MetricsEventResponse(
    val success: Boolean,
    val message: String,
    val eventId: String? = null
)
