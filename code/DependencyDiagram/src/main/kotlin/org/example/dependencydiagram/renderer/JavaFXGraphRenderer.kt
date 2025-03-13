package org.example.dependencydiagram.renderer

import javafx.scene.image.Image
import java.io.ByteArrayInputStream

/**
 * Adapts the standard GraphRenderer to work with JavaFX Image objects.
 * Converts raw image bytes into JavaFX Image instances for display.
 */
class JavaFXGraphRenderer(private val renderer: GraphRenderer) {
    /**
     * Renders a graph and converts the result to a JavaFX Image.
     *
     * @param graphDefinition String defining the graph structure
     * @param enabledVertices Set of vertex IDs to include in the rendering
     * @param onSuccess Callback function that receives the JavaFX Image
     * @param onError Callback function that handles any exceptions
     */
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

    /**
     * Forwards shutdown call to the underlying renderer.
     */
    fun shutdown() {
        renderer.shutdown()
    }
}