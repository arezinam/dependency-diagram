package org.example.dependencydiagram.model

import javafx.beans.property.SimpleBooleanProperty

class Vertex(val id: String) {
    val enabledProperty = SimpleBooleanProperty(true)

    var enabled: Boolean
        get() = enabledProperty.get()
        set(value) = enabledProperty.set(value)

    override fun toString(): String = id
}