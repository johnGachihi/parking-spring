package com.example.parking.entry

import com.example.parking.models.Entry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EntryRepository: JpaRepository<Entry, Long> {
    @Query("select case when count(e) > 0 then true else false end from entries e where e.ticketCode = :ticketCode and e.exitTime is null ")
    fun isTicketCodeInUse(ticketCode: String): Boolean
}