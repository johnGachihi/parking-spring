package com.example.parking.visit

import com.example.parking.models.OngoingVisit
import org.springframework.data.jpa.repository.JpaRepository

interface OngoingVisitRepo : JpaRepository<OngoingVisit, Long> {
    fun findByTicketCode(ticketCode: String): OngoingVisit?
    fun existsByTicketCode(ticketCode: String): Boolean
}