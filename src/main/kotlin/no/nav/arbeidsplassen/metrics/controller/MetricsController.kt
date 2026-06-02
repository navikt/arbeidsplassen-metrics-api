package no.nav.arbeidsplassen.metrics.controller

import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import no.nav.arbeidsplassen.metrics.model.MetricsEventResponse
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
        return try {
            metricsService.processEvent(event)
            LOG.info("${event.eventName} event with eventId ${event.eventId} received and queued for processing")
            ResponseEntity.ok(
                MetricsEventResponse(
                    success = true,
                    message = "${event.eventName} event with eventId ${event.eventId} received and queued for processing",
                    eventId = event.eventId
                )
            )
        } catch (e: Exception) {
            LOG.warn("Failed to process ${event.eventName} event with eventId ${event.eventId}: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                MetricsEventResponse(
                    success = false,
                    message = "Failed to process ${event.eventName} event with eventId ${event.eventId}: ${e.message}",
                    eventId = event.eventId
                )
            )
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

        return try {
            metricsService.processEvent(event)
            LOG.info("'${event.eventName}' enrichment event with eventId ${event.eventId} received and queued for processing")
            ResponseEntity.ok(
                MetricsEventResponse(
                    success = true,
                    message = "${event.eventName} event with eventId ${event.eventId} received and queued for processing",
                    eventId = event.eventId
                )
            )
        } catch (e: Exception) {
            LOG.warn("Failed to process '${event.eventName}' event with eventId ${event.eventId}: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                MetricsEventResponse(
                    success = false,
                    message = "Failed to process ${event.eventName} event with eventId ${event.eventId}: ${e.message}",
                    eventId = event.eventId
                )
            )
        }
    }
}
