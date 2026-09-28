package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A contextual floating toolbar displayed near a selected edge.
 */
@Composable
fun EdgeToolbar(
    modifier: Modifier = Modifier,
    edgeId: String? = null,
    isVisible: Boolean = true,
    position: Offset = Offset.Zero,
    offsetY: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .offset(x = position.x.dp, y = (position.y - offsetY.value).dp)
                .shadow(4.dp, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            content()
        }
    }
}
