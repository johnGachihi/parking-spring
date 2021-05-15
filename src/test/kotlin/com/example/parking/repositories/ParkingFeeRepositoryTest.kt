package com.example.parking.repositories

import com.example.parking.exit.ParkingFeeRepository
import com.example.parking.models.ParkingTimeRange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ParkingFeeRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var parkingFeeRepository: ParkingFeeRepository

    @Test
    fun `test findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine when greaterThan`() {
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 30 })
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 20 })
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 40 })
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 10 })

        entityManager.flush()

        val parkingTimeRange = parkingFeeRepository
            .findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(15)

        assertThat(parkingTimeRange?.pointOnTimeLine).isEqualTo(20)
    }

    @Test
    fun `test findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine when Equal`() {
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 30 })
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 20 })
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 40 })
        entityManager.persist(
            ParkingTimeRange().apply { pointOnTimeLine = 10 })

        entityManager.flush()

        val parkingTimeRange = parkingFeeRepository
            .findFirstByPointOnTimeLineGreaterThanEqualOrderByPointOnTimeLine(30)

        assertThat(parkingTimeRange?.pointOnTimeLine).isEqualTo(30)
    }
}