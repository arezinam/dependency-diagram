package org.example.dependencydiagram.model

data class ParsedGraph(
    val vertices: Set<String>,
    val edges: List<Edge>
)