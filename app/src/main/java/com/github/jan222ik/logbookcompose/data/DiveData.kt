package com.github.jan222ik.logbookcompose.data

import java.time.LocalDate

data class DiveData(
    val diveNumber: Int = 0,
    val depthMAX: Double = 0.0,
    val depthAVG: Double = 0.0,
    val date: LocalDate = LocalDate.MIN,
    val duration: Int = 0,
    val spotname: String = "Unknown"
)
