package com.gmail.laktionov.pomodorotracker.domain

import android.content.SharedPreferences
import com.gmail.laktionov.pomodorotracker.domain.LocalSource.Companion.IS_KEEP_SCREEN
import com.gmail.laktionov.pomodorotracker.domain.LocalSource.Companion.IS_REVERS
import com.gmail.laktionov.pomodorotracker.domain.LocalSource.Companion.TIMER_ACTION
import com.gmail.laktionov.pomodorotracker.domain.LocalSource.Companion.TIMER_REST

class LocalStorage(override val sharedPreferences: SharedPreferences) : LocalSource {

    override fun getUserSettings(): TimerSettings {
        val actionTimerTime = sharedPreferences.getLong(TIMER_ACTION, TimerValues.DEFAULT_TIMER_VALUE)
        val restTimerTime = sharedPreferences.getLong(TIMER_REST, TimerValues.DEFAULT_TIMER_VALUE)

        return TimerSettings().apply {
            actionValues.seconds = actionValues.formatToSeconds(actionTimerTime)
            actionValues.minutes = actionValues.formatToMinutes(actionTimerTime)
            restValues.seconds = actionValues.formatToMinutes(restTimerTime)
            actionValues.minutes = actionValues.formatToMinutes(restTimerTime)
            isKeepScreen = sharedPreferences.getBoolean(IS_KEEP_SCREEN, false)
            isReverse = sharedPreferences.getBoolean(IS_REVERS, false)
        }
    }

    override fun saveUserSetting(timerSettings: TimerSettings) {
        sharedPreferences.edit()
                .putLong(TIMER_ACTION, timerSettings.actionValues.getPeriod())
                .putLong(TIMER_REST, timerSettings.restValues.getPeriod())
                .putBoolean(IS_KEEP_SCREEN, timerSettings.isKeepScreen)
                .putBoolean(IS_REVERS, timerSettings.isReverse)
                .apply()
    }

    companion object {
        const val EMPTY_STRING = ""
    }
}