package no.nav.arbeidsplassen.metrics.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.arbeidsplassen.metrics.model.MetricsEvent
import no.nav.arbeidsplassen.metrics.model.MetricsEventResponse
import no.nav.arbeidsplassen.metrics.service.MetricsService
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime
import java.util.UUID

class MetricsControllerTest {

    private val metricsService: MetricsService = mockk()
    private val controller = MetricsController(metricsService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `receiveEvent should return 200 when valid event is received`() {
        val event = MetricsEvent(
            eventId = UUID.randomUUID(),
            createdAt = OffsetDateTime.now(),
            eventName = "test_event",
            eventData = null
        )
        val eventAsString = """
            {
                "eventId": "${event.eventId}",
                "createdAt": "${event.createdAt}",
                "eventName": "${event.eventName}",
                "eventData": null
            }
        """.trimIndent()
        every { metricsService.processEvent(any()) } returns Unit

        val response = mockMvc.post("/api/v1/metrics/event") {
            contentType = MediaType.APPLICATION_JSON
            content = eventAsString
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val responseEvent = objectMapper.readValue(
            response.response.contentAsString,
            MetricsEventResponse::class.java
        )
        verify(exactly = 1) { metricsService.processEvent(match {
            it.eventId == event.eventId &&
            it.eventName == event.eventName
        }) }
        assert(responseEvent.success)
        assert(responseEvent.eventId == event.eventId)
    }

    @Test
    fun `receiveEvent should return 400 when invalid event is received`() {
        val response = mockMvc.post("/api/v1/metrics/event") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "eventId":"123",
                    "createdAt":"2025-11-27T14:14:56.571423161+01:00",
                    "eventName":"test_event",
                    "eventData":null
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()
    }
}
