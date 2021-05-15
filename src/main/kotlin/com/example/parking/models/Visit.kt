package com.example.parking.models

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "entries")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class Visit {
    @Id
    @GeneratedValue
    open var id: Long? = null

    @NotNull
    @CreationTimestamp
    open var entryTime: Instant = Instant.now()

    @NotNull
    open var ticketCode: String = ""   // TODO: Change to Long

    @OneToMany(
        mappedBy = "visit", cascade = [CascadeType.ALL],
        orphanRemoval = true, targetEntity = Payment::class)
    open var payments: List<Payment> = listOf()
}

@Entity(name = "OngoingVisit")
open class OngoingVisit : Visit()

@Entity(name = "FinishedVisit")
open class FinishedVisit : Visit() {
    @CreationTimestamp
    open var exitTime: Instant? = null
}