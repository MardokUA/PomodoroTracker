package com.gmail.laktionov.pomodorotracker.domain

import android.content.Context
import android.content.SharedPreferences

private const val TRACKER_PREFS = "tracker_simple_storage"

object DependencyManager {

    fun initViewModelFactory(applicationContext: Context): Unit = ViewModelFactory.initFactory(provideLocalSource(applicationContext))

    private fun provideLocalSource(applicationContext: Context): LocalSource {
        return LocalStorage(provideSharedPrefs(applicationContext))
    }

    private fun provideSharedPrefs(applicationContext: Context): SharedPreferences {
        return applicationContext.getSharedPreferences(TRACKER_PREFS, Context.MODE_PRIVATE)
    }
}