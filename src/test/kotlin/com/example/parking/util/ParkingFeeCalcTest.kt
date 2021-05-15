package com.example.parking.util

import com.example.parking.exit.ParkingFeeRepository
import com.example.parking.models.ParkingTimeRange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class ParkingFeeCalcTest {
    @Mock
    lateinit var parkingFeeRepository: ParkingFeeRepository

    @Test
    fun `test when there is no parking fee data in DB, then return fee as 0`() {
        `when`(parkingFeeRepository.findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(10))
            .thenReturn(null)

        `when`(parkingFeeRepository.findFirstByOrderByPointOnTimeLineDesc())
            .thenReturn(null)

        val parkingFeeCalc = ParkingFeeCalc(parkingFeeRepository)

        assertThat(parkingFeeCalc.calculateFee(10))
            .isEqualTo(0.0)
    }

    @Test
    fun `test when timeOfStay is within defined range, then returns correct fee`() {
        `when`(parkingFeeRepository.count()).thenReturn(1)

        `when`(parkingFeeRepository.findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(10))
            .thenReturn(
                ParkingTimeRange().apply {
                    pointOnTimeLine = 15
                    fee = 200.0
                })

        val parkingFeeCalc = ParkingFeeCalc(parkingFeeRepository)

        assertThat(parkingFeeCalc.calculateFee(10))
            .isEqualTo(200.0)
    }

    @Test
    fun `test when timeOfStay is beyond all defined ranges, then returns fee for last range on timeline`() {
        `when`(parkingFeeRepository.count()).thenReturn(1)

        `when`(parkingFeeRepository.findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(10))
            .thenReturn(null)

        `when`(parkingFeeRepository.findFirstByOrderByPointOnTimeLineDesc())
            .thenReturn(
                ParkingTimeRange().apply {
                    pointOnTimeLine = 15
                    fee = 200.0
                })

        val parkingFeeCalc = ParkingFeeCalc(parkingFeeRepository)

        assertThat(parkingFeeCalc.calculateFee(timeOfStayInMinutes = 10))
            .isEqualTo(200.0)
    }

}