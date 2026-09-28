package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Placement of the toolbar relative to the node. */
enum class ToolbarPosition {
    TOP, BOTTOM, LEFT, RIGHT
}

/** Alignment of the toolbar relative to the node. */
enum class ToolbarAlign {
    START, CENTER, END
}

/**
 * A contextual floating toolbar displayed near a selected node.
 */
@Composable
fun NodeToolbar(
    modifier: Modifier = Modifier,
    nodeId: String? = null,
    isVisible: Boolean = true,
    position: ToolbarPosition = ToolbarPosition.TOP,
    align: ToolbarAlign = ToolbarAlign.CENTER,
    offset: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .shadow(4.dp, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            content()
        }
    }
}
