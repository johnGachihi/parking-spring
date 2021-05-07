package com.example.parking.repositories

import com.example.parking.models.Entry
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.Instant

@DataJpaTest
class EntryRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val entryRepository: EntryRepository
) {
    @Test
    fun `test isTicketCodeInUse when ticketCode is in use, then returns true`() {
        val rfid = "1234567"
        val exitTimeNotNull = Entry(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persist(exitTimeNotNull)

        val exitTimeNull = Entry(
            ticketCode = rfid, exitTime = null)
        entityManager.persistAndFlush(exitTimeNull)

        assertTrue(entryRepository.isTicketCodeInUse(rfid))
    }

    @Test
    fun `test isTicketCodeInUse when ticketCode is not in use, then returns false`() {
        val rfid = "1234567"
        val exitTimeNotNull = Entry(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persist(exitTimeNotNull)

        val exitTimeNull = Entry(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persistAndFlush(exitTimeNull)

        assertFalse(entryRepository.isTicketCodeInUse(rfid))
    }
}