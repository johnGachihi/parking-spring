package com.example.parking.models

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.*

@Entity(name = "payments")
open class Payment {
    @Id
    @GeneratedValue
    var id: Long? = null

    @ManyToOne
    var visit: Visit? = null

    @CreationTimestamp
    var madeAt: Instant? = null

    var amount: Int = 0
}