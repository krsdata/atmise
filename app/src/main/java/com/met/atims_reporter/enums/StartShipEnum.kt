package com.met.atims_reporter.enums

enum class StartShipEnum(private val title: String) {
    STATE("State"),
    OPERATION("Operation"),
    ZONE("Beat/Zone"),
    SHIFT_TIME("Time");

    fun getTitle(): String {
        return title
    }
}