package com.example.parking.exit

import com.example.parking.models.ParkingTimeRange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParkingTimeRangeRepo : JpaRepository<ParkingTimeRange, Long> {
    // TODO: Cache
    @Query("select p from parking_time_range p order by p.upperLimit")
    fun findAllOrderByUpperLimit(): List<ParkingTimeRange>
}