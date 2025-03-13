package org.example.dependencydiagram.parser

import org.example.dependencydiagram.model.ParsedGraph

interface GraphParser {
    fun parseGraphDefinition(text: String): ParsedGraph
}