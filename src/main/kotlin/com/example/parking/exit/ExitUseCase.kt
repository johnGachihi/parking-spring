package com.example.parking.exit

import com.example.parking.entry.EntryRepository
import com.example.parking.services.InvalidTicketCodeException
import org.springframework.stereotype.Service

@Service
class ExitUseCase(
    private val registeredVehicleRepository: RegisteredVehicleRepository,
    private val entryRepository: EntryRepository
) {
    fun exit(ticketCode: Long) {
        if (!entryRepository.isTicketCodeInUse(ticketCode.toString())) {
            throw InvalidTicketCodeException("The provided ticket code is not in use. Ticket code: $ticketCode")
        }

        if (!registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode)) {
            throw InvalidTicketCodeException(
                "Ticket code does not belong to any registered vehicle. Ticket code: $ticketCode")
        }

        entryRepository.markExited(ticketCode.toString())
    }
}