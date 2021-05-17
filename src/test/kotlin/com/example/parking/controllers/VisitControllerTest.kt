package com.example.parking.controllers

import com.example.parking.dtos.TicketCode
import com.example.parking.visit.VisitController
import com.example.parking.models.Visit
import com.example.parking.visit.VisitService
import com.example.parking.visit.InvalidTicketCodeException
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [VisitController::class])
class VisitRepositoryTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var visitService: VisitService

    private val ticketCode = 1234567890L

    private lateinit var validEntryRequest: RequestBuilder

    @BeforeEach
    fun beforeEach() {
        validEntryRequest = post("/entries")
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(TicketCode(ticketCode)))
    }

    @Test
    fun `test addEntry route when request lacks required content, then returns 400`() {
        mockMvc.perform(post("/entries"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `test addEntry route when valid request made, then returns 200`() {
        mockMvc.perform(validEntryRequest)
            .andExpect {
                status().isOk
            }
    }

    @Test
    fun `test addEntry route when valid request made, then calls business logic`() {
        mockMvc.perform(validEntryRequest)
            .andExpect(status().isOk)

        verify(visitService, times(1))
            .addVisit(1234567890)
    }

    @Test
    fun `test addEntry route, when valid request, then returns Entry resource`() {
        `when`(visitService.addVisit(1234567890))
            .thenReturn(Visit().apply { ticketCode = 1234567890 })

        val mvcResponse = mockMvc.perform(validEntryRequest)
            .andExpect(status().isOk)
            .andReturn()

        val actualResponseBody = objectMapper.readValue(
            mvcResponse.response.contentAsString, Visit::class.java
        )

        assertThat(actualResponseBody.ticketCode).isEqualTo(1234567890)
    }

    @Test
    fun `when business logic throws, then proper 400 error response returned`() {
        `when`(visitService.addVisit(ticketCode))
            .thenThrow(InvalidTicketCodeException("Exception message. Oh noo!"))

        val mvcResponse = mockMvc.perform(validEntryRequest)
            .andExpect(status().isBadRequest)
            .andReturn()

        assertThat(mvcResponse.response.errorMessage)
            .isEqualTo("Exception message. Oh noo!")
    }
}