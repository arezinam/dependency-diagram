package org.example.dependencydiagram.debouncer

import javafx.application.Platform
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.Assertions.*


class InputDebouncerTest {

    // We need to initialize JavaFX toolkit for the tests to work with Platform.runLater
    companion object {
        var jfxInitialized = false

        @BeforeAll
        @JvmStatic
        fun initJavaFX() {
            // Only attempt to initialize JavaFX once
            if (!jfxInitialized) {
                try {
                    Platform.startup {}
                    jfxInitialized = true
                } catch (e: Exception) {
                    System.err.println("JavaFX initialization failed: ${e.message}")
                }
            }
        }
    }

    private lateinit var debouncer: InputDebouncer
    private val shortDelay = 100L
    private val longDelay = 300L

    @BeforeEach
    fun setUp() {
        // Create a debouncer with a short delay for faster tests
        debouncer = InputDebouncer(shortDelay)
    }

    @AfterEach
    fun tearDown() {
        // Ensure we shut down the debouncer after each test
        debouncer.shutdown()
    }

    @Test
    @Timeout(10) // Set a timeout to prevent tests from hanging
    fun `test only last action is executed after debounce period`() {
        // Skip test if JavaFX initialization failed
        if (!jfxInitialized) return

        val counter = AtomicInteger(0)
        val latch = CountDownLatch(1)

        // Schedule multiple actions in quick succession
        debouncer.debounce { counter.set(1) }
        debouncer.debounce { counter.set(2) }
        debouncer.debounce {
            counter.set(3)
            latch.countDown() // Signal that the action has completed
        }

        // Wait for the action to be executed
        assertTrue(latch.await(longDelay * 2, TimeUnit.MILLISECONDS),
            "Action should execute within timeout period")

        // Only the last action should have been executed
        assertEquals(3, counter.get(), "Only the last debounced action should be executed")
    }

    @Test
    @Timeout(10)
    fun `test action is delayed by specified time`() {
        // Skip test if JavaFX initialization failed
        if (!jfxInitialized) return

        val executionTime = AtomicInteger(0)
        val startTime = System.currentTimeMillis()
        val latch = CountDownLatch(1)

        // Schedule an action
        debouncer.debounce {
            executionTime.set((System.currentTimeMillis() - startTime).toInt())
            latch.countDown()
        }

        // Wait for action to complete
        assertTrue(latch.await(longDelay * 2, TimeUnit.MILLISECONDS),
            "Action should execute within timeout period")

        // Verify the action was delayed by at least the specified delay
        assertTrue(executionTime.get() >= shortDelay,
            "Action should be delayed by at least $shortDelay ms, but was delayed by ${executionTime.get()} ms")
    }

    @Test
    @Timeout(10)
    fun `test rapid succession of events`() {
        // Skip test if JavaFX initialization failed
        if (!jfxInitialized) return

        val counter = AtomicInteger(0)
        val latch = CountDownLatch(1)

        // Create a debouncer with longer delay for this test
        val longerDebouncer = InputDebouncer(longDelay)

        try {
            // Simulate rapid events (like keystrokes)
            val executor = Executors.newSingleThreadScheduledExecutor()
            try {
                // Schedule 5 events, 50ms apart
                for (i in 1..5) {
                    executor.schedule({
                        longerDebouncer.debounce {
                            counter.incrementAndGet()
                            if (i == 5) latch.countDown()
                        }
                    }, (i * 50).toLong(), TimeUnit.MILLISECONDS)
                }

                // Wait for final action
                assertTrue(latch.await(longDelay * 4, TimeUnit.MILLISECONDS),
                    "Final action should execute within timeout period")

                // Should only have executed once despite 5 events
                assertEquals(1, counter.get(),
                    "Only one action should execute despite multiple rapid events")

            } finally {
                executor.shutdownNow()
            }
        } finally {
            longerDebouncer.shutdown()
        }
    }

    @Test
    @Timeout(10)
    fun `test spaced out events execute separately`() {
        // Skip test if JavaFX initialization failed
        if (!jfxInitialized) return

        val counter = AtomicInteger(0)
        val latch = CountDownLatch(2) // Expecting 2 executions

        // Create actions with a callback to count executions
        val action = {
            counter.incrementAndGet()
            latch.countDown()
        }

        // Schedule first action
        debouncer.debounce(action)

        // Wait longer than the debounce period
        Thread.sleep(shortDelay * 2)

        // Schedule second action after first should have executed
        debouncer.debounce(action)

        // Wait for both actions to complete
        assertTrue(latch.await(longDelay * 3, TimeUnit.MILLISECONDS),
            "Both actions should execute within timeout period")

        // Should have executed twice
        assertEquals(2, counter.get(),
            "Two separate events spaced apart should both execute")
    }

    @Test
    fun `test shutdown cancels pending actions`() {
        // Skip test if JavaFX initialization failed
        if (!jfxInitialized) return

        val executed = AtomicInteger(0)

        // Schedule an action
        debouncer.debounce { executed.incrementAndGet() }

        // Immediately shut down before it can execute
        debouncer.shutdown()

        // Wait longer than debounce period to ensure execution would have happened
        Thread.sleep(shortDelay * 2)

        // Action should not have executed
        assertEquals(0, executed.get(),
            "Action should not execute after shutdown")
    }
}