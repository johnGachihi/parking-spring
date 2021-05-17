package com.example.parking.exit

import com.example.parking.visit.InvalidTicketCodeException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class ExitController(
    private val exitService: ExitService
) {

    @PutMapping("/exit")
    fun exit(@RequestBody ticketCode: TicketCode) {
        try {
            exitService.exit(ticketCode.ticketCode)
        } catch (e: InvalidTicketCodeException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

}

class TicketCode(val ticketCode: Long)

