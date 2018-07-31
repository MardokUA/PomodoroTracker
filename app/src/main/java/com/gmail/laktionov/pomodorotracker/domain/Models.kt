package com.gmail.laktionov.pomodorotracker.domain

import com.gmail.laktionov.pomodorotracker.core.formatValue
import com.gmail.laktionov.pomodorotracker.core.toMinutes
import com.gmail.laktionov.pomodorotracker.core.toSeconds

enum class TimerAction { START, STOP }

data class TimerValues(var minutes: Long = DEFAULT_TIMER_VALUE,
                       var seconds: Long = DEFAULT_TIMER_VALUE) {

    fun getFormattedValues() = formatValues(minutes, seconds)
    fun getPeriod() = (seconds * 1000) + (minutes * 60 * 1000)

    fun formatToMinutes(leftTime: Long) = leftTime.toMinutes()
    fun formatToSeconds(leftTime: Long) = leftTime.toSeconds()

    companion object {
        fun formatValues(minutes: Long, seconds: Long) = "${minutes.formatValue()}:${seconds.formatValue()}"
        const val DEFAULT_TIMER_VALUE = 0L
    }
}

open class ValueHolder(val actionValues: TimerValues = TimerValues(),
                       val restValues: TimerValues = TimerValues())

data class TimerSettings(var isReverse: Boolean = false,
                         var isKeepScreen: Boolean = false) : ValueHolder()