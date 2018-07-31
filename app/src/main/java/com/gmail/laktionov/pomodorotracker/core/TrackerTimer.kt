package com.gmail.laktionov.pomodorotracker.core

import android.os.CountDownTimer
import android.util.Log

class TrackerTimer(period: Long, step: Long = DEFAULT_STEP,
                   private val tickListener: (Long) -> Unit,
                   private val finishListener: () -> Unit) : CountDownTimer(period, step) {

    override fun onTick(p0: Long) {
        tickListener(p0)
        Log.d("TrackerTimer", "Tick: $p0")
    }

    override fun onFinish() = finishListener()

    fun stop() {
        super.cancel()
    }

    companion object {
        const val DEFAULT_STEP = 1_000L
    }
}