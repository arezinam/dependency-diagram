package org.example.dependencydiagram.model

import javafx.collections.ObservableList

interface ObservableGraphModel {
    fun updateGraph(parsedGraph: ParsedGraph)
    fun clear()
    fun getVertices(): List<Vertex>
    fun getEnabledVertices(): Set<String>
    fun observeVertices(): ObservableList<Vertex>
}