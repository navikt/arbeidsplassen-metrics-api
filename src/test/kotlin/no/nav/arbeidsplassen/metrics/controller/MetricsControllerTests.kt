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

class MetricsControllerTest {

    private val metricsService: MetricsService = mockk()
    private val controller = MetricsController(metricsService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `receiveEvent should return 200 when valid event is received`() {
        val event = MetricsEvent(
            "123",
            OffsetDateTime.now().toString(),
            "test_event",
            null
        )
        every { metricsService.processEvent(event) } returns Unit

        val response = mockMvc.post("/api/v1/metrics/event") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(event)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val responseEvent = objectMapper.readValue(
            response.response.contentAsString,
            MetricsEventResponse::class.java
        )
        verify(exactly = 1) { metricsService.processEvent(event) }
        assert(responseEvent.success)
        assert(responseEvent.eventId == event.eventId)
    }
}