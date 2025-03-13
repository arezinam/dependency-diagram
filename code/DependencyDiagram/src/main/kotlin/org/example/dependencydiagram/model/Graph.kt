package org.example.dependencydiagram.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class Graph : ObservableGraphModel{
    private val vertices = FXCollections.observableArrayList<Vertex>()

    override fun updateGraph(parsedGraph: ParsedGraph) {
        // Save current enabled states
        val currentVertexMap = vertices.associateBy({ it.id }, { it.enabled })

        // Clear and repopulate with new vertices
        vertices.clear()

        // Add sorted vertices to the list, preserving enabled states
        parsedGraph.vertices.sorted().forEach { vertexId ->
            val vertex = Vertex(vertexId)
            // Preserve enabled state if vertex existed before
            vertex.enabled = currentVertexMap[vertexId] ?: true
            vertices.add(vertex)
        }
    }

    override fun clear() {
        vertices.clear()
    }

    override fun getVertices(): List<Vertex> = vertices.toList()

    override fun getEnabledVertices(): Set<String> =
        vertices.filter { it.enabled }.map { it.id }.toSet()

    override fun observeVertices(): ObservableList<Vertex> =
        FXCollections.unmodifiableObservableList(vertices)
}