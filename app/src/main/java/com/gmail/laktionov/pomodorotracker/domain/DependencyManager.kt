package com.gmail.laktionov.pomodorotracker.domain

import android.content.Context

private const val TRACKER_PREFS = "tracker_simple_storage"

object DependencyManager {

    fun initViewModelFactory(applicationContext: Context) {
        ViewModelFactory.initFactory(provideSharedPrefs(applicationContext))
    }

    private fun provideSharedPrefs(applicationContext: Context) = applicationContext.getSharedPreferences(TRACKER_PREFS, Context.MODE_PRIVATE)
}