package com.example.parking.services

import com.example.parking.models.OngoingVisit
import com.example.parking.models.Visit
import com.example.parking.visit.InvalidTicketCodeException
import com.example.parking.visit.OngoingVisitRepo
import com.example.parking.visit.VisitRepository
import com.example.parking.visit.VisitService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class VisitServiceTest {
    @Mock
    lateinit var ongoingVisitRepo: OngoingVisitRepo
    @InjectMocks
    lateinit var visitService: VisitService

    @Test
    fun `when ticketCode has associated OngoingVisit, then throw InvalidTicketCodeException`() {
        val ticketCode = 1234567890L
        `when`(ongoingVisitRepo.existsByTicketCode(anyString()))
            .thenReturn(true)

        assertThatExceptionOfType(InvalidTicketCodeException::class.java).isThrownBy {
            visitService.addVisit(ticketCode.toString())
        }.withMessage("Provided ticket code is in use: $ticketCode")
    }

    @Test
    fun `when ticketCode is valid, then persists new visit`() {
        val ticketCode = "1234567890"
        val visit = OngoingVisit().apply { this.ticketCode = ticketCode }

        `when`(ongoingVisitRepo.existsByTicketCode(anyString()))
            .thenReturn(false)
        `when`(ongoingVisitRepo.save(any())) // TODO: Figure out how to by-pass this
            .thenReturn(visit)

        visitService.addVisit(ticketCode)

        verify(ongoingVisitRepo).save(argThat {
            it.ticketCode == ticketCode
        })
    }

    @Test
    fun `when ticketCode is valid, then returns saved Entry`() {
        val ticketCode = "1234567890"
        val expectedEntry = OngoingVisit().apply {
            id = 1
            entryTime = Instant.now()
            this.ticketCode = ticketCode
        }

        `when`(ongoingVisitRepo.save(any()))
            .thenReturn(expectedEntry)

        val actualEntry = visitService.addVisit(ticketCode)

        assertThat(actualEntry).isSameAs(expectedEntry)
    }
}