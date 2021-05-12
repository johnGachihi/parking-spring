package com.example.parking.entry

import com.example.parking.models.Entry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface EntryRepository: JpaRepository<Entry, Long> {
    @Query("select case when count(e) > 0 then true else false end from entries e where e.ticketCode = :ticketCode and e.exitTime is null ")
    fun isTicketCodeInUse(ticketCode: String): Boolean // TODO: Use Long

    @Modifying
    @Query("update entries e set e.exitTime = current_timestamp where e.ticketCode = :ticketCode")
    fun markExited(@Param("ticketCode") ticketCode: String)
}