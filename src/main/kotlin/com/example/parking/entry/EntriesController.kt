package com.example.parking.entry

import com.example.parking.models.Entry
import com.example.parking.services.EntryService
import com.example.parking.services.InvalidTicketCodeException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class EntriesController(
    val entryService: EntryService
) {
    @PostMapping("/entries")
    fun addEntry(@RequestBody ticketCode: String): Entry {
        return try {
            entryService.addEntry(ticketCode)
        } catch (e: InvalidTicketCodeException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}