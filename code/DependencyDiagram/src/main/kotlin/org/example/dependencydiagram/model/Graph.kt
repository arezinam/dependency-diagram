package org.example.dependencydiagram.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class Graph{
    private val vertices = mutableListOf<Vertex>()

    fun updateGraph(parsedGraph: ParsedGraph) {
        val currentVertexMap = vertices.associateBy({ it.id }, { it.enabled })
        vertices.clear()
        parsedGraph.vertices.sorted().forEach { vertexId ->
            val vertex = Vertex(vertexId)
            vertex.enabled = currentVertexMap[vertexId] ?: true
            vertices.add(vertex)
        }
    }

    fun clear() {
        vertices.clear()
    }

    fun getVertices(): List<Vertex> = vertices.toList()

    fun getEnabledVertices(): Set<String> =
        vertices.filter { it.enabled }.map { it.id }.toSet()
}