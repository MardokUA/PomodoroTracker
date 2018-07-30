package com.gmail.laktionov.pomodorotracker.domain

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.gmail.laktionov.pomodorotracker.tracker.TrackerViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
        private val sharedPreferences: SharedPreferences) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(TrackerViewModel::class.java) -> TrackerViewModel(sharedPreferences)
                else -> throwError(this)
            }
        } as T
    }

    private fun <T : ViewModel?> throwError(modelClass: Class<T>): Nothing {
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {

        lateinit var INSTANCE: ViewModelFactory

        fun initFactory(sharedPreferences: SharedPreferences) {
            INSTANCE = ViewModelFactory(sharedPreferences)
        }
    }
}