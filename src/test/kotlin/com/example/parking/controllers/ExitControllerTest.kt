package com.example.parking.controllers

import com.example.parking.exit.ExitController
import com.example.parking.exit.ExitUseCase
import com.example.parking.exit.TicketCode
import com.example.parking.services.InvalidTicketCodeException
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ExitController::class])
class ExitControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var exitUseCase: ExitUseCase

    @Test
    fun `test exit route, when valid request made, then returns 200`() {
        val ticketCode = TicketCode(1234567890L)

        mockMvc.perform(
            put("/exit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(ticketCode))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `test exit route, when valid request made, then calls business logic`() {
        val ticketCode = TicketCode(1234567890L)

        mockMvc.perform(
            put("/exit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(ticketCode))
        )

        verify(exitUseCase, times(1)).exit(ticketCode.ticketCode)
    }

    @Test
    fun `test exit route, when business logic throws, then proper error response returned`() {
        val ticketCode = TicketCode(1234567890L)
        val exMessage = "Exception thrown!!!"

        `when`(exitUseCase.exit(ticketCode.ticketCode))
            .thenThrow(InvalidTicketCodeException(exMessage))

        val errResponse = mockMvc.perform(
            put("/exit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(ticketCode))
        ).andExpect(status().isBadRequest)
            .andReturn()

        assertThat(errResponse.response.errorMessage)
            .isEqualTo(exMessage)
    }

    @Test
    fun `test exit route, when request lacks ticketCode in body, then returns 400`() {
        mockMvc.perform(put("/exit"))
            .andExpect(status().isBadRequest)
    }
}