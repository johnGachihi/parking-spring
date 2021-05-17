package com.example.parking.visit

import com.example.parking.models.OngoingVisit
import com.example.parking.models.Visit
import org.springframework.stereotype.Service

@Service
class VisitService(
    private val ongoingVisitRepo: OngoingVisitRepo,
) {
    fun addVisit(ticketCode: String): Visit {
        if (isTicketCodeInUse(ticketCode)) {
            throw InvalidTicketCodeException("Provided ticket code is in use: $ticketCode")
        }
        return ongoingVisitRepo.save(OngoingVisit().apply { this.ticketCode = ticketCode })
    }

    private fun isTicketCodeInUse(ticketCode: String): Boolean {
        return ongoingVisitRepo.existsByTicketCode(ticketCode)
    }
}

class InvalidTicketCodeException(
    message: String? = null
) : IllegalArgumentException(message)