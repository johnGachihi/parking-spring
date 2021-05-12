package com.example.parking.repositories

import com.example.parking.entry.EntryRepository
import com.example.parking.models.Entry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.Instant

@DataJpaTest
class EntryRepositoryTest @Autowired constructor( // TODO: Make DI style uniform across tests
    val entityManager: TestEntityManager,
    val entryRepository: EntryRepository
) {
    @Test
    fun `test isTicketCodeInUse when ticketCode is in use, then returns true`() {
        val ticketCode = "1234567"
        val exitTimeNotNull = Entry(
            ticketCode = ticketCode, exitTime = Instant.now())
        entityManager.persist(exitTimeNotNull)

        val exitTimeNull = Entry(
            ticketCode = ticketCode, exitTime = null)
        entityManager.persistAndFlush(exitTimeNull)

        assertTrue(entryRepository.isTicketCodeInUse(ticketCode))
    }

    @Test
    fun `test isTicketCodeInUse, when ticketCode is not in use, then returns false`() {
        val rfid = "1234567"
        val exitTimeNotNull = Entry(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persist(exitTimeNotNull)

        val exitTimeNull = Entry(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persistAndFlush(exitTimeNull)

        assertFalse(entryRepository.isTicketCodeInUse(rfid))
    }

    @Test
    fun `test isTicketCodeInUse, when ticketCode does not exist, then returns false`() {
        assertFalse(entryRepository.isTicketCodeInUse("12345"))
    }

    @Test
    fun `test markExited, when entry not exited, then updates exitTime`() {
        val ticketCode = 1234567890L

        val id = entityManager.persistAndGetId(
            Entry(ticketCode = ticketCode.toString()))
        entityManager.flush()
        val creationTime = Instant.now()

        entryRepository.markExited(ticketCode.toString())
        entityManager.clear()
        val updateTime = Instant.now()

        entityManager.find(Entry::class.java, id).run {
            assertThat(exitTime).isNotNull
            assertThat(exitTime).isAfterOrEqualTo(creationTime)
            assertThat(exitTime).isBeforeOrEqualTo(updateTime)
        }
    }
}