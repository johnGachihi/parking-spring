package com.example.parking.exit

import com.example.parking.models.ParkingTimeRange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParkingFeeRepository : JpaRepository<ParkingTimeRange, Long> {
    fun findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(timeOfStayInMinutes: Long): ParkingTimeRange?
    fun findFirstByOrderByPointOnTimeLineDesc(): ParkingTimeRange?
}