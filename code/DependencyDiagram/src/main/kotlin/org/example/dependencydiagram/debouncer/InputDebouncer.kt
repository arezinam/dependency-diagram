package org.example.dependencydiagram.debouncer

import javafx.application.Platform
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class InputDebouncer(
    private val delayMillis: Long,
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
) : Debouncer {
    private var scheduledFuture: ScheduledFuture<*>? = null

    override fun debounce(action: () -> Unit) {
        // Cancel any previous scheduled task
        scheduledFuture?.cancel(false)

        // Schedule the new task to run after the debounce delay
        scheduledFuture = scheduler.schedule({
            Platform.runLater(action)
        }, delayMillis, TimeUnit.MILLISECONDS)
    }

    override fun shutdown() {
        scheduledFuture?.cancel(false)
        scheduler.shutdown()
    }
}