package org.example.dependencydiagram.parser

import org.example.dependencydiagram.model.Edge
import org.example.dependencydiagram.model.ParsedGraph
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

class RegexGraphParserTest {

    private lateinit var parser: RegexGraphParser

    @BeforeEach
    fun setUp() {
        parser = RegexGraphParser()
    }

    @Test
    @DisplayName("Test parsing simple valid edge definitions")
    fun testParseSimpleEdgeDefinitions() {
        // Arrange - Create a simple graph definition with two edges
        val input = """
            A -> B
            B -> C
        """.trimIndent()

        // Act - Parse the graph definition
        val result = parser.parseGraphDefinition(input)

        // Assert - Check that the parser correctly identifies vertices and edges
        assertEquals(setOf("A", "B", "C"), result.vertices, "Should extract all unique vertices")
        assertEquals(2, result.edges.size, "Should extract the correct number of edges")
        assertTrue(result.edges.contains(Edge("A", "B")), "Should contain edge from A to B")
        assertTrue(result.edges.contains(Edge("B", "C")), "Should contain edge from B to C")
    }

    @Test
    @DisplayName("Test parsing edges with varied spacing")
    fun testParseEdgesWithVariedSpacing() {
        // Arrange - Create edge definitions with different spacing patterns
        val input = """
            A->B
            C -> D
            E  ->  F
            G->    H
            I   ->J
        """.trimIndent()

        // Act
        val result = parser.parseGraphDefinition(input)

        // Assert
        assertEquals(setOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"), result.vertices)
        assertEquals(5, result.edges.size)
        assertTrue(result.edges.contains(Edge("A", "B")))
        assertTrue(result.edges.contains(Edge("C", "D")))
        assertTrue(result.edges.contains(Edge("E", "F")))
        assertTrue(result.edges.contains(Edge("G", "H")))
        assertTrue(result.edges.contains(Edge("I", "J")))
    }

    @Test
    @DisplayName("Test parsing with empty lines and non-edge content")
    fun testParseWithEmptyLinesAndNonEdgeContent() {
        // Arrange - Create input with empty lines and lines that don't match the edge pattern
        val input = """
            
            A -> B
            
            This is a comment
            C -> D
            Not an edge definition
            E -> F
            
        """.trimIndent()

        // Act
        val result = parser.parseGraphDefinition(input)

        // Assert
        assertEquals(setOf("A", "B", "C", "D", "E", "F"), result.vertices)
        assertEquals(3, result.edges.size)
        assertTrue(result.edges.contains(Edge("A", "B")))
        assertTrue(result.edges.contains(Edge("C", "D")))
        assertTrue(result.edges.contains(Edge("E", "F")))
    }

    @Test
    @DisplayName("Test parsing with alphanumeric vertex names")
    fun testParseWithAlphanumericVertexNames() {
        // Arrange - Create edge definitions with alphanumeric vertex names
        val input = """
            A1 -> B2
            Node123 -> Service456
            Client -> API789
        """.trimIndent()

        // Act
        val result = parser.parseGraphDefinition(input)

        // Assert
        assertEquals(setOf("A1", "B2", "Node123", "Service456", "Client", "API789"), result.vertices)
        assertEquals(3, result.edges.size)
        assertTrue(result.edges.contains(Edge("A1", "B2")))
        assertTrue(result.edges.contains(Edge("Node123", "Service456")))
        assertTrue(result.edges.contains(Edge("Client", "API789")))
    }

    @Test
    @DisplayName("Test parsing with duplicate edges")
    fun testParseWithDuplicateEdges() {
        // Arrange - Create input with duplicate edge definitions
        val input = """
            A -> B
            A -> B
            B -> C
            B -> C
            A -> C
        """.trimIndent()

        // Act
        val result = parser.parseGraphDefinition(input)

        // Assert
        assertEquals(setOf("A", "B", "C"), result.vertices)
        // Since we're using a mutable list for edges, duplicates are not automatically removed
        assertEquals(5, result.edges.size, "Should contain all edges including duplicates")

        // Count occurrences of each edge
        val edgeAB = result.edges.count { it.source == "A" && it.target == "B" }
        val edgeBC = result.edges.count { it.source == "B" && it.target == "C" }
        val edgeAC = result.edges.count { it.source == "A" && it.target == "C" }

        assertEquals(2, edgeAB, "Should contain A->B edge twice")
        assertEquals(2, edgeBC, "Should contain B->C edge twice")
        assertEquals(1, edgeAC, "Should contain A->C edge once")
    }

    @Test
    @DisplayName("Test parsing with completely empty input")
    fun testParseWithEmptyInput() {
        // Arrange
        val input = ""

        // Act
        val result = parser.parseGraphDefinition(input)

        // Assert
        assertTrue(result.vertices.isEmpty(), "No vertices should be extracted from empty input")
        assertTrue(result.edges.isEmpty(), "No edges should be extracted from empty input")
    }

    @Test
    @DisplayName("Test parsing with multiple edges on the same line")
    fun testParseWithMultipleEdgesOnSameLine() {
        // Arrange - Create input with multiple edge definitions on the same line
        val input = """
            A -> B C -> D
            E -> F G -> H
        """.trimIndent()

        // Act
        val result = parser.parseGraphDefinition(input)

        // Assert - The current implementation only finds the first edge on each line
        assertEquals(setOf("A", "B", "E", "F"), result.vertices)
        assertEquals(2, result.edges.size)
        assertTrue(result.edges.contains(Edge("A", "B")))
        assertTrue(result.edges.contains(Edge("E", "F")))

        // Note: C->D and G->H are not detected with the current implementation
        assertFalse(result.edges.contains(Edge("C", "D")))
        assertFalse(result.edges.contains(Edge("G", "H")))
    }
}