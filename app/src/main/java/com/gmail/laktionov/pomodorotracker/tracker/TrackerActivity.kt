package com.gmail.laktionov.pomodorotracker.tracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gmail.laktionov.pomodorotracker.R
import com.gmail.laktionov.pomodorotracker.core.changeState
import com.gmail.laktionov.pomodorotracker.core.swapText
import com.gmail.laktionov.pomodorotracker.domain.TimerAction
import com.gmail.laktionov.pomodorotracker.domain.ViewModelFactory
import kotlinx.android.synthetic.main.activity_pomodoro.*

class TrackerActivity : AppCompatActivity() {

    private lateinit var viewModel: TrackerViewModel

    private val start = TimerAction.START
    private val stop = TimerAction.STOP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)
        viewModel = ViewModelProviders.of(this, ViewModelFactory.INSTANCE).get(TrackerViewModel::class.java)

        setupView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.timerSub.observe(this, Observer { consumeValue(it) })
    }

    private fun setupView() {
        with(timerStartStopButton) {
            isSelected = false
            setOnClickListener { viewModel.startTimer(swapAction()) }
        }
    }

    private fun swapAction(): TimerAction {
        timerStartStopButton.changeState().swapText(R.string.button_stop_text, R.string.button_start_text)
        return if (timerStartStopButton.isSelected) start else stop
    }

    private fun consumeValue(it: String?) = it?.let { timerView.text = it }
}
