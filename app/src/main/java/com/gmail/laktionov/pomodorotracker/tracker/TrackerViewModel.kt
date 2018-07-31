package com.gmail.laktionov.pomodorotracker.tracker

import android.arch.lifecycle.MutableLiveData
import com.gmail.laktionov.pomodorotracker.core.LifecycleViewModel
import com.gmail.laktionov.pomodorotracker.core.TrackerTimer
import com.gmail.laktionov.pomodorotracker.core.toMinutes
import com.gmail.laktionov.pomodorotracker.core.toSeconds
import com.gmail.laktionov.pomodorotracker.domain.LocalSource
import com.gmail.laktionov.pomodorotracker.domain.TimerAction
import com.gmail.laktionov.pomodorotracker.domain.TimerSettings
import com.gmail.laktionov.pomodorotracker.domain.ValueHolder
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach

class TrackerViewModel(localSource: LocalSource,
                       val actionTimerSub: MutableLiveData<String> = MutableLiveData(),
                       val restTimerSub: MutableLiveData<String> = MutableLiveData()) : LifecycleViewModel(localSource) {

    private var timerJob: Job = Job(androidJob)

    private var timer: TrackerTimer? = null
    private lateinit var settings: TimerSettings
    private lateinit var valueHolder: ValueHolder

    private val tickHandler: (Long) -> Unit = { leftTime -> timerUpdateChannel.offer(leftTime) }
    private val finishHandler: () -> Unit = { resetTimer() }
    private val timerUpdateChannel = actor<Long> { channel.consumeEach { updateTimer(it) } }

    fun getSettings(block: (TimerSettings) -> Unit) {
        launch {
            settings = localSource.getUserSettings()
            valueHolder = ValueHolder(actionValues = settings.actionValues.copy(), restValues = settings.restValues.copy())
            withContext(UI) { block(settings) }
        }
    }

    fun startTimer(timerAction: TimerAction) = startJob(timerAction)

    private fun startJob(timerAction: TimerAction) {
        when (timerAction) {
            TimerAction.START -> starTimer()
            TimerAction.STOP -> stopTimer()
        }
    }

    private fun starTimer() {
        timer = TrackerTimer(valueHolder.actionValues.getPeriod(),
                tickListener = tickHandler,
                finishListener = finishHandler)

        timer!!.start()
    }

    private fun stopTimer() {
        timer?.stop()
        timer = null
    }

    private fun updateTimer(leftTime: Long) {
        launch {
            val message = with(valueHolder.actionValues) {
                seconds = leftTime.toSeconds()
                minutes = leftTime.toMinutes()
                getFormattedValues()
            }
            actionTimerSub.postValue(message)
        }
    }

    private fun resetTimer() {
        actionTimerSub.postValue(null)
        actionTimerSub.postValue(valueHolder.actionValues.getFormattedValues())
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
