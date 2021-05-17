package com.example.parking.exit

import com.example.parking.models.RegisteredVehicle
import org.springframework.data.jpa.repository.JpaRepository

interface RegisteredVehicleRepository: JpaRepository<RegisteredVehicle, Long> {
    fun existsByTicketCode(ticketCode: Long): Boolean
}