package com.gmail.laktionov.pomodorotracker.domain

import com.gmail.laktionov.pomodorotracker.core.formatValue
import com.gmail.laktionov.pomodorotracker.core.toMinutes

enum class TimerAction { START, STOP }

data class TimerValues(var minutes: Long = DEFAULT_TIMER_VALUE,
                       var seconds: Long = DEFAULT_TIMER_VALUE) {

    fun getFormattedValues() = "${minutes.formatValue()}:${seconds.formatValue()}"
    fun getPeriod() = (seconds * 1000) + (minutes * 60 * 1000)

    fun formatToMinutes(minutes: Long) = minutes.toMinutes()
    fun formatToSeconds(seconds: Long) = seconds.toMinutes()

    companion object {
        const val DEFAULT_TIMER_VALUE = 0L
    }
}

open class ValueHolder(val actionValues: TimerValues = TimerValues(),
                       val restValues: TimerValues = TimerValues())

data class TimerSettings(var isReverse: Boolean = false,
                         var isKeepScreen: Boolean = false) : ValueHolder()