package com.gmail.laktionov.pomodorotracker.domain

import com.gmail.laktionov.pomodorotracker.core.formatValue

enum class TimerAction { START, STOP }

data class TimerValues(var minutes: Long = DEFAULT_TIMER_VALUE,
                       var seconds: Long = DEFAULT_TIMER_VALUE) {

    fun getFormattedValues() = "${minutes.formatValue()}:${seconds.formatValue()}"

    fun getPeriod() = (seconds * 1000) + (minutes * 60 * 1000)

    companion object {
        const val DEFAULT_TIMER_VALUE = 0L
    }
}
