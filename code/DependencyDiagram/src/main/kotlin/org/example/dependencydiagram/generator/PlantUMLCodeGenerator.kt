package org.example.dependencydiagram.generator

class PlantUMLCodeGenerator : CodeGenerator {
    private val edgePattern = Regex("([\\w\\d]+)\\s*->\\s*([\\w\\d]+)")

    override fun generatePlantUmlCode(graphDefinition: String, enabledVertices: Set<String>): String {
        val sb = StringBuilder()
        sb.append("@startuml\n")
        // Set minimalistic theme/styling
        sb.append("!define BLACK_KNIGHT\n")
        sb.append("skinparam backgroundColor #111418\n")
        sb.append("skinparam defaultFontName 'Inter'\n")
        sb.append("skinparam defaultFontSize 12\n")

        // Remove diagram borders
        sb.append("hide empty members\n")
        sb.append("hide circle\n")
        sb.append("hide stereotype\n")

        // Minimize node shapes - use rectangle with rounded corners
        sb.append("skinparam roundCorner 10\n")
        sb.append("skinparam rectangleBorderThickness 1\n")

        // Define styles for nodes and arrows
        sb.append("<style>\n")
        sb.append("rectangle {\n")
        sb.append("  FontStyle bold\n")
        sb.append("  LineColor #FFFFFF\n")
        sb.append("  BackgroundColor #2D3748\n")
        sb.append("  BorderColor #FFFFFF\n")
        sb.append("  FontColor #FFFFFF\n")
        sb.append("}\n")
        sb.append("arrow {\n")
        sb.append("  LineColor #FFFFFF\n")
        sb.append("  LineThickness 1\n")
        sb.append("}\n")
        sb.append("</style>\n")

        // Set diagram scale to prevent excessive size
        sb.append("scale max 800 width\n")
        sb.append("scale max 600 height\n")

        // Only proceed if we have enabled vertices
        if (enabledVertices.isEmpty()) {
            sb.append("rectangle \"No enabled vertices\" as NoData #2D3748\n")
            sb.append("@enduml")
            return sb.toString()
        }

        // Define all nodes first with custom shape
        enabledVertices.forEach { vertexId ->
            sb.append("rectangle \"$vertexId\" as $vertexId #2D3748 {\n")
            sb.append("}\n")
        }

        // Process edges that have both source and target enabled
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

        // If no edges were found between enabled vertices, show a message
        if (!hasEdges && enabledVertices.isNotEmpty()) {
            sb.append("rectangle \"No connections between enabled vertices\" as NoConnections #2D3748\n")
        }

        sb.append("@enduml")
        return sb.toString()
    }
}