package com.example.parking.visit

import com.example.parking.models.Visit
import org.springframework.data.jpa.repository.JpaRepository

interface VisitRepository: JpaRepository<Visit, Long>