package com.example.parking.models

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "registered_vehicles")
@Inheritance(strategy = InheritanceType.JOINED)
open class RegisteredVehicle {
    @Id
    @NotNull
    @GeneratedValue
    open var id: Long? = null

    @NotNull
    @Column(unique = true)
    open var rfidCode: Long? = null

    @NotNull
    @Column(unique = true)
    open var numberPlate: String? = null
}

@Entity
@PrimaryKeyJoinColumn(name = "registered_vehicle_id")
open class ExternalCompanyVehicle : RegisteredVehicle() {
    // TODO: Temporary fields. WIP
    @NotNull
    open var companyId: String? = null
}

@Entity
@PrimaryKeyJoinColumn(name = "registered_vehicle_id")
open class StaffVehicle: RegisteredVehicle() {
    // TODO: Temporary fields. WIP
    open var firstName: String? = null
    open var lastName: String? = null
}