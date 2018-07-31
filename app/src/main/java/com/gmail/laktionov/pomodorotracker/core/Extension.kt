package com.gmail.laktionov.pomodorotracker.core

import android.support.annotation.StringRes
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.text.Editable
import android.text.TextWatcher
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

fun AppCompatTextView.addChangeObserver(block: (String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {} //NOT USED
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {} //NOT USED
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = block(p0.toString())
    })
}

fun Long.formatValue(): String = if (this in 0..9) "0$this" else "$this"
fun Long.toMinutes(): Long = this / 1000 / 60
fun Long.toSeconds(): Long = this / 1000 % 60
