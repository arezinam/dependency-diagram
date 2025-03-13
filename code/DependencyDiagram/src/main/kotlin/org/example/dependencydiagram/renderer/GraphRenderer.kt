package org.example.dependencydiagram.renderer

interface GraphRenderer {
    fun renderGraph(
        graphDefinition: String,
        enabledVertices: Set<String>,
        onSuccess: (ByteArray) -> Unit,
        onError: (Exception) -> Unit
    )

    fun shutdown()
}