package com.gmail.laktionov.pomodorotracker

import android.app.Application
import com.gmail.laktionov.pomodorotracker.domain.DependencyManager

class PomodoroApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DependencyManager.initViewModelFactory(applicationContext)
    }
}