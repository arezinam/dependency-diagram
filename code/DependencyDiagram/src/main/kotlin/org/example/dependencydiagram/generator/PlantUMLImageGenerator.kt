package org.example.dependencydiagram.generator

import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream

class PlantUMLImageGenerator : ImageGenerator {
    override fun generateImage(plantUmlCode: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val reader = SourceStringReader(plantUmlCode)

        // Generate the image
        reader.outputImage(outputStream)

        return outputStream.toByteArray()
    }
}