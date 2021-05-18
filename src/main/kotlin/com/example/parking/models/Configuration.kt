package com.example.parking.models

import javax.persistence.*
import javax.validation.constraints.NotNull

object ConfigLabel {
    const val PARKING_FEE = "parking_fee"
}

@Entity(name = "configuration")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class Configuration {
    @Id
    @GeneratedValue
    open var id: Long? = null

    @NotNull
    @Column(unique = true)
    open var key: String = ""

    @NotNull
    open var value: String = ""
}

@Entity(name = "parking_fee_configuration")
open class ParkingFeeConfigModel : Configuration()