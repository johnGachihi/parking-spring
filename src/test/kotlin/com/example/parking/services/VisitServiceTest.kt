package com.example.parking.services

import com.example.parking.models.Visit
import com.example.parking.visit.VisitRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
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
    lateinit var visitRepository: VisitRepository
    @InjectMocks
    lateinit var visitService: VisitService

    @Test
    fun `test addEntry when ticketCode is invalid, then throw InvalidTicketCodeException`() {
        `when`(visitRepository.isTicketCodeInUse(anyString()))
            .thenReturn(true)

        assertThrows(InvalidTicketCodeException::class.java) {
            visitService.addVisit(anyString())
        }
    }

    @Test
    fun `test addEntry when ticketCode is valid, then calls entryRepository's save`() {
        val ticketCode = "1234567890"
        `when`(visitRepository.isTicketCodeInUse(ticketCode))
            .thenReturn(false)
        `when`(visitRepository.save(any(Visit::class.java))) // TODO: Figure out how to by-pass this
            .thenReturn(Visit().apply { this.ticketCode = ticketCode })

        visitService.addVisit(ticketCode)

        verify(visitRepository).save(any(Visit::class.java))
    }

    @Test
    fun `test addEntry when ticketCode is valid, then returns saved Entry`() {
        val ticketCode = "1234567890"
        val expectedEntry = Visit().apply {
            id = 1
            entryTime = Instant.now()
            this.ticketCode = ticketCode
        }

        `when`(visitRepository.save(any()))
            .thenReturn(expectedEntry)

        val actualEntry = visitService.addVisit(ticketCode)

        assertThat(actualEntry).isSameAs(expectedEntry)
    }
}