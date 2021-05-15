package com.example.parking.services

import com.example.parking.models.Visit
import com.example.parking.visit.VisitRepository
import org.springframework.stereotype.Service
import kotlin.Exception

// Change name to VisitService
@Service
class VisitService(
    private val visitRepository: VisitRepository
) {
    // TODO: throw different exception for each invalid scenario
    fun addVisit(ticketCode: String): Visit {
        if (! isTicketCodeValid(ticketCode)) {
            throw InvalidTicketCodeException("Invalid ticket code: $ticketCode")
        }
        // OngoingVisitRepo.save
        return visitRepository.save(Visit().apply { this.ticketCode = ticketCode })
    }

    // TODO Move ticketCode validation to TicketCodeService
    // 1. Must not be in use
    private fun isTicketCodeValid(ticketCode: String): Boolean {
        // use ongoingRepository.existsByTicketCode(...)
        return !visitRepository.isTicketCodeInUse(ticketCode)
    }
}

class InvalidTicketCodeException(
    message: String? = null
) : IllegalArgumentException(message)