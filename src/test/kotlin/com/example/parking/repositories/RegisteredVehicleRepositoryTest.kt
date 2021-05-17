package com.example.parking.repositories

import com.example.parking.exit.RegisteredVehicleRepository
import com.example.parking.models.StaffVehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RegisteredVehicleRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager
    @Autowired
    lateinit var registeredVehicleRepository: RegisteredVehicleRepository

    @Test
    fun `test existsRegisteredVehicleByTicketCode, when registered-vehicle exists, then returns true`() {
        entityManager.persistAndFlush(
            StaffVehicle().apply {
                numberPlate = "qwerty"
                ticketCode = 1234567890L
            })

        val exists = registeredVehicleRepository
            .existsByTicketCode(1234567890L)

        assertThat(exists).isTrue
    }

    @Test
    fun `test existsRegisteredVehicleByTicketCode, when registered-vehicle does not exist, then returns false`() {
        val exists = registeredVehicleRepository
            .existsByTicketCode(1234L)

        assertThat(exists).isFalse
    }
}