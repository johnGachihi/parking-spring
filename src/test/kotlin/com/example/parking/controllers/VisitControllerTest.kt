package com.example.parking.controllers

import com.example.parking.visit.VisitController
import com.example.parking.models.Visit
import com.example.parking.services.VisitService
import com.example.parking.services.InvalidTicketCodeException
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
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

    @Test
    fun `test addEntry route when request lacks required content, then returns 400`() {
        mockMvc.perform(post("/entries"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `test addEntry route when valid request made, then returns 200`() {
        mockMvc.perform(
            post("/entries")
                .content("1234567890")
        )
            .andExpect {
                status().isOk
            }
    }

    @Test
    fun `test addEntry route when valid request made, then calls business logic`() {
        mockMvc.perform(
            post("/entries")
                .content("1234567890"))
            .andExpect(status().isOk)

        verify(visitService, times(1))
            .addVisit("1234567890")
    }

    @Test
    fun `test addEntry route when valid request, then returns Entry resource`() {
        `when`(visitService.addVisit("1234567890"))
            .thenReturn(Visit().apply { ticketCode = "1234567890" })

        val mvcResponse = mockMvc.perform(
            post("/entries")
                .content("1234567890"))
            .andExpect(status().isOk)
            .andReturn()

        val actualResponseBody = objectMapper.readValue(
            mvcResponse.response.contentAsString, Visit::class.java)

        assertThat(actualResponseBody.ticketCode).isEqualTo("1234567890")
    }

    @Test
    fun `test addEntry route when ticket code invalid, then proper error response returned`() {
        `when`(visitService.addVisit("1234567890"))
            .thenThrow(InvalidTicketCodeException("Exception message. Oh noo!"))

        val mvcResponse = mockMvc.perform(
            post("/entries")
                .content("1234567890"))
            .andExpect(status().isBadRequest)
            .andReturn()

        assertThat(mvcResponse.response.errorMessage)
            .isEqualTo("Exception message. Oh noo!")
    }
}