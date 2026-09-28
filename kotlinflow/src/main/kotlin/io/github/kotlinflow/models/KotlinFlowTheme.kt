package io.github.kotlinflow.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

typealias SwiftFlowTheme = KotlinFlowTheme

/**
 * Configurable visual theme for the entire KotlinFlow canvas.
 *
 * Customize colors, sizes, and styles for edges, nodes, handles,
 * selection box, canvas background, minimap, snap lines, and edge labels.
 */
data class KotlinFlowTheme(
    // Edges
    val edgeColor: Color = Color.Gray.copy(alpha = 0.6f),
    val edgeSelectedColor: Color = Color(0xFF1E88E5), // Blue
    val edgeWidth: Float = 2f,
    val edgeSelectedWidth: Float = 3f,

    // Nodes
    val nodeBackgroundColor: Color = Color.White,
    val nodeSelectedBorderColor: Color = Color(0xFF1E88E5),
    val nodeSelectedBorderWidth: Float = 2f,

    // Handles
    val handleColor: Color = Color.Gray,
    val handleBorderColor: Color = Color.White,
    val handleSize: Float = 12f,

    // Selection Box
    val selectionBoxColor: Color = Color(0x1A1E88E5),
    val selectionBoxBorderColor: Color = Color(0x801E88E5),

    // Canvas
    val canvasBackgroundColor: Color = Color.Transparent,
    val gridColor: Color = Color.Gray.copy(alpha = 0.3f),
    val gridSpacing: Float = 20f,

    // Minimap
    val minimapBackgroundOpacity: Float = 0.9f,
    val minimapNodeColor: Color = Color.Gray.copy(alpha = 0.5f),
    val minimapSelectedNodeColor: Color = Color(0xFF1E88E5),

    // Snap Lines
    val snapLineColor: Color = Color(0x801E88E5),
    val snapLineWidth: Float = 1f,

    // Edge Labels
    val edgeLabelTextStyle: TextStyle = TextStyle(fontSize = 11.sp),
    val edgeLabelColor: Color = Color.Black,
    val edgeLabelBackgroundColor: Color = Color(0xFF333333)
) {
    companion object {
        val Default = KotlinFlowTheme()

        val Dark = KotlinFlowTheme(
            edgeColor = Color.White.copy(alpha = 0.4f),
            edgeSelectedColor = Color(0xFF00E5FF),
            nodeBackgroundColor = Color(0xFF333333),
            nodeSelectedBorderColor = Color(0xFF00E5FF),
            handleColor = Color.White.copy(alpha = 0.6f),
            selectionBoxColor = Color(0x1A00E5FF),
            selectionBoxBorderColor = Color(0x8000E5FF),
            canvasBackgroundColor = Color(0xFF1E1E1E),
            gridColor = Color.White.copy(alpha = 0.08f),
            minimapNodeColor = Color.White.copy(alpha = 0.3f),
            minimapSelectedNodeColor = Color(0xFF00E5FF),
            snapLineColor = Color(0x8000E5FF),
            edgeLabelColor = Color.White,
            edgeLabelBackgroundColor = Color(0xFF333333)
        )

        val Light = KotlinFlowTheme(
            edgeColor = Color.Gray.copy(alpha = 0.6f),
            edgeSelectedColor = Color(0xFF1E88E5),
            nodeBackgroundColor = Color.White,
            nodeSelectedBorderColor = Color(0xFF1E88E5),
            handleColor = Color.Gray,
            selectionBoxColor = Color(0x1A1E88E5),
            selectionBoxBorderColor = Color(0x801E88E5),
            canvasBackgroundColor = Color.Transparent,
            gridColor = Color.Gray.copy(alpha = 0.3f),
            minimapNodeColor = Color.Gray.copy(alpha = 0.5f),
            minimapSelectedNodeColor = Color(0xFF1E88E5),
            snapLineColor = Color(0x801E88E5),
            edgeLabelColor = Color.Black,
            edgeLabelBackgroundColor = Color(0xFF333333)
        )
    }
}
