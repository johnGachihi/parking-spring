package com.example.parking.payment

import com.example.parking.models.ConfigLabel
import com.example.parking.util.Minutes
import org.springframework.stereotype.Service
import kotlin.IllegalStateException

@Service
class ParkingFeeConfig(
    private val parkingFeeConfigRepo: ParkingFeeConfigRepo
) {
    val paymentExpirationTime: Minutes?
        get() {
            val prop = getProperty("payment_expiration_time")
                ?: return null

            return try {
                Minutes(prop.toInt().toLong())
            } catch (e: NumberFormatException) {
                IllegalStateException(
                    "Invalid payment_expiration_time configuration provided: $prop. " +
                            "payment_expiration_time must be formatted as an integer. " +
                            "Null will be returned",
                    e
                ).printStackTrace()
                null
            }
        }

    fun getProperty(key: String): String? =
        parkingFeeConfigRepo.findByKey("${ConfigLabel.PARKING_FEE}_$key")?.value

    //TODO: WIP. Consider situation where property does not exist in datasource. Create new?
    fun setProperty(key: String, value: String) {
        parkingFeeConfigRepo.setProperty("${ConfigLabel.PARKING_FEE}_$key", value)
    }
}