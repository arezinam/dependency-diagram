package org.example.dependencydiagram.model

/**
 * Represents a graph data structure for the dependency diagram.
 * Manages vertices and their enabled/disabled states.
 */
class Graph {
    /** Collection of all vertices in the graph */
    private val vertices = mutableListOf<Vertex>()

    /**
     * Updates the graph with new vertices while preserving enabled states.
     *
     * @param parsedGraph The new graph structure to adopt
     */
    fun updateGraph(parsedGraph: ParsedGraph) {
        // Save current enabled states
        val currentVertexMap = vertices.associateBy({ it.id }, { it.enabled })
        vertices.clear()

        // Rebuild with new vertices, keeping previous enabled states
        parsedGraph.vertices.sorted().forEach { vertexId ->
            val vertex = Vertex(vertexId)
            vertex.enabled = currentVertexMap[vertexId] ?: true
            vertices.add(vertex)
        }
    }

    /** Removes all vertices from the graph */
    fun clear() {
        vertices.clear()
    }

    /** Returns a copy of all vertices */
    fun getVertices(): List<Vertex> = vertices.toList()

    /** Returns IDs of all enabled vertices */
    fun getEnabledVertices(): Set<String> =
        vertices.filter { it.enabled }.map { it.id }.toSet()
}