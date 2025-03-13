package org.example.dependencydiagram.debouncer

import javafx.application.Platform
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * A debouncer implementation that delays executing an action until after user input has stopped.
 *
 * Used in this application to prevent excessive processing during rapid user input events:
 * - Prevents the expensive graph operations from being called due to rapid user input
 *
 * @param delayMillis How long to wait after the last event before executing (typically 250-500ms)
 * @param scheduler Thread scheduler (default works for most cases)
 */
class InputDebouncer(
    private val delayMillis: Long,
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
) : Debouncer {
    private var scheduledFuture: ScheduledFuture<*>? = null

    override fun debounce(action: () -> Unit) {
        // Cancel previous pending action if it exists
        scheduledFuture?.cancel(false)

        // Schedule the new action to run after the delay
        scheduledFuture = scheduler.schedule({
            Platform.runLater(action)  // Ensures UI updates happen on JavaFX thread
        }, delayMillis, TimeUnit.MILLISECONDS)
    }

    override fun shutdown() {
        scheduledFuture?.cancel(false)
        scheduler.shutdown()
    }
}