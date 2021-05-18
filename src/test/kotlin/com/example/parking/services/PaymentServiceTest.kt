package com.example.parking.services

import com.example.parking.exit.ParkingTimeRangeRepo
import com.example.parking.exit.PaymentService
import com.example.parking.models.ParkingTimeRange
import com.example.parking.models.Payment
import com.example.parking.payment.ParkingFeeConfig
import com.example.parking.util.Minutes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockitoExtension::class)
internal class PaymentServiceTest {
    @Mock
    lateinit var parkingTimeRangeRepo: ParkingTimeRangeRepo

    @Mock
    lateinit var parkingFeeConfig: ParkingFeeConfig

    @InjectMocks
    lateinit var paymentService: PaymentService

    @Nested
    @DisplayName("Test calculateFee()")
    inner class CalculatorFeeTest {
        @Test
        fun `when there is no parking time-range data in DB, then return fee as 0`() {
            `when`(parkingTimeRangeRepo.findAllOrderByUpperLimit())
                .thenReturn(emptyList())

            assertThat(paymentService.calculateFee(Minutes(10)))
                .isEqualTo(0.0)
        }

        @Test
        fun `when timeOfStay is within a defined range, then returns correct fee`() {
            `when`(parkingTimeRangeRepo.findAllOrderByUpperLimit())
                .thenReturn(
                    listOf(
                        ParkingTimeRange().apply { upperLimit = Minutes(10); fee = 100.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(15); fee = 200.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(20); fee = 300.0 }
                    ))

            assertThat(paymentService.calculateFee(Minutes(10)))
                .isEqualTo(200.0)
        }

        @Test
        fun `when timeOfStay is beyond all defined ranges, then returns fee for last range on timeline`() {
            `when`(parkingTimeRangeRepo.findAllOrderByUpperLimit())
                .thenReturn(
                    listOf(
                        ParkingTimeRange().apply { upperLimit = Minutes(15); fee = 100.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(20); fee = 200.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(25); fee = 300.0 }
                    ))

            assertThat(paymentService.calculateFee(timeOfStay = Minutes(50)))
                .isEqualTo(300.0)
        }
    }

    @Nested
    @DisplayName("Test paymentExpired()")
    inner class PaymentExpiredTest {
        @Test
        fun `when payment is older than expiration time, then returns true`() {
            `when`(parkingFeeConfig.paymentExpirationTime)
                .thenReturn(Minutes(15))

            val twentyMinutesAgo =
                Instant.now().minus(20, ChronoUnit.MINUTES)

            val payment = Payment().apply {
                madeAt = twentyMinutesAgo
            }

            assertThat(paymentService.paymentExpired(payment))
                .isTrue
        }

        @Test
        fun `when payment is not older than expiration time, then returns false`() {
            `when`(parkingFeeConfig.paymentExpirationTime)
                .thenReturn(Minutes(15))

            val tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES)

            val payment = Payment().apply {
                madeAt = tenMinutesAgo
            }

            assertThat(paymentService.paymentExpired(payment))
                .isFalse
        }
    }
}