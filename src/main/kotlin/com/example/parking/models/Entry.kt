package com.example.parking.models

import org.hibernate.annotations.CreationTimestamp
import org.jetbrains.annotations.NotNull
import java.time.Instant
import javax.persistence.*

@Entity(name = "entries")
open class Entry(
    @Id
    @GeneratedValue
    @Column(name = "entry_id")
    open var id: Long? = null,

    @CreationTimestamp
    @NotNull
    open var entryTime: Instant = Instant.now(),
    open var ticketCode: String?,   // TODO: Change to Long
    open var exitTime: Instant? = null,

    @OneToMany(
        mappedBy = "entry", cascade = [CascadeType.ALL],
        orphanRemoval = true, targetEntity = Payment::class
    )
    open var payments: List<Payment> = listOf()
) {
    fun exited(): Boolean {
        return exitTime != null
    }
}