package org.example.dependencydiagram.generator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

import java.io.File



class PlantUMLImageGeneratorTest {

    private lateinit var generator: PlantUMLImageGenerator

    @BeforeEach
    fun setUp() {
        generator = PlantUMLImageGenerator()
    }

    @Test
    fun `test generate simple diagram produces non-empty byte array`() {
        // Arrange
        val simplePlantUML = """
            @startuml
            Alice -> Bob: Hello
            @enduml
        """.trimIndent()

        val result = generator.generateImage(simplePlantUML)

        assertTrue(result.isNotEmpty(), "Generated image byte array should not be empty")
        assertTrue(result.size > 100, "Generated image should have a reasonable size")
    }


    @Test
    fun `test complex diagram generation`() {
        // Arrange
        val complexPlantUML = """
            @startuml
            package "Customer Domain" {
              class Customer
              class Address
              class Order
            }
            
            package "Product Domain" {
              class Product
              class Category
              class Review
            }
            
            Customer --> Address
            Customer --> Order
            Order --> Product
            Product --> Category
            Product --> Review
            @enduml
        """.trimIndent()

        val result = generator.generateImage(complexPlantUML)

        assertTrue(result.isNotEmpty(), "Generated image byte array should not be empty")
    }

    @Test
    fun `test handling of invalid PlantUML code`() {
        // Arrange
        val invalidPlantUML = """
            @startuml
            This is not valid PlantUML syntax
            @enduml
        """.trimIndent()

        // PlantUML typically doesn't throw exceptions for invalid syntax, it tries to render what it can
        // So we're just verifying it returns something without crashing
        val result = generator.generateImage(invalidPlantUML)
        assertTrue(result.isNotEmpty(), "Should produce output even with invalid PlantUML")
    }

    @Test
    fun `test empty input handling`() {
        val emptyPlantUML = ""

        val result = generator.generateImage(emptyPlantUML)

        assertTrue(result.isNotEmpty(), "Should handle empty input gracefully")
    }

    // Optional: A test that writes the output to a file so you can visually inspect it
    @Test
    fun `test output can be written to file for visual inspection`() {
        // Arrange
        val plantUML = """
            @startuml
            actor User
            User -> System: Request Data
            System --> User: Response
            @enduml
        """.trimIndent()

        val result = generator.generateImage(plantUML)

        val tempFile = File.createTempFile("plantuml-test-", ".png")
        tempFile.writeBytes(result)

        println("Test image written to: ${tempFile.absolutePath}")

        // Assert
        assertTrue(tempFile.length() > 0, "File should contain data")
        assertTrue(tempFile.exists(), "File should exist")
    }
}