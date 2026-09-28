package io.github.kotlinflow.types

import androidx.compose.ui.input.key.Key

/**
 * Configurable keyboard shortcuts for the KotlinFlow canvas.
 */
data class KeyboardShortcuts(
    val deleteKey: Key = Key.Backspace,
    val forwardDeleteKey: Key = Key.Delete,
    val nudgeDistance: Float = 1f,
    val shiftNudgeDistance: Float = 10f
) {
    companion object {
        val Default = KeyboardShortcuts()
    }
}
