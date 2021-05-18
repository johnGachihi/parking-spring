package com.example.parking.payment

import com.example.parking.models.ParkingFeeConfigModel
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ParkingFeeConfigRepo : JpaRepository<ParkingFeeConfigModel, Long> {
    @Cacheable("parking_fee_config")
    fun findByKey(key: String): ParkingFeeConfigModel?

    @Modifying
    @Query("update parking_fee_configuration p set p.value = :value where p.key = :key")
    @CacheEvict("parking_fee_config", allEntries = true)
    fun setProperty(@Param("key") key: String,
                    @Param("value") value: String)
}
