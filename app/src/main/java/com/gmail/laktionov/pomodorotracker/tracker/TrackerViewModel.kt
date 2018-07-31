package com.gmail.laktionov.pomodorotracker.tracker

import android.arch.lifecycle.MutableLiveData
import com.gmail.laktionov.pomodorotracker.core.*
import com.gmail.laktionov.pomodorotracker.domain.LocalSource
import com.gmail.laktionov.pomodorotracker.domain.TimerAction
import com.gmail.laktionov.pomodorotracker.domain.TimerSettings
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import kotlin.properties.Delegates

class TrackerViewModel(localSource: LocalSource,
                       val actionTimerSub: MutableLiveData<String> = MutableLiveData(),
                       val restTimerSub: MutableLiveData<String> = MutableLiveData(),
                       val buttuonStateSub: MutableLiveData<Boolean> = MutableLiveData()) : LifecycleViewModel(localSource) {

    private var timerJob: Job = Job(androidJob)

    private var timer: TrackerTimer? = null
    private lateinit var settings: TimerSettings
    private var actionTimeValue: Long by Delegates.observable(0L) { _, _, newValue -> validateTimer(newValue) }

    private fun validateTimer(newValue: Long) = buttuonStateSub.postValue(newValue != 0L)

    private var restTimeValue: Long = 0L

    private val tickHandler: (Long) -> Unit = { leftTime -> timerUpdateChannel.offer(leftTime) }
    private val finishHandler: () -> Unit = { resetTimer() }
    private val timerUpdateChannel = actor<Long> { channel.consumeEach { updateTimer(it) } }

    fun getSettings(block: (TimerSettings) -> Unit) {
        launch(timerJob) {
            settings = localSource.getUserSettings()
            actionTimeValue = settings.actionValues.getPeriod()
            restTimeValue = settings.restValues.getPeriod()
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
        timer = TrackerTimer(actionTimeValue,
                tickListener = tickHandler,
                finishListener = finishHandler)

        timer!!.start()
    }

    private fun stopTimer() {
        timer?.stop()
        timer = null
    }

    private fun updateTimer(leftTime: Long) {
        launch(timerJob) {
            actionTimeValue = leftTime
            val message = let {
                val seconds = leftTime.toSeconds()
                val minutes = leftTime.toMinutes()
                formatValues(minutes, seconds)
            }
            actionTimerSub.postValue(message)
        }
    }

    private fun resetTimer() {
        actionTimerSub.postValue(null)
        actionTimerSub.postValue(settings.actionValues.getFormattedValues())
        actionTimeValue = settings.actionValues.getPeriod()
    }


    private fun formatValues(minutes: Long, seconds: Long) = "${minutes.formatValue()}:${seconds.formatValue()}"

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
