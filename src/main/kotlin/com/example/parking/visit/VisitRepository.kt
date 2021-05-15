package com.example.parking.visit

import com.example.parking.models.Visit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VisitRepository: JpaRepository<Visit, Long> {
    @Deprecated(message = "Use findFirstByTicketCodeOrderByEntryTimeDesc instead")
    @Query("select case when count(e) > 0 then true else false end from entries e where e.ticketCode = :ticketCode and e.exitTime is null ")
    fun isTicketCodeInUse(ticketCode: String): Boolean // TODO: Use Long

    /*//Changes @Query to: update visit v set v.exitTime = current_timestamp where e.id = :id
    // Try first in OngoingVisitRepo
    @Modifying
    @Query("update entries e set e.exitTime = current_timestamp where e.ticketCode = :ticketCode")
    fun markExited(@Param("ticketCode") ticketCode: String)*/

    // Changes to ongoingVisitRepo.findByTicketCode(ticketCode: String): Visit?
//    fun findFirstByTicketCodeAndExitTimeIsNull(ticketCode: String): Visit?
}