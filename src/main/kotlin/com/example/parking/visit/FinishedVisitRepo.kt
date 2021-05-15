package com.example.parking.visit

import com.example.parking.models.FinishedVisit
import org.springframework.data.jpa.repository.JpaRepository

interface FinishedVisitRepo : JpaRepository<FinishedVisit, Long>