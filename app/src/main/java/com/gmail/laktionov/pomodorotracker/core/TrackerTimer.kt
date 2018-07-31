package com.gmail.laktionov.pomodorotracker.core

import android.os.CountDownTimer
import android.util.Log

class TrackerTimer(val period: Long, step: Long = DEFAULT_STEP,
                   private val block: (Long) -> Unit) : CountDownTimer(period, step) {

    override fun onTick(p0: Long) {
        block(p0)
        Log.d("TrackerTimer", "Tick: $p0")
    }

    override fun onFinish() = block(period)

    fun stop() {
        super.cancel()
    }

    companion object {
        private const val DEFAULT_STEP = 1_000L
    }
}