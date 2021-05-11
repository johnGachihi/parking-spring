package com.example.parking.services

import com.example.parking.models.Entry
import com.example.parking.entry.EntryRepository
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
internal class EntryServiceTest {
    @Mock
    lateinit var entryRepository: EntryRepository
    @InjectMocks
    lateinit var entryService: EntryService

    @Test
    fun `test addEntry when ticketCode is invalid, then throw InvalidTicketCodeException`() {
        `when`(entryRepository.isTicketCodeInUse(anyString()))
            .thenReturn(true)

        assertThrows(InvalidTicketCodeException::class.java) {
            entryService.addEntry(anyString())
        }
    }

    @Test
    fun `test addEntry when ticketCode is valid, then calls entryRepository's save`() {
        val ticketCode = "1234567890"
        `when`(entryRepository.isTicketCodeInUse(ticketCode))
            .thenReturn(false)
        `when`(entryRepository.save(any(Entry::class.java))) // TODO: Figure out how to by-pass this
            .thenReturn(Entry(ticketCode = ticketCode))

        entryService.addEntry(ticketCode)

        verify(entryRepository).save(any(Entry::class.java))
    }

    @Test
    fun `test addEntry when ticketCode is valid, then returns saved Entry`() {
        val ticketCode = "1234567890"
        val expectedEntry = Entry(
            id = 1, entryTime = Instant.now(), ticketCode = ticketCode)

        `when`(entryRepository.save(any()))
            .thenReturn(expectedEntry)

        val actualEntry = entryService.addEntry(ticketCode)

        assertThat(actualEntry).isSameAs(expectedEntry)
    }
}