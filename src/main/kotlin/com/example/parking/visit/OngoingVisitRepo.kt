package com.example.parking.visit

import com.example.parking.models.OngoingVisit
import org.springframework.data.jpa.repository.JpaRepository

interface OngoingVisitRepo : JpaRepository<OngoingVisit, Long> {
    fun findByTicketCode(ticketCode: Long): OngoingVisit?
    fun existsByTicketCode(ticketCode: Long): Boolean
}