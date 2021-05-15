package com.example.parking.services

import com.example.parking.entry.EntryRepository
import com.example.parking.exit.ExitUseCase
import com.example.parking.exit.RegisteredVehicleRepository
import com.example.parking.models.Entry
import com.example.parking.util.ParkingFeeCalc
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
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

    @Mock
    lateinit var parkingFeeCalc: ParkingFeeCalc

    @InjectMocks
    lateinit var exitUseCase: ExitUseCase

    @Test
    fun `test exit, when ticketCode is not associated to unexited Entry, then throws`() {
        `when`(entryRepository.findFirstByTicketCodeAndExitTimeIsNull("12345"))
            .thenReturn(null)

        assertThatExceptionOfType(InvalidTicketCodeException::class.java).isThrownBy {
            exitUseCase.exit(12345)
        }.withMessage("The provided ticket code is not in use. Ticket code: 12345")

        verify(entryRepository, times(0)).markExited("12345")
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

    @Test
    fun `test exit, when ticketCode is in use, is not for registered vehicle and fee is 0, then updates entry to show exited`() {
        val ticketCode = 12345L
        `when`(entryRepository.isTicketCodeInUse(ticketCode.toString()))
            .thenReturn(true)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode))
            .thenReturn(false)
        `when`(entryRepository.findFirstByTicketCodeAndExitTimeIsNull(ticketCode.toString()))
            .thenReturn(Entry(ticketCode = ticketCode.toString()))
        `when`(parkingFeeCalc.calculateFee(anyLong()))
            .thenReturn(0.0)

        exitUseCase.exit(ticketCode)

        verify(entryRepository, times(1))
            .markExited(ticketCode.toString())
    }

    @Test
    fun `test exit, when ticketCode is in use, is not for registered vehicle, fee is not 0 and there are no payments, then throws payment not made`() {

    }
}