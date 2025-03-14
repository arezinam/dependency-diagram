package org.example.dependencydiagram.parser

import org.example.dependencydiagram.model.Edge
import org.example.dependencydiagram.model.ParsedGraph

/**
 * Parser that extracts graph structure from text using regular expressions.
 * Identifies edges in the format "source -> target" to build a graph model.
 */
class RegexGraphParser : GraphParser {
    /** Regular expression pattern to match edge definitions */
    private val edgePattern = Regex("([\\w\\d]+)\\s*->\\s*([\\w\\d]+)")

    /**
     * Parses a text definition into a structured graph representation.
     *
     * @param text String containing edge definitions in "source -> target" format
     * @return ParsedGraph containing all discovered vertices and edges
     */
    override fun parseGraphDefinition(text: String): ParsedGraph {
        val foundVertices = mutableSetOf<String>()
        val edges = mutableListOf<Edge>()

        // Process each line to find edge definitions
        text.lines().forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isNotEmpty()) {
                val matchResult = edgePattern.find(trimmedLine)

                if (matchResult != null) {
                    val (source, target) = matchResult.destructured
                    foundVertices.add(source)
                    foundVertices.add(target)
                    edges.add(Edge(source, target))
                }
            }
        }

        return ParsedGraph(foundVertices, edges)
    }
}