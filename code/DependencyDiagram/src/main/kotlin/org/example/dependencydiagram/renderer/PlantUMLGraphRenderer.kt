package org.example.dependencydiagram.renderer

import javafx.application.Platform
import org.example.dependencydiagram.generator.ImageGenerator
import org.example.dependencydiagram.generator.PlantUMLCodeGenerator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Implements the GraphRenderer interface using PlantUML for diagram generation.
 * This class coordinates the process of converting graph definitions into PlantUML code
 * and then into image bytes, handling the asynchronous execution of these operations.
 */
class PlantUMLGraphRenderer(
    private val codeGenerator: PlantUMLCodeGenerator,
    private val imageGenerator: ImageGenerator,
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
) : GraphRenderer {

    /**
     * Renders a graph by converting the graph definition to PlantUML code and then to an image.
     * This method executes asynchronously on a background thread to avoid blocking the UI.
     *
     * @param graphDefinition String defining the graph structure
     * @param enabledVertices Set of vertex IDs to include in the rendering
     * @param onSuccess Callback function that receives the generated image as a byte array
     * @param onError Callback function that handles any exceptions during the rendering process
     */
    override fun renderGraph(
        graphDefinition: String,
        enabledVertices: Set<String>,
        onSuccess: (ByteArray) -> Unit,
        onError: (Exception) -> Unit
    ) {
        executorService.submit {
            try {
                // Generate PlantUML code from the graph definition and enabled vertices
                val plantUmlCode = codeGenerator.generatePlantUmlCode(graphDefinition, enabledVertices)

                // Convert the PlantUML code to an image
                val imageBytes = imageGenerator.generateImage(plantUmlCode)

                // Run the success callback on the JavaFX application thread
                Platform.runLater { onSuccess(imageBytes) }
            } catch (e: Exception) {
                // Run the error callback on the JavaFX application thread if an exception occurs
                Platform.runLater { onError(e) }
            }
        }
    }

    /**
     * Releases resources by shutting down the executor service.
     * This should be called when the renderer is no longer needed to prevent memory leaks.
     */
    override fun shutdown() {
        executorService.shutdown()
    }
}