package io.github.kotlinflow.types

/**
 * Configuration for accessibility features in KotlinFlow.
 */
data class AccessibilityConfig(
    val announceSelectionChanges: Boolean = true,
    val announceConnectionEvents: Boolean = true,
    val nodeRoleDescription: String = "Graph node",
    val edgeRoleDescription: String = "Graph edge",
    val nodeDescription: String = "Double tap to select, drag to move",
    val nodeDescriptionKeyboardDisabled: String = "Node",
    val edgeDescription: String = "Double tap to select",
    val controlsAriaLabel: String = "Graph controls",
    val minimapAriaLabel: String = "Graph minimap",
    val handleAriaLabel: String = "Drag to create a connection",
    val zoomInAriaLabel: String = "Zoom in",
    val zoomOutAriaLabel: String = "Zoom out",
    val fitViewAriaLabel: String = "Fit view",
    val interactiveAriaLabel: String = "Toggle interactivity"
) {
    companion object {
        val Default = AccessibilityConfig()
    }
}
