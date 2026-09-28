package io.github.kotlinflow.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kotlinflow.types.PanelPosition

/**
 * Positional overlay container for placing custom UI elements on the canvas.
 *
 * ```kotlin
 * Panel(position = PanelPosition.TOP_RIGHT) {
 *     Button(onClick = { ... }) { Text("Reset") }
 * }
 * ```
 */
@Composable
fun Panel(
    position: PanelPosition = PanelPosition.TOP_RIGHT,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = position.alignment
    ) {
        content()
    }
}
