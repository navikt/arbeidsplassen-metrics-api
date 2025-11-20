package no.nav.arbeidsplassen.metrics.controller

import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import no.nav.arbeidsplassen.metrics.model.MetricsEventResponse
import no.nav.arbeidsplassen.metrics.service.MetricsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/metrics")
@CrossOrigin(origins = ["*"]) // Configure this properly for production
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

    // TODO: Move health checks to a dedicated HealthController
    @GetMapping("internal/isAlive")
    fun isAlive(): ResponseEntity<HttpStatus> {
        // TODO: Add real health check logic
        return ResponseEntity<HttpStatus>(HttpStatus.OK)
    }

    @GetMapping("internal/isReady")
    fun isReady() = ResponseEntity<HttpStatus>(HttpStatus.OK)
}
