package com.example.parking.services

import com.example.parking.models.Entry
import com.example.parking.repositories.EntryRepository
import org.springframework.stereotype.Service

@Service
class EntryService(
    private val entryRepository: EntryRepository
) {
    fun addEntry(ticketCode: String): Entry {
        if (! isTicketCodeValid(ticketCode)) {
            throw InvalidTicketCodeException("Invalid ticket code: $ticketCode")
        }
        return entryRepository.save(Entry(ticketCode = ticketCode))
    }

    // TODO Move ticketCode validation to TicketCodeService
    // 1. Must not be in use
    private fun isTicketCodeValid(ticketCode: String): Boolean {
        return !entryRepository.isTicketCodeInUse(ticketCode)
    }
}

class InvalidTicketCodeException(
    message: String? = null
) : IllegalArgumentException(message)