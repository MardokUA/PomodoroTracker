package com.gmail.laktionov.pomodorotracker.tracker

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import com.gmail.laktionov.pomodorotracker.core.LifecycleViewModel
import com.gmail.laktionov.pomodorotracker.core.TrackerTimer
import com.gmail.laktionov.pomodorotracker.domain.TimerAction
import com.gmail.laktionov.pomodorotracker.domain.TimerValues
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach

class TrackerViewModel(sharedPreferences: SharedPreferences,
                       val timerSub: MutableLiveData<String> = MutableLiveData()) : LifecycleViewModel(sharedPreferences) {

    private var timerJob: Job = Job(androidJob)

    private var timer: TrackerTimer? = null
    private val timerValues: TimerValues = TimerValues(1, 30)

    private val tickHandler: (Long) -> Unit = { leftTime -> timerUpdateChannel.offer(leftTime) }
    private val timerUpdateChannel = actor<Long> { channel.consumeEach { updateTimer(it) } }

    fun startTimer(timerAction: TimerAction) = startJob(timerAction)

    private fun startJob(timerAction: TimerAction) {
        when (timerAction) {
            TimerAction.START -> starTimer()
            TimerAction.STOP -> stopTimer()
        }
    }

    private fun starTimer() {
        timer = TrackerTimer(timerValues.getPeriod(), block = tickHandler)
        timer!!.start()
    }

    private fun stopTimer() {
        timer?.stop()
        timer = null
    }

    private fun updateTimer(leftTime: Long) {
        val message = let {
            timerValues.seconds = leftTime / 1000
            timerValues.minutes = leftTime / (60 * 1000)
            timerValues.getFormattedValues()
        }
        timerSub.postValue(message)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
