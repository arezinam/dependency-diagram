package org.example.dependencydiagram.renderer

import javafx.application.Platform
import org.example.dependencydiagram.generator.ImageGenerator
import org.example.dependencydiagram.generator.PlantUMLCodeGenerator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlantUMLGraphRenderer(
    private val codeGenerator: PlantUMLCodeGenerator,
    private val imageGenerator: ImageGenerator,
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
) : GraphRenderer {

    override fun renderGraph(
        graphDefinition: String,
        enabledVertices: Set<String>,
        onSuccess: (ByteArray) -> Unit,
        onError: (Exception) -> Unit
    ) {
        executorService.submit {
            try {
                val plantUmlCode = codeGenerator.generatePlantUmlCode(graphDefinition, enabledVertices)
                val imageBytes = imageGenerator.generateImage(plantUmlCode)
                Platform.runLater { onSuccess(imageBytes) }
            } catch (e: Exception) {
                Platform.runLater { onError(e) }
            }
        }
    }

    override fun shutdown() {
        executorService.shutdown()
    }
}