package com.gmail.laktionov.pomodorotracker.domain

import android.content.SharedPreferences

interface LocalSource {
    val sharedPreferences: SharedPreferences

    fun getUserSettings(): TimerSettings

    fun saveUserSetting(timerSettings: TimerSettings)

    companion object {
        const val TIMER_ACTION = "timer_action_value"
        const val TIMER_REST = "timer_action_value"
        const val IS_KEEP_SCREEN = "is_keep_screen"
        const val IS_REVERS = "is_reverse"
    }
}