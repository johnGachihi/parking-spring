package com.example.parking.models

import com.example.parking.util.Minutes
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

// TODO: Check nullness
@Entity(name = "parking_time_range")
open class ParkingTimeRange {
    @Id
    @GeneratedValue
    open var id: Long? = null

    @NotNull
    @Min(1)
    @Column(unique = true)
    open var upperLimit: Minutes = Minutes(0)

    @NotNull
    open var fee: Double = 0.0
}