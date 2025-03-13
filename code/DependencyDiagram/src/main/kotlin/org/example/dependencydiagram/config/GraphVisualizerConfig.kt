package org.example.dependencydiagram.config

class GraphVisualizerConfig {
    // UI Settings
    val defaultInputText = "A -> B\nB -> C\nC -> A"
    val debounceDelayMs = 500L

    // Styling Settings
    val umlBackgroundColor = "#111418"
    val umlNodeBackgroundColor = "#2D3748"
    val umlFontName = "Inter"
    val umlFontSize = 12

    // Dimensions
    val maxDiagramWidth = 800
    val maxDiagramHeight = 600
}