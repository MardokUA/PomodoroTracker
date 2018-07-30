package com.gmail.laktionov.pomodorotracker.core

import android.support.annotation.StringRes
import android.support.v7.widget.AppCompatButton
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor

private const val CAPACITY_SINGLE = 0

fun View.onClick(capacity: Int = CAPACITY_SINGLE, action: suspend () -> Unit) {
    val clickActor = actor<Unit>(UI, capacity) { for (event in channel) action() }
    setOnClickListener { clickActor.offer(Unit) }
}

fun AppCompatButton.changeState() = apply { isSelected = !isSelected }
fun AppCompatButton.swapText(@StringRes textOne: Int,
                             @StringRes textTwo: Int) = apply {
    text = if (isSelected) context.getString(textOne) else context.getString(textTwo)
}