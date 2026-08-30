package com.autojournal.data.model

data class DTC(
    val code: String,          // P0300
    val description: String,   // "Случайные пропуски зажигания"
    val severity: String       // CRITICAL, WARNING, INFO
)