package com.example.parking.util

import com.example.parking.exit.ParkingFeeRepository
import org.springframework.stereotype.Service

@Service
class ParkingFeeCalc(
    private val parkingFeeRepository: ParkingFeeRepository
) {
    // todo: Use entryTime as param, instead of timeOfStayInMinutes
    fun calculateFee(timeOfStayInMinutes: Long): Double {
        var fee = parkingFeeRepository
            .findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(timeOfStayInMinutes)?.fee
        if (fee != null) {
            return fee
        }

        fee = parkingFeeRepository.findFirstByOrderByPointOnTimeLineDesc()?.fee
        if (fee != null) {
            return fee
        }

        // ParkingFeeRepo contains no parking fee records
        return 0.0
    }
}

class NoConfigurationException(message: String) : Exception(message)