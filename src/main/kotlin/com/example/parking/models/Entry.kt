package com.example.parking.models

import org.hibernate.annotations.CreationTimestamp
import org.jetbrains.annotations.NotNull
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "entries")
open class Entry(
    @Id
    @GeneratedValue
    open var id: Long? = null,

    @CreationTimestamp
    @NotNull
    open var entryTime: Instant? = null,
    open var ticketCode: String?,   // TODO: Change to Long
    open var exitTime: Instant? = null,
)