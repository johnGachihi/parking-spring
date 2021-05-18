package com.example.parking.services

import com.example.parking.exit.ParkingTimeRangeRepo
import com.example.parking.exit.PaymentService
import com.example.parking.models.ParkingTimeRange
import com.example.parking.util.Minutes
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class PaymentServiceTest {
    @Nested
    @DisplayName("Test calculateFee()")
    inner class CalculatorFeeTest {
        @Mock
        lateinit var parkingTimeRangeRepo: ParkingTimeRangeRepo

        @InjectMocks
        lateinit var paymentService: PaymentService

        @Test
        fun `when there is no parking time-range data in DB, then return fee as 0`() {
            Mockito.`when`(parkingTimeRangeRepo.findAllOrderByUpperLimit())
                .thenReturn(emptyList())

            Assertions.assertThat(paymentService.calculateFee(Minutes(10)))
                .isEqualTo(0.0)
        }

        @Test
        fun `when timeOfStay is within a defined range, then returns correct fee`() {
            Mockito.`when`(parkingTimeRangeRepo.findAllOrderByUpperLimit())
                .thenReturn(
                    listOf(
                        ParkingTimeRange().apply { upperLimit = Minutes(10); fee = 100.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(15); fee = 200.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(20); fee = 300.0 }
                    )
                )

            Assertions.assertThat(paymentService.calculateFee(Minutes(10)))
                .isEqualTo(200.0)
        }

        @Test
        fun `when timeOfStay is beyond all defined ranges, then returns fee for last range on timeline`() {
            Mockito.`when`(parkingTimeRangeRepo.findAllOrderByUpperLimit())
                .thenReturn(
                    listOf(
                        ParkingTimeRange().apply { upperLimit = Minutes(15); fee = 100.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(20); fee = 200.0 },
                        ParkingTimeRange().apply { upperLimit = Minutes(25); fee = 300.0 }
                    )
                )

            Assertions.assertThat(paymentService.calculateFee(timeOfStay = Minutes(50)))
                .isEqualTo(300.0)
        }
    }
}