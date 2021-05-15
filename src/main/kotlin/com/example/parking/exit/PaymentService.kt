package com.example.parking.exit

import com.example.parking.models.Visit
import org.springframework.stereotype.Service

@Service
class PaymentService {
    fun hasLatestPaymentExpired(visit: Visit): Boolean {
        return true
    }
}