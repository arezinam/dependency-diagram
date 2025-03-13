package org.example.dependencydiagram.generator

import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream

/**
 * Converts PlantUML code into image data.
 *
 * This generator takes a string containing PlantUML diagram code and renders it into
 * a binary image representation that can be displayed or saved. It uses the PlantUML
 * library to handle the actual rendering process.
 */
class PlantUMLImageGenerator : ImageGenerator {

    /**
     * Generates an image from the provided PlantUML code.
     *
     * This method takes a string containing valid PlantUML markup and converts it
     * into binary image data. The PlantUML library handles the conversion internally,
     * including parsing the diagram definition and rendering the graphical elements.
     *
     * The output format is determined by PlantUML's default settings, which typically
     * produce PNG images unless otherwise specified in the PlantUML code.
     *
     * @param plantUmlCode A string containing valid PlantUML diagram definition
     * @return Binary image data as a ByteArray that can be written to a file or displayed
     */
    override fun generateImage(plantUmlCode: String): ByteArray {
        // Create a ByteArrayOutputStream to capture the image data
        val outputStream = ByteArrayOutputStream()

        // Create a PlantUML reader to parse the diagram definition
        val reader = SourceStringReader(plantUmlCode)

        // Generate the image and write it to the output stream
        // The PlantUML library handles all the parsing and rendering internally
        reader.outputImage(outputStream)

        // Convert the stream to a byte array and return it
        return outputStream.toByteArray()
    }
}