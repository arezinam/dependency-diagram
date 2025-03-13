package org.example.dependencydiagram.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue

class Vertex(val id: String) {
    private val _enabledProperty = SimpleBooleanProperty(true)

    // This provides the observable property for UI binding
    fun enabledProperty(): ObservableValue<Boolean> = _enabledProperty

    // This provides the direct boolean access for standard logic
    var enabled: Boolean
        get() = _enabledProperty.get()
        set(value) = _enabledProperty.set(value)

    override fun toString(): String = id
}