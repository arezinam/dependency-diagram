package org.example.dependencydiagram.generator

/**
 * Generates PlantUML diagram code from a graph definition string.
 *
 * This generator creates a customized, dark-themed PlantUML diagram
 * showing relationships between enabled vertices. The output follows
 * a minimalist visual style optimized for dependency visualization.
 */
class PlantUMLCodeGenerator : CodeGenerator {
    /** Regular expression to identify edge definitions in the format "source -> target" */
    private val edgePattern = Regex("([\\w\\d]+)\\s*->\\s*([\\w\\d]+)")

    private val backgroundColor = "#111418"

    private val nodeBackgroundColor = "#2D3748"

    private val fontColor = "#FFFFFF"

    /**
     * Generates complete PlantUML code based on a graph definition and a set of enabled vertices.
     *
     * @param graphDefinition String containing edge definitions in "source -> target" format
     * @param enabledVertices Set of vertex IDs that should be included in the diagram
     * @return Complete PlantUML code as a string
     */
    override fun generatePlantUmlCode(graphDefinition: String, enabledVertices: Set<String>): String {
        val sb = StringBuilder()

        // Start the UML diagram
        sb.append("@startuml\n")

        // Apply styling
        applyDiagramStyling(sb)

        // Handle empty graph case
        if (enabledVertices.isEmpty()) {
            sb.append("rectangle \"No enabled vertices\" as NoData #$nodeBackgroundColor\n")
            sb.append("@enduml")
            return sb.toString()
        }

        // Define all enabled nodes
        defineNodes(sb, enabledVertices)

        // Process and add edges
        val hasEdges = addEdges(sb, graphDefinition, enabledVertices)

        // Add a message if no edges were found
        if (!hasEdges && enabledVertices.isNotEmpty()) {
            sb.append("rectangle \"No connections between enabled vertices\" as NoConnections #$nodeBackgroundColor\n")
        }

        // Close the UML diagram
        sb.append("@enduml")
        return sb.toString()
    }

    /**
     * Applies styling directives to make the diagram visually appealing with a dark theme.
     * Customizes node appearance, arrows, fonts, and overall diagram layout.
     *
     * @param sb StringBuilder to append styling directives to
     */
    private fun applyDiagramStyling(sb: StringBuilder) {
        // Set theme and basic visual properties
        sb.append("!define BLACK_KNIGHT\n")
        sb.append("skinparam backgroundColor $backgroundColor\n")
        sb.append("skinparam defaultFontName 'Inter'\n")
        sb.append("skinparam defaultFontSize 12\n")

        // Remove unnecessary diagram elements
        sb.append("hide empty members\n")
        sb.append("hide circle\n")
        sb.append("hide stereotype\n")

        // Configure node shape and appearance
        sb.append("skinparam roundCorner 10\n")
        sb.append("skinparam rectangleBorderThickness 1\n")

        // Define custom CSS-like styles
        sb.append("<style>\n")
        sb.append("rectangle {\n")
        sb.append("  FontStyle bold\n")
        sb.append("  LineColor $fontColor\n")
        sb.append("  BackgroundColor $nodeBackgroundColor\n")
        sb.append("  BorderColor $fontColor\n")
        sb.append("  FontColor $fontColor\n")
        sb.append("}\n")
        sb.append("arrow {\n")
        sb.append("  LineColor $fontColor\n")
        sb.append("  LineThickness 1\n")
        sb.append("}\n")
        sb.append("</style>\n")

        // Set diagram size limits to prevent excessive scaling
        sb.append("scale max 800 width\n")
        sb.append("scale max 600 height\n")
    }

    /**
     * Defines all enabled nodes in the PlantUML diagram.
     *
     * @param sb StringBuilder to append node definitions to
     * @param enabledVertices Set of vertex IDs to define as nodes
     */
    private fun defineNodes(sb: StringBuilder, enabledVertices: Set<String>) {
        enabledVertices.forEach { vertexId ->
            sb.append("rectangle \"$vertexId\" as $vertexId #$nodeBackgroundColor {\n")
            sb.append("}\n")
        }
    }

    /**
     * Processes the graph definition to extract and add edges between enabled vertices.
     *
     * @param sb StringBuilder to append edge definitions to
     * @param graphDefinition String containing edge definitions
     * @param enabledVertices Set of vertex IDs that should be included
     * @return Boolean indicating whether any edges were added
     */
    private fun addEdges(sb: StringBuilder, graphDefinition: String, enabledVertices: Set<String>): Boolean {
        var hasEdges = false

        graphDefinition.lines().forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isNotEmpty()) {
                val matchResult = edgePattern.find(trimmedLine)
                if (matchResult != null) {
                    val (source, target) = matchResult.destructured
                    // Only include edge if both vertices are enabled
                    if (source in enabledVertices && target in enabledVertices) {
                        sb.append("$source --> $target\n")
                        hasEdges = true
                    }
                }
            }
        }

        return hasEdges
    }
}