package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kotlinflow.models.KotlinFlowTheme
import io.github.kotlinflow.state.LocalKotlinFlowState
import io.github.kotlinflow.types.PanelPosition
import kotlin.math.roundToInt

/**
 * A floating panel containing zoom, fit-to-view, and lock controls.
 *
 * Controls automatically binds to viewport manipulation methods on
 * the shared [KotlinFlowState]. Place inside the overlay Composable of [KotlinFlow].
 */
@Composable
fun Controls(
    modifier: Modifier = Modifier,
    showZoom: Boolean = true,
    showFitView: Boolean = true,
    showInteractive: Boolean = false,
    position: PanelPosition = PanelPosition.BOTTOM_LEFT,
    children: @Composable () -> Unit = {}
) {
    val flowState = LocalKotlinFlowState.current
    val isDark = flowState.theme == KotlinFlowTheme.Dark
    val containerBg = if (isDark) Color(0xFF2C2C2C).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.2f)
    val contentColor = if (isDark) Color(0xFFE0E0E0) else Color.DarkGray
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.LightGray.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = position.alignment
    ) {
        Column(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(containerBg)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .width(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showZoom) {
                ControlButton(onClick = { flowState.zoomIn() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom in",
                        modifier = Modifier.padding(6.dp),
                        tint = contentColor
                    )
                }
                HorizontalDivider(color = dividerColor)
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(36.dp)
                        .clickable { flowState.zoomTo(1f) },
                    contentAlignment = Alignment.Center
                ) {
                    val pct = (flowState.viewport.zoom * 100f).roundToInt()
                    Text(
                        text = "$pct%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = contentColor
                    )
                }
                HorizontalDivider(color = dividerColor)
                ControlButton(onClick = { flowState.zoomOut() }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom out",
                        modifier = Modifier.padding(6.dp),
                        tint = contentColor
                    )
                }
            }

            if (showFitView) {
                if (showZoom) {
                    HorizontalDivider(color = dividerColor)
                }
                ControlButton(onClick = { flowState.fitView() }) {
                    Icon(
                        imageVector = Icons.Default.CropFree,
                        contentDescription = "Fit all nodes",
                        modifier = Modifier.padding(6.dp),
                        tint = contentColor
                    )
                }
            }

            if (showInteractive) {
                if (showZoom || showFitView) {
                    HorizontalDivider(color = dividerColor)
                }
                ControlButton(onClick = { flowState.isInteractive = !flowState.isInteractive }) {
                    Icon(
                        imageVector = if (flowState.isInteractive) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = if (flowState.isInteractive) "Disable interactions" else "Enable interactions",
                        modifier = Modifier.padding(6.dp),
                        tint = contentColor
                    )
                }
            }

            children()
        }
    }
}
