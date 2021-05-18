package com.example.parking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class ParkingApplication

fun main(args: Array<String>) {
    runApplication<ParkingApplication>(*args)
}