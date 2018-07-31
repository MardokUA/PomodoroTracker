package com.gmail.laktionov.pomodorotracker.core

import android.arch.lifecycle.*
import android.content.SharedPreferences
import com.gmail.laktionov.pomodorotracker.domain.LocalSource
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.LinkedListChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SubscriptionReceiveChannel
import kotlinx.coroutines.experimental.channels.consume
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Simple implementation of [Job], witch consume lifecycle for further cancellation.
 *
 * @param lifecycle object that has an Android Lifecycle;
 * @see [LifecycleObserver], [Job]
 */
class AndroidJob(lifecycle: Lifecycle) : Job by Job(), LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() = cancel()
}

/**
 * Custom [ViewModel] witch take care about cancellation of all background task's, when it destroys.
 * It creates own [LifecycleRegistry] and dispatches [Lifecycle.Event] in init() and onCleared() methods.
 *
 * @param localSource simple local storage
 * @param uiContext [CoroutineContext], stored in variable for further easier testing;
 * @param bgContext [CoroutineContext], stored in variable for further easier testing;
 */
abstract class LifecycleViewModel(protected val localSource: LocalSource,
                                  protected val uiContext: CoroutineContext = UI,
                                  protected val bgContext: CoroutineContext = CommonPool) : ViewModel(), LifecycleOwner {

    private val registry: LifecycleRegistry = LifecycleRegistry(this)
    protected var androidJob = AndroidJob(registry)

    init {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun getLifecycle(): Lifecycle = registry

    override fun onCleared() {
        super.onCleared()
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}

/**
 * Connection engine between [LiveData] and Kotlin coroutines, provided by Dmytro Danylyk
 *
 * @see <a href="https://github.com/dmytrodanylyk/coroutines-arch/tree/master/example-live-data/src/main/java/com/kotlin/arch/example">Dmytro Danylyk example</a>
 */

class LiveDataChannel<T>(private val liveData: LiveData<T>)
    : LinkedListChannel<T?>(), SubscriptionReceiveChannel<T?>, Observer<T?>, LifecycleObserver {

    override fun onChanged(t: T?) {
        offer(t)
    }

    override fun afterClose(cause: Throwable?) = liveData.removeObserver(this)

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() = close()
}

fun <T> LiveData<T>.observeChannel(lifecycleOwner: LifecycleOwner): LiveDataChannel<T> {
    val channel = LiveDataChannel(this)
    observe(lifecycleOwner, channel)
    lifecycleOwner.lifecycle.addObserver(channel)
    return channel
}

fun <T> LiveData<T>.observeChannel(): LiveDataChannel<T> {
    val channel = LiveDataChannel(this)
    observeForever(channel)
    return channel
}

fun <E> ReceiveChannel<E>.launchConsumeEach(context: CoroutineContext = DefaultDispatcher,
                                            start: CoroutineStart = CoroutineStart.DEFAULT,
                                            parent: Job? = null,
                                            action: (E) -> Unit) = launch(context, start, parent) {
    consume {
        for (element in this) action(element)
    }
}