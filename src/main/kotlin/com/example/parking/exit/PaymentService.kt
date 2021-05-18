package com.example.parking.exit

import com.example.parking.models.ParkingTimeRange
import com.example.parking.models.Payment
import com.example.parking.payment.ParkingFeeConfig
import com.example.parking.util.Minutes
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

@Service
class PaymentService(
    private val parkingTimeRangeRepo: ParkingTimeRangeRepo,
    private val parkingFeeConfig: ParkingFeeConfig
) {
    fun calculateFee(timeOfStay: Minutes): Double {
        val parkingTimeRanges = parkingTimeRangeRepo.findAllOrderByUpperLimit()

        // Datasource contains no parking fee records
        if (parkingTimeRanges.isEmpty())
            return 0.0

        val timeRange = getOverlappingTimeRange(timeOfStay, parkingTimeRanges)
            ?: parkingTimeRanges.last()

        return timeRange.fee
    }

    private fun getOverlappingTimeRange(
        timeOfStay: Minutes,
        parkingTimeRanges: List<ParkingTimeRange>
    ): ParkingTimeRange? {
        if (parkingTimeRanges.isEmpty())
            return null

        parkingTimeRanges.sortedBy {
            it.upperLimit.minutes
        }
        return parkingTimeRanges.find {
            it.upperLimit.minutes > timeOfStay.minutes
        }
    }

    fun paymentExpired(payment: Payment): Boolean {
        val paymentExpirationTime = parkingFeeConfig.paymentExpirationTime
            ?: Minutes(20)   // TODO: Shouldn't this default be set somewhere else?

        val paymentAge = Instant.now()
            .until(payment.madeAt, ChronoUnit.MINUTES).absoluteValue

        return paymentAge > paymentExpirationTime.minutes

    }
}