package no.nav.arbeidsplassen.metrics.controller

import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import no.nav.arbeidsplassen.metrics.model.MetricsEventResponse
import no.nav.arbeidsplassen.metrics.service.MetricsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/metrics")
class MetricsController(private val metricsService: MetricsService) {

    @PostMapping("/events")
    fun receiveEvent(@RequestBody event: MetricsEvent): ResponseEntity<MetricsEventResponse> {
        return try {
            metricsService.processEvent(event)
            ResponseEntity.ok(
                MetricsEventResponse(
                    success = true,
                    message = "Event received and queued for processing",
                    eventId = event.eventId
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                MetricsEventResponse(
                    success = false,
                    message = "Failed to process event: ${e.message}"
                )
            )
        }
    }

    @PostMapping("/events/rating")
    fun receiveRatingEvent(@RequestBody event: MetricsEvent): ResponseEntity<MetricsEventResponse> {
        return try {
            metricsService.processEvent(event: MetricsEvent)
            ResponseEntity.ok(
                MetricsEventResponse(
                    success = true,
                    message = "Rating event received and queued for processing",
                    eventId = event.eventId
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                MetricsEventResponse(
                    success = false,
                    message = "Failed to process rating event: ${e.message}"
                )
            )
        }
    }

    @PostMapping("/events/cookie-consent")
    fun receiveCookieConsentEvent(@RequestBody event: MetricsEvent): ResponseEntity<MetricsEventResponse> {
        return try {
            metricsService.processEvent(event: MetricsEvent)
            ResponseEntity.ok(
                MetricsEventResponse(
                    success = true,
                    message = "Rating event received and queued for processing",
                    eventId = event.eventId
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                MetricsEventResponse(
                    success = false,
                    message = "Failed to process rating event: ${e.message}"
                )
            )
        }
    }
}
