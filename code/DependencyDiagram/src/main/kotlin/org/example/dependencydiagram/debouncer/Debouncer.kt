package org.example.dependencydiagram.debouncer

interface Debouncer {
    fun debounce(action: () -> Unit)
    fun shutdown()
}