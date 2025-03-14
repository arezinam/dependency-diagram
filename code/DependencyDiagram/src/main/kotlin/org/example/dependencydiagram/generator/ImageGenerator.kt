package org.example.dependencydiagram.generator

interface ImageGenerator {
    fun generateImage(plantUmlCode: String): ByteArray
}