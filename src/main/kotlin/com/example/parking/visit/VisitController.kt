package com.example.parking.visit

import com.example.parking.models.Visit
import com.example.parking.services.VisitService
import com.example.parking.services.InvalidTicketCodeException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class VisitController(
    val visitService: VisitService
) {
    @PostMapping("/entries")
    fun addEntry(@RequestBody ticketCode: String): Visit {
        return try {
            visitService.addVisit(ticketCode)
        } catch (e: InvalidTicketCodeException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}