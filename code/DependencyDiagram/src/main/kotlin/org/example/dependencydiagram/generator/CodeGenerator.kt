package org.example.dependencydiagram.generator

interface CodeGenerator {
    fun generatePlantUmlCode(graphDefinition: String, enabledVertices: Set<String>): String
}