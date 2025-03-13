package org.example.dependencydiagram.renderer

import javafx.scene.image.Image
import java.io.ByteArrayInputStream

class JavaFXGraphRenderer(private val renderer: GraphRenderer) {
    fun renderGraph(
        graphDefinition: String,
        enabledVertices: Set<String>,
        onSuccess: (Image) -> Unit,
        onError: (Exception) -> Unit
    ) {
        renderer.renderGraph(
            graphDefinition,
            enabledVertices,
            { imageBytes -> onSuccess(Image(ByteArrayInputStream(imageBytes))) },
            onError
        )
    }

    fun shutdown() {
        renderer.shutdown()
    }
}