package com.example.parking.exit

import com.example.parking.models.FinishedVisit
import com.example.parking.models.OngoingVisit
import com.example.parking.models.Payment
import com.example.parking.util.Minutes
import com.example.parking.visit.InvalidTicketCodeException
import com.example.parking.visit.FinishedVisitRepo
import com.example.parking.visit.OngoingVisitRepo
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

@Service
class ExitService(
    private val registeredVehicleRepository: RegisteredVehicleRepository,
    private val ongoingVisitRepo: OngoingVisitRepo,   //
    private val finishedVisitRepo: FinishedVisitRepo, //combine?
    private val paymentService: PaymentService
) {
    fun exit(ticketCode: Long) {
        val ongoingVisit = ongoingVisitRepo.findByTicketCode(ticketCode)
            ?: throw InvalidTicketCodeException(
                "The provided ticket code is not in use. Ticket code: $ticketCode")

        if (registeredVehicleRepository.existsByTicketCode(ticketCode)) {
            markOnGoingEntryAsFinished(ongoingVisit)
            return
        }

        val parkingFee = paymentService.calculateFee(
            getTimeOfStay(ongoingVisit))

        if (parkingFee == 0.0) {
            markOnGoingEntryAsFinished(ongoingVisit)
            return
        }

        if (ongoingVisit.payments.isEmpty()) {
            throw UnservicedParkingBill()
        }

        val latestPayment = getLatestPayment(ongoingVisit)!!
        if (paymentService.paymentExpired(latestPayment)) {
            throw UnservicedParkingBill("Latest payment expired")
        }

        markOnGoingEntryAsFinished(ongoingVisit)
    }

    private fun markOnGoingEntryAsFinished(ongoingVisit: OngoingVisit) {
        val finishedVisit = FinishedVisit().apply {
            entryTime = ongoingVisit.entryTime
            this.ticketCode = ongoingVisit.ticketCode
            this.payments = payments
        }
        finishedVisitRepo.save(finishedVisit)

        ongoingVisitRepo.delete(ongoingVisit)
    }

    private fun getTimeOfStay(ongoingVisit: OngoingVisit): Minutes =
        Minutes(Instant.now().until(ongoingVisit.entryTime, ChronoUnit.MINUTES).absoluteValue) // TODO: Added .absoluteValue. See what was happening before

    private fun getLatestPayment(ongoingVisit: OngoingVisit): Payment? =
        ongoingVisit.payments.maxByOrNull { it.madeAt }
}

class UnservicedParkingBill(
    message: String? = null
) : Exception(message)