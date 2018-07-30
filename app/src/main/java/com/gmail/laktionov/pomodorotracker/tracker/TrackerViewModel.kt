package com.gmail.laktionov.pomodorotracker.tracker

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.util.Log
import com.gmail.laktionov.pomodorotracker.core.Constants.VIEW_MODEL_TAG
import com.gmail.laktionov.pomodorotracker.core.LifecycleViewModel
import com.gmail.laktionov.pomodorotracker.domain.TimerAction
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class TrackerViewModel(sharedPreferences: SharedPreferences) : LifecycleViewModel(sharedPreferences) {

    val timerSub: MutableLiveData<String> = MutableLiveData()
    private val startTimerActor = actor<TimerAction> { channel.consumeEach { startJob(it) } }

    private var timerJob = Job(androidJob)
    private val timer: Array<Int> = Array(20) { it + 1 }

    fun startTimer(timerAction: TimerAction) = startTimerActor.offer(timerAction)

    private fun startJob(timerAction: TimerAction) {
        when (timerAction) {
            TimerAction.START -> timerJob = starTimer()
            TimerAction.STOP -> timerJob.cancel()
        }
    }

    private fun starTimer() = launch {
        for (value in timer) {
            delay(500, TimeUnit.MILLISECONDS)
            timerSub.postValue(value.toString())
            Log.d(VIEW_MODEL_TAG, "Value posted: $value")
        }
    }
}
