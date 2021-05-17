package com.example.parking.controllers

import com.example.parking.dtos.TicketCode
import com.example.parking.exit.ExitController
import com.example.parking.exit.ExitService
import com.example.parking.visit.InvalidTicketCodeException
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ExitController::class])
class ExitControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var exitService: ExitService

    lateinit var validRequest: RequestBuilder

    private val ticketCode = 1234567890L

    @BeforeEach
    fun initValidRequest() {
        validRequest = put("/exit")
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(TicketCode(ticketCode)))
    }

    @Test
    fun `when request lacks ticketCode in body, then returns 400`() {
        mockMvc.perform(put("/exit"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `when valid request made, then returns 200`() {
        mockMvc.perform(validRequest)
            .andExpect(status().isOk)
    }

    @Test
    fun `when valid request made, then calls business logic`() {
        mockMvc.perform(validRequest)

        verify(exitService, times(1)).exit(eq(ticketCode))
    }

    @Test
    fun `when business logic throws, then proper error response returned`() {
        val exMessage = "Exception thrown!!!"

        `when`(exitService.exit(anyLong()))
            .thenThrow(InvalidTicketCodeException(exMessage))

        val mvcResponse = mockMvc.perform(validRequest)
            .andExpect(status().isBadRequest)
            .andReturn()

        assertThat(mvcResponse.response.errorMessage)
            .isEqualTo(exMessage)
    }
}