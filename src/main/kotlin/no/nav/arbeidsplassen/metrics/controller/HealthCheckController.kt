package no.nav.arbeidsplassen.metrics.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class HealthCheckController {
    // TODO: Move health checks to a dedicated HealthController
    @GetMapping("/isAlive")
    fun isAlive(): ResponseEntity<HttpStatus> {
        // TODO: Add real health check logic
        return ResponseEntity<HttpStatus>(HttpStatus.OK)
    }

    @GetMapping("/isReady")
    fun isReady(): ResponseEntity<HttpStatus> = ResponseEntity<HttpStatus>(HttpStatus.OK)
}
