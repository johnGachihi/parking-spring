package com.example.parking.exit

import com.example.parking.models.ParkingTimeRange
import com.example.parking.models.Visit
import com.example.parking.util.Minutes
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val parkingTimeRangeRepo: ParkingTimeRangeRepo
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

    fun hasLatestPaymentExpired(visit: Visit): Boolean {
        // TODO: Add implementation
        return true
    }
}