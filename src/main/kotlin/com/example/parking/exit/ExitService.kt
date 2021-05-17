package com.example.parking.exit

import com.example.parking.models.FinishedVisit
import com.example.parking.models.OngoingVisit
import com.example.parking.visit.InvalidTicketCodeException
import com.example.parking.util.ParkingFeeCalc
import com.example.parking.visit.FinishedVisitRepo
import com.example.parking.visit.OngoingVisitRepo
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ExitService(
    private val registeredVehicleRepository: RegisteredVehicleRepository,
    private val ongoingVisitRepo: OngoingVisitRepo,   //
    private val finishedVisitRepo: FinishedVisitRepo, //combine
    private val parkingFeeCalc: ParkingFeeCalc, //
    private val paymentService: PaymentService  //combine
) {
    fun exit(ticketCode: Long) {
        val ongoingVisit = ongoingVisitRepo.findByTicketCode(ticketCode.toString())
            ?: throw InvalidTicketCodeException(
                "The provided ticket code is not in use. Ticket code: $ticketCode")

        if (registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode)) {
            markOnGoingEntryAsFinished(ongoingVisit)
            return
        }

        val parkingFee = parkingFeeCalc.calculateFee(
            Instant.now().until(ongoingVisit.entryTime, ChronoUnit.MINUTES))

        if (parkingFee == 0.0) {
            markOnGoingEntryAsFinished(ongoingVisit)
            return
        }

        if (ongoingVisit.payments.isEmpty()) {
            throw UnservicedParkingBill()
        }

        if (paymentService.hasLatestPaymentExpired(ongoingVisit)) {
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
}

class UnservicedParkingBill(
    message: String? = null
) : Exception(message)