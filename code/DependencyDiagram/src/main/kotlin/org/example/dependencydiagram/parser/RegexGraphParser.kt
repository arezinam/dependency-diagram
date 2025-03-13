package org.example.dependencydiagram.parser

import org.example.dependencydiagram.model.Edge
import org.example.dependencydiagram.model.ParsedGraph

class RegexGraphParser : GraphParser {
    private val edgePattern = Regex("([\\w\\d]+)\\s*->\\s*([\\w\\d]+)")

    override fun parseGraphDefinition(text: String): ParsedGraph {
        val foundVertices = mutableSetOf<String>()
        val edges = mutableListOf<Edge>()

        // Parse each line for edges
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