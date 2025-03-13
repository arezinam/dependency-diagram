package org.example.dependencydiagram

import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.cell.CheckBoxListCell
import javafx.scene.image.ImageView
import javafx.util.StringConverter
import org.example.dependencydiagram.debouncer.InputDebouncer
import org.example.dependencydiagram.generator.PlantUMLCodeGenerator
import org.example.dependencydiagram.generator.PlantUMLImageGenerator
import org.example.dependencydiagram.model.Graph
import org.example.dependencydiagram.model.Vertex
import org.example.dependencydiagram.parser.RegexGraphParser
import org.example.dependencydiagram.renderer.JavaFXGraphRenderer
import org.example.dependencydiagram.renderer.PlantUMLGraphRenderer

class MainController {
    @FXML private lateinit var graphInputTextArea: TextArea
    @FXML private lateinit var vertexListView: ListView<Vertex>
    @FXML private lateinit var diagramImageView: ImageView

    // Core components
    private val graphParser = RegexGraphParser()
    private val graphModel = Graph()
    private val debouncer = InputDebouncer(500L)
    private val renderer = JavaFXGraphRenderer(
        PlantUMLGraphRenderer(
            PlantUMLCodeGenerator(),
            PlantUMLImageGenerator()
        )
    )

    @FXML
    fun initialize() {
        // Configure vertex list view to use checkboxes
        vertexListView.setCellFactory {
            CheckBoxListCell<Vertex>(
                { vertex -> vertex.enabledProperty() },
                VertexStringConverter()
            )
        }

        // Bind vertex list to the model
        vertexListView.items = graphModel.observeVertices()

        // Set up input listener with debouncing
        graphInputTextArea.textProperty().addListener { _, _, newText ->
            debouncer.debounce {
                updateGraph(newText)
            }
        }

        // Initial view setup
        graphInputTextArea.text = "A -> B\nB -> C\nC -> A"

        // Set up listener for vertex enabled state changes
        vertexListView.items.addListener(ListChangeListener {
            renderGraph()
        })
    }

    private fun updateGraph(graphDefinition: String) {
        val parsedGraph = graphParser.parseGraphDefinition(graphDefinition)
        graphModel.updateGraph(parsedGraph)
        renderGraph()
    }

    private fun renderGraph() {
        val graphDefinition = graphInputTextArea.text
        val enabledVertices = graphModel.getEnabledVertices()

        // Show loading indicator or placeholder
        diagramImageView.image = null

        // Request rendering
        renderer.renderGraph(
            graphDefinition,
            enabledVertices,
            { image ->
                Platform.runLater {
                    diagramImageView.image = image
                }
            },
            { exception ->
                Platform.runLater {
                    // Show error to user
                    println("Error rendering graph: ${exception.message}")
                    // In a real app, you might show this in the UI
                }
            }
        )
    }

    // Handle vertex check/uncheck events
    private fun onVertexEnabledChanged() {
        renderGraph()
    }

    // Converter for displaying vertices in the list view
    private class VertexStringConverter : StringConverter<Vertex>() {
        override fun toString(vertex: Vertex): String = vertex.id
        override fun fromString(string: String): Vertex = Vertex(string)
    }

    // Cleanup resources when application closes
    fun shutdown() {
        debouncer.shutdown()
        renderer.shutdown()
    }
}