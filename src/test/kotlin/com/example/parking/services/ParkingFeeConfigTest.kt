package com.example.parking.services

import com.example.parking.models.ConfigLabel
import com.example.parking.models.ParkingFeeConfigModel
import com.example.parking.payment.ParkingFeeConfig
import com.example.parking.payment.ParkingFeeConfigRepo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class ParkingFeeConfigTest {
    @Mock
    lateinit var parkingFeeConfigRepo: ParkingFeeConfigRepo

    @InjectMocks
    lateinit var parkingFeeConfig: ParkingFeeConfig

    @Test
    fun `test getProperty, calls parkingFeeConfigRepo appropriately`() {
        parkingFeeConfig.getProperty("ble")

        verify(parkingFeeConfigRepo, times(1))
            .findByKey("${ConfigLabel.PARKING_FEE}_ble")
    }

    @Test
    fun `test getProperty, when property exists, then returns value appropriately`() {
        `when`(parkingFeeConfigRepo.findByKey(anyString()))
            .thenReturn(ParkingFeeConfigModel().apply { value = "value" })

        val value = parkingFeeConfig.getProperty("key")

        assertThat(value).isEqualTo("value")
    }

    @Test
    fun `test getProperty, when property does not exist, then returns null`() {
        `when`(parkingFeeConfigRepo.findByKey(anyString()))
            .thenReturn(null)

        val value = parkingFeeConfig.getProperty("key")

        assertThat(value).isNull()
    }

    @Test
    fun `test setProperty, calls ParkingFeeConfigRepo appropriately`() {
        parkingFeeConfig.setProperty("key", "value")

        verify(parkingFeeConfigRepo, times(1))
            .setProperty("${ConfigLabel.PARKING_FEE}_key", "value")
    }

    @Nested
    @DisplayName("Test paymentExpiration property")
    inner class PaymentExpirationTest {
        @Test
        fun `when property does not exist in datasource, then returns null`() {
            `when`(parkingFeeConfigRepo.findByKey(anyString()))
                .thenReturn(null)

            assertThat(parkingFeeConfig.paymentExpirationTime).isNull()
        }

        @Test
        fun `when property exists in datasource, then returns it`() {
            `when`(parkingFeeConfigRepo.findByKey(anyString()))
                .thenReturn(
                    ParkingFeeConfigModel().apply {
                        value = "15"
                    })

            assertThat(parkingFeeConfig.paymentExpirationTime!!.minutes).isEqualTo(15)
        }

        @Test
        fun `when property is invalid (not Integer), then returns null`() {
            `when`(parkingFeeConfigRepo.findByKey(anyString()))
                .thenReturn(
                    ParkingFeeConfigModel().apply {
                        value = "invalid value"
                    })

            assertThat(parkingFeeConfig.paymentExpirationTime).isNull()
        }
    }
}