package com.example.parking.services

import com.example.parking.entry.EntryRepository
import com.example.parking.exit.ExitUseCase
import com.example.parking.exit.RegisteredVehicleRepository
import com.example.parking.models.RegisteredVehicle
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ExitUseCaseTest {
    @Mock
    lateinit var registeredVehicleRepository: RegisteredVehicleRepository

    @Mock
    lateinit var entryRepository: EntryRepository

    @InjectMocks
    lateinit var exitUseCase: ExitUseCase

    @Test
    fun `test exit, when ticketCode is not 'in use', then throws`() {
        `when`(entryRepository.isTicketCodeInUse("12345"))
            .thenReturn(false)

        assertThatExceptionOfType(InvalidTicketCodeException::class.java).isThrownBy {
            exitUseCase.exit(12345)
        }.withMessage("The provided ticket code is not in use. Ticket code: 12345")

        verify(entryRepository, times(0)).markExited("12345")
    }

    @Test
    fun `test exit, when ticketCode is in use and is not for registered vehicle, then throws`() {
        val ticketCode = 12345L
        `when`(entryRepository.isTicketCodeInUse(ticketCode.toString()))
            .thenReturn(true)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode))
            .thenReturn(false)

        assertThatExceptionOfType(InvalidTicketCodeException::class.java).isThrownBy {
            exitUseCase.exit(ticketCode)
        }.withMessage("Ticket code does not belong to any registered vehicle. Ticket code: $ticketCode")
    }

    @Test
    fun `test exit, when ticketCode is in use and is for registered vehicle, then updates entry to show exited`() {
        val ticketCode = 12345L
        `when`(entryRepository.isTicketCodeInUse(ticketCode.toString()))
            .thenReturn(true)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode))
            .thenReturn(true)

        exitUseCase.exit(ticketCode)

        verify(entryRepository, times(1)).markExited(ticketCode.toString())
    }
}