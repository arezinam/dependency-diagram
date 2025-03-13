package org.example.dependencydiagram

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.example.dependencydiagram.config.GraphVisualizerConfig
import org.example.dependencydiagram.debouncer.Debouncer
import org.example.dependencydiagram.debouncer.InputDebouncer
import org.example.dependencydiagram.generator.PlantUMLCodeGenerator
import org.example.dependencydiagram.generator.PlantUMLImageGenerator
import org.example.dependencydiagram.model.Graph
import org.example.dependencydiagram.parser.GraphParser
import org.example.dependencydiagram.parser.RegexGraphParser
import org.example.dependencydiagram.renderer.JavaFXGraphRenderer
import org.example.dependencydiagram.renderer.PlantUMLGraphRenderer
import java.net.URL
import java.util.*

class MainController : Initializable {
    @FXML lateinit var graphDataInput: TextArea
    @FXML lateinit var checkboxContainer: VBox
    @FXML lateinit var diagramContainer: StackPane
    @FXML lateinit var noDataContainer: VBox
    @FXML lateinit var loadExampleButton: Button

    private val graphParser = RegexGraphParser()
    private val graphModel = Graph()
    private val debouncer = InputDebouncer(500L)
    val codeGenerator = PlantUMLCodeGenerator()
    val imageGenerator = PlantUMLImageGenerator()
    val plantUMLGraphRenderer = PlantUMLGraphRenderer(codeGenerator, imageGenerator)
    val renderer = JavaFXGraphRenderer(plantUMLGraphRenderer)

    // Using ImageView for diagram display
    private var diagramImageView = ImageView()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Set up the ImageView with proper constraints
        diagramImageView.styleClass.add("diagram-imageview")
        diagramImageView.isPreserveRatio = true

        // Critical: Set these properties to prevent infinite expansion
        diagramImageView.isSmooth = true
        diagramImageView.isCache = true

        // Bind the dimensions to the container, but with a maximum size
        // This prevents the ImageView from growing infinitely
        diagramImageView.fitWidthProperty().bind(diagramContainer.widthProperty().multiply(0.95))
        diagramImageView.fitHeightProperty().bind(diagramContainer.heightProperty().multiply(0.95))

        // Make sure the ImageView doesn't force its container to expand
        StackPane.setAlignment(diagramImageView, javafx.geometry.Pos.CENTER)

        diagramContainer.children.add(diagramImageView)
        diagramImageView.isVisible = false

        // Set up text area listener with debounce
        graphDataInput.textProperty().addListener { _, _, newValue ->
            debouncer.debounce {
                handleTextInputChange(newValue)
            }
        }

        // Set up load example button
        loadExampleButton.setOnAction {
            graphDataInput.text = generateExampleGraph()
        }

        // Initial empty state
        handleTextInputChange("")
    }



    private fun handleTextInputChange(text: String) {
        Platform.runLater {
            if (text.isNotBlank()) {
                // Parse input and update model
                val parsedGraph = graphParser.parseGraphDefinition(text)
                graphModel.updateGraph(parsedGraph)

                // Update UI
                updateVertexCheckboxes()
                refreshGraph()

                // Show graph view
                noDataContainer.isVisible = false
                diagramImageView.isVisible = true
            } else {
                // Handle empty input
                noDataContainer.isVisible = true
                diagramImageView.isVisible = false
                graphModel.clear()
                updateVertexCheckboxes()
            }
        }
    }


    private fun updateVertexCheckboxes() {
        // Clear existing checkboxes
        checkboxContainer.children.clear()


        val vertices = graphModel.getVertices()

        // Add a checkbox for each vertex
        if (vertices.isEmpty()) {
            // Add a label when no vertices exist
            val label = Label("No vertices found in graph data")
            label.styleClass.add("no-vertices-label")
            checkboxContainer.children.add(label)
        } else {
            vertices.forEach { vertex ->
                val checkBox = CheckBox("Node: ${vertex.id}").apply {
                    selectedProperty().bindBidirectional(vertex.enabledProperty)
                    selectedProperty().addListener { _, _, _ -> refreshGraph() }
                    styleClass.add("node-checkbox")
                }
                checkboxContainer.children.add(checkBox)
            }
        }
    }



    private fun refreshGraph() {
        // Only proceed if we have vertices
        if (graphModel.getVertices().isEmpty()) return

        renderer.renderGraph(
            graphDefinition = graphDataInput.text,
            enabledVertices = graphModel.getEnabledVertices(),
            onSuccess = { image ->
                Platform.runLater {
                    diagramImageView.image = image
                }
            },
            onError = { exception ->
                exception.printStackTrace()
            }
        )
    }


    private fun generateExampleGraph(): String {
        // Generate a random graph with 8-12 vertices and 10-20 edges
        val vertexCount = (8..12).random()
        val edgeCount = (10..20).random()

        val vertices = ('A'..'Z').take(vertexCount).map { it.toString() }
        val edges = mutableListOf<String>()

        repeat(edgeCount) {
            val from = vertices.random()
            val to = vertices.filter { it != from }.random()
            edges.add("$from -> $to")
        }

        return edges.joinToString("\n")
    }
}