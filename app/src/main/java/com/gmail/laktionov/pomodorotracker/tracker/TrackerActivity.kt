package com.gmail.laktionov.pomodorotracker.tracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gmail.laktionov.pomodorotracker.R
import com.gmail.laktionov.pomodorotracker.domain.ViewModelFactory

class TrackerActivity : AppCompatActivity() {

    private lateinit var viewModel: TrackerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)
        viewModel = ViewModelFactory.INSTANCE.create(TrackerViewModel::class.java)
    }
}
