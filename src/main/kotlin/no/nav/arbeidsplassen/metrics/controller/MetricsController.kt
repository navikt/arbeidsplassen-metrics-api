package no.nav.arbeidsplassen.metrics.controller

import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import no.nav.arbeidsplassen.metrics.model.MetricsEventResponse
import no.nav.arbeidsplassen.metrics.model.SurveyType
import no.nav.arbeidsplassen.metrics.service.MetricsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/metrics")
class MetricsController(private val metricsService: MetricsService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(MetricsController::class.java)
        private const val ENRICHMENT_CREATED_EVENT_NAME = "Opprettet - Tilleggsdata"
    }

    @PostMapping("/event")
    fun receiveEvent(@RequestBody event: MetricsEvent): ResponseEntity<MetricsEventResponse> {
        return handleEventRequest(event, "metric") {
            metricsService.processEvent(event)
        }
    }

    @PostMapping("/enrichment-event")
    fun receiveEnrichmentEvent(@RequestBody event: MetricsEvent): ResponseEntity<MetricsEventResponse> {
        if (event.eventName != ENRICHMENT_CREATED_EVENT_NAME) {
            val message = "Invalid enrichment eventName '${event.eventName}'."
            LOG.warn(message)
            return ResponseEntity.badRequest().body(
                MetricsEventResponse(
                    success = false,
                    message = message,
                    eventId = event.eventId
                )
            )
        }

        return handleEventRequest(event, "enrichment") {
            metricsService.processEnrichmentEvent(event)
        }
    }

    @PostMapping("/survey-event")
    fun receiveSurveyEvent(@RequestBody event: MetricsEvent): ResponseEntity<MetricsEventResponse> {
        if (!SurveyType.isValid(event.eventName)) {
            val message = "Invalid survey eventName '${event.eventName}'."
            LOG.warn(message)
            return ResponseEntity.badRequest().body(
                MetricsEventResponse(
                    success = false,
                    message = message,
                    eventId = event.eventId
                )
            )
        }

        return handleEventRequest(event, "survey") {
            metricsService.processSurveyEvent(event)
        }
    }

    private fun handleEventRequest(
        event: MetricsEvent,
        eventType: String,
        action: () -> Unit
    ): ResponseEntity<MetricsEventResponse> {
        return try {
            action()
            val message = "Event type: $eventType - '${event.eventName}' with eventId ${event.eventId} received and queued for processing"
            LOG.info(message)

            ResponseEntity.ok(
                MetricsEventResponse(
                    success = true,
                    message = message,
                    eventId = event.eventId
                )
            )
        } catch (e: Exception) {
            val message = "Failed to process event type: $eventType - '${event.eventName}' with eventId ${event.eventId}: ${e.message}"
            LOG.warn(message)

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                MetricsEventResponse(
                    success = false,
                    message = message,
                    eventId = event.eventId
                )
            )
        }
    }
}
