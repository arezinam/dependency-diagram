package org.example.dependencydiagram.config

import org.example.dependencydiagram.debouncer.Debouncer
import org.example.dependencydiagram.debouncer.InputDebouncer
import org.example.dependencydiagram.generator.PlantUMLCodeGenerator
import org.example.dependencydiagram.generator.PlantUMLImageGenerator
import org.example.dependencydiagram.model.Graph
import org.example.dependencydiagram.model.ObservableGraphModel
import org.example.dependencydiagram.parser.GraphParser
import org.example.dependencydiagram.parser.RegexGraphParser
import org.example.dependencydiagram.renderer.JavaFXGraphRenderer
import org.example.dependencydiagram.renderer.PlantUMLGraphRenderer

class GraphVisualizerFactory(private val config: GraphVisualizerConfig = GraphVisualizerConfig()) {

    fun createGraphParser(): GraphParser {
        return RegexGraphParser()
    }

    fun createGraphModel(): ObservableGraphModel {
        return Graph()
    }

    fun createDebouncer(): Debouncer {
        return InputDebouncer(config.debounceDelayMs)
    }

    fun createGraphRenderer(): JavaFXGraphRenderer {
        val codeGenerator = PlantUMLCodeGenerator()
        val imageGenerator = PlantUMLImageGenerator()
        val renderer = PlantUMLGraphRenderer(codeGenerator, imageGenerator)
        return JavaFXGraphRenderer(renderer)
    }
}