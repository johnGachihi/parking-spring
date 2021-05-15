package com.example.parking.repositories

import com.example.parking.visit.VisitRepository
import com.example.parking.models.Visit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.Instant

@DataJpaTest
class VisitRepositoryTest @Autowired constructor( // TODO: Make DI style uniform across tests
    val entityManager: TestEntityManager,
    val visitRepository: VisitRepository
) {
    /*@Test
    fun `test isTicketCodeInUse when ticketCode is in use, then returns true`() {
        val ticketCode = "1234567"
        val exitTimeNotNull = Visit().apply {
            this.ticketCode = ticketCode
            this.exitTime = Instant.now()
        }
        entityManager.persist(exitTimeNotNull)

        val exitTimeNull = Visit(
            ticketCode = ticketCode, exitTime = null)
        entityManager.persistAndFlush(exitTimeNull)

        assertTrue(visitRepository.isTicketCodeInUse(ticketCode))
    }*/

    /*@Test
    fun `test isTicketCodeInUse, when ticketCode is not in use, then returns false`() {
        val rfid = "1234567"
        val exitTimeNotNull = Visit(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persist(exitTimeNotNull)

        val exitTimeNull = Visit(
            ticketCode = rfid, exitTime = Instant.now())
        entityManager.persistAndFlush(exitTimeNull)

        assertFalse(visitRepository.isTicketCodeInUse(rfid))
    }*/

    @Test
    fun `test isTicketCodeInUse, when ticketCode does not exist, then returns false`() {
        assertFalse(visitRepository.isTicketCodeInUse("12345"))
    }

    /*@Test
    fun `test markExited, when entry not exited, then updates exitTime`() {
        val ticketCode = 1234567890L

        val id = entityManager.persistAndGetId(
            Visit(ticketCode = ticketCode.toString()))
        entityManager.flush()
        val creationTime = Instant.now()

        visitRepository.markExited(ticketCode.toString())
        entityManager.clear()
        val updateTime = Instant.now()

        entityManager.find(Visit::class.java, id).run {
            assertThat(exitTime).isNotNull
            assertThat(exitTime).isAfterOrEqualTo(creationTime)
            assertThat(exitTime).isBeforeOrEqualTo(updateTime)
        }
    }*/

    /*@Test
    fun `test findLatestByTicketCode, when entries exist, then returns latest`() {
        val ticketCode = 1234567890L

        entityManager.persist(
            Visit(ticketCode = ticketCode.toString()))
        entityManager.persist(
            Visit(ticketCode = ticketCode.toString()))
        val latestEntryId = entityManager.persistAndGetId(
            Visit(ticketCode = ticketCode.toString()))
        entityManager.flush()

        val actualEntry = visitRepository.findFirstByTicketCodeAndExitTimeIsNull(ticketCode.toString())

        assertThat(actualEntry).isNotNull
        assertThat(actualEntry!!.id).isEqualTo(latestEntryId)
    }*/

    /*@Test
    fun `test findLatestByTicketCode, when entries do not exist, then returns null`() {
        assertThat(
            visitRepository.findFirstByTicketCodeAndExitTimeIsNull("1234567890")
        ).isNull()
    }*/
}