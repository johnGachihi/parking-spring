package com.example.parking.exit

import com.example.parking.entry.EntryRepository
import com.example.parking.services.InvalidTicketCodeException
import com.example.parking.util.ParkingFeeCalc
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ExitUseCase(
    private val registeredVehicleRepository: RegisteredVehicleRepository,
    private val entryRepository: EntryRepository,
    private val parkingFeeCalc: ParkingFeeCalc,
) {
    fun exit(ticketCode: Long) {
        val entry = entryRepository.findFirstByTicketCodeAndExitTimeIsNull(ticketCode.toString())

        if (entry != null && ! entry.exited()) {
            throw InvalidTicketCodeException(
                "The provided ticket code is not in use. Ticket code: $ticketCode")
        }

        /*if (!entryRepository.isTicketCodeInUse(ticketCode.toString())) {
            throw InvalidTicketCodeException("The provided ticket code is not in use. Ticket code: $ticketCode")
        }*/

        if (registeredVehicleRepository.existsRegisteredVehicleByTicketCode(ticketCode)) {
            entryRepository.markExited(ticketCode.toString())
            return
        }


        val parkingFee = parkingFeeCalc.calculateFee(
            Instant.now().until(entry!!.entryTime, ChronoUnit.MINUTES)
        )

        if (parkingFee == 0.0) {
            entryRepository.markExited(ticketCode.toString())
            return
        }

//        if (entry!!.payments.isEmpty()) {
            // The check if there is a parking fee
//        }

    }
}