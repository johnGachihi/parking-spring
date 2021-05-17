package com.example.parking.services

import com.example.parking.visit.VisitRepository
import com.example.parking.exit.ExitService
import com.example.parking.exit.PaymentService
import com.example.parking.exit.RegisteredVehicleRepository
import com.example.parking.exit.UnservicedParkingBill
import com.example.parking.models.FinishedVisit
import com.example.parking.models.OngoingVisit
import com.example.parking.models.Payment
import com.example.parking.util.ParkingFeeCalc
import com.example.parking.visit.FinishedVisitRepo
import com.example.parking.visit.InvalidTicketCodeException
import com.example.parking.visit.OngoingVisitRepo
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ExitServiceTest {
    @Mock
    lateinit var registeredVehicleRepository: RegisteredVehicleRepository

    @Mock
    lateinit var visitRepository: VisitRepository

    @Mock
    lateinit var ongoingVisitRepo: OngoingVisitRepo

    @Mock
    lateinit var finishedVisitRepo: FinishedVisitRepo

    @Mock
    lateinit var parkingFeeCalc: ParkingFeeCalc

    @Mock
    lateinit var paymentService: PaymentService

    @Captor
    lateinit var finishedVisitCaptor: ArgumentCaptor<FinishedVisit>

    @InjectMocks
    lateinit var exitService: ExitService

    lateinit var ongoingVisit: OngoingVisit

    @BeforeEach
    fun beforeEach() {
        ongoingVisit = OngoingVisit().apply {
            this.ticketCode = "1234567890"
            entryTime = Instant.now()
        }

        ongoingVisit.payments = listOf(
            Payment().apply {
                visit = ongoingVisit
                madeAt = Instant.now()
            },
            Payment().apply {
                visit = ongoingVisit
                madeAt = Instant.now().plusSeconds(10)
            })
    }

    @Test
    fun `when ticketCode is not associated to any OngoingVisit, then throws`() {
        `when`(ongoingVisitRepo.findByTicketCode("12345"))
            .thenReturn(null)

        assertThatExceptionOfType(InvalidTicketCodeException::class.java).isThrownBy {
            exitService.exit(12345)
        }.withMessage("The provided ticket code is not in use. Ticket code: 12345")
    }

    @Test
    fun `when ticketCode is for registered vehicle, then finishes visit`() {
        val ticketCode = ongoingVisit.ticketCode.toLong()

        `when`(ongoingVisitRepo.findByTicketCode(ticketCode.toString()))
            .thenReturn(ongoingVisit)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode))
            .thenReturn(true)

        exitService.exit(ticketCode)

        assertFinishesVisit()
    }

    @Test
    fun `when ticketCode is not for registered vehicle and fee is 0, then finishes visit`() {
        val ticketCode = ongoingVisit.ticketCode.toLong()

        `when`(ongoingVisitRepo.findByTicketCode(ticketCode.toString()))
            .thenReturn(ongoingVisit)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode))
            .thenReturn(false)
        `when`(parkingFeeCalc.calculateFee(anyLong()))
            .thenReturn(0.0)

        exitService.exit(ticketCode.toLong())

        assertFinishesVisit()
    }

    @Test
    fun `when ticketCode is not for registered vehicle, fee is not 0 and there are no payments, then throws unserviced bill exception`() {
        ongoingVisit.payments = emptyList()

        `when`(ongoingVisitRepo.findByTicketCode(ongoingVisit.ticketCode))
            .thenReturn(ongoingVisit)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ongoingVisit.ticketCode.toLong()))
            .thenReturn(false)
        `when`(parkingFeeCalc.calculateFee(anyLong()))
            .thenReturn(100.0)

        assertThatExceptionOfType(UnservicedParkingBill::class.java).isThrownBy {
            exitService.exit(ongoingVisit.ticketCode.toLong())
        }
    }

    @Test
    fun `when ticketCode is not for registered vehicle, fee is not 0 and last payment has expired, then throws unserviced bill exception`() {
        `when`(ongoingVisitRepo.findByTicketCode(ongoingVisit.ticketCode))
            .thenReturn(ongoingVisit)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ongoingVisit.ticketCode.toLong()))
            .thenReturn(false)
        `when`(parkingFeeCalc.calculateFee(anyLong()))
            .thenReturn(100.0)
        `when`(paymentService.hasLatestPaymentExpired(ongoingVisit))
            .thenReturn(true)

        assertThatExceptionOfType(UnservicedParkingBill::class.java).isThrownBy {
            exitService.exit(ongoingVisit.ticketCode.toLong())
        }.withMessage("Latest payment expired")
    }

    @Test
    fun `when ticketCode is not for registered vehicle, fee is not 0 and last payment has not expired, then finishes visit`() {
        `when`(ongoingVisitRepo.findByTicketCode(ongoingVisit.ticketCode))
            .thenReturn(ongoingVisit)
        `when`(registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ongoingVisit.ticketCode.toLong()))
            .thenReturn(false)
        `when`(parkingFeeCalc.calculateFee(anyLong()))
            .thenReturn(100.0)
        `when`(paymentService.hasLatestPaymentExpired(ongoingVisit))
            .thenReturn(false)

        exitService.exit(ongoingVisit.ticketCode.toLong())

        assertFinishesVisit()
    }

    private fun assertFinishesVisit() {
        verify(ongoingVisitRepo, times(1)).delete(ongoingVisit)

        verify(finishedVisitRepo, times(1))
            .save(finishedVisitCaptor.capture())

        assertThat(finishedVisitCaptor.value)
            .extracting("ticketCode", "entryTime")
            .containsExactly(ongoingVisit.ticketCode, ongoingVisit.entryTime)
    }
}