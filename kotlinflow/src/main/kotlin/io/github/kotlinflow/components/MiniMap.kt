package io.github.kotlinflow.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kotlinflow.models.KotlinFlowTheme
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.state.LocalKotlinFlowState
import io.github.kotlinflow.types.PanelPosition
import kotlin.math.max
import kotlin.math.min

/**
 * Properties passed to a custom minimap node view or color mapper.
 */
data class MiniMapNodeProps(
    val id: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val selected: Boolean,
    val type: String
)

/**
 * An interactive overview of the graph showing current node positions and the viewport.
 *
 * Reads node positions, sizes, and viewport from the environment.
 * Supports touch drag to pan the main viewport. Place inside the
 * overlay Composable of [KotlinFlow].
 */
@Composable
fun MiniMap(
    modifier: Modifier = Modifier,
    nodeColor: Color = Color.Gray.copy(alpha = 0.5f),
    nodeColorMapper: ((MiniMapNodeProps) -> Color)? = null,
    selectedNodeColor: Color = Color(0xFF1E88E5),
    nodeStrokeColor: Color = Color.Transparent,
    nodeStrokeWidth: Float = 0f,
    nodeBorderRadius: Float = 2f,
    maskColor: Color = Color(0x1A000000),
    maskStrokeColor: Color = Color(0xFF1E88E5),
    width: Dp = 150.dp,
    height: Dp = 100.dp,
    pannable: Boolean = true,
    zoomable: Boolean = false,
    position: PanelPosition = PanelPosition.BOTTOM_RIGHT,
    nodeContent: (@Composable (MiniMapNodeProps) -> Unit)? = null
) {
    val flowState = LocalKotlinFlowState.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = position.alignment
    ) {
        val isDark = flowState.theme == KotlinFlowTheme.Dark
        val containerBg = if (isDark) Color(0xFF252525).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.92f)
        val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.2f)

        Box(
            modifier = Modifier
                .size(width, height)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(containerBg)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (pannable && flowState.isInteractive) {
                            Modifier.pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    // Map minimap drag to canvas pan
                                    val currentVp = flowState.viewport
                                    val newX = currentVp.x - dragAmount.x * 4f
                                    val newY = currentVp.y - dragAmount.y * 4f
                                    flowState.setViewport(currentVp.copy(x = newX, y = newY), animated = false)
                                }
                            }
                        } else Modifier
                    )
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Calculate bounding box of graph in flow coordinates
                val visibleNodes = flowState.nodes.filter { !it.hidden }
                var minX = 0f
                var minY = 0f
                var maxX = 1000f
                var maxY = 1000f

                if (visibleNodes.isNotEmpty()) {
                    minX = visibleNodes.minOf { it.x }
                    minY = visibleNodes.minOf { it.y }
                    maxX = visibleNodes.maxOf {
                        val sz = flowState.nodeSizes[it.id] ?: Size(150f, 50f)
                        it.x + sz.width
                    }
                    maxY = visibleNodes.maxOf {
                        val sz = flowState.nodeSizes[it.id] ?: Size(150f, 50f)
                        it.y + sz.height
                    }
                }

                // Also incorporate viewport rectangle
                val vp = flowState.viewport
                val vpZoom = max(vp.zoom, 0.1f)
                val vpLeft = -vp.x / vpZoom
                val vpTop = -vp.y / vpZoom
                val vpRight = vpLeft + (flowState.viewSize.width / vpZoom)
                val vpBottom = vpTop + (flowState.viewSize.height / vpZoom)

                val bMinX = min(minX, vpLeft) - 50f
                val bMinY = min(minY, vpTop) - 50f
                val bMaxX = max(maxX, vpRight) + 50f
                val bMaxY = max(maxY, vpBottom) + 50f

                val bWidth = max(bMaxX - bMinX, 1f)
                val bHeight = max(bMaxY - bMinY, 1f)

                val scaleX = canvasW / bWidth
                val scaleY = canvasH / bHeight
                val scale = min(scaleX, scaleY)

                val offsetX = (canvasW - bWidth * scale) / 2f
                val offsetY = (canvasH - bHeight * scale) / 2f

                fun toMinimapX(gx: Float) = (gx - bMinX) * scale + offsetX
                fun toMinimapY(gy: Float) = (gy - bMinY) * scale + offsetY

                // 1. Draw nodes
                for (node in visibleNodes) {
                    val sz = flowState.nodeSizes[node.id] ?: Size(150f, 50f)
                    val nx = toMinimapX(node.x)
                    val ny = toMinimapY(node.y)
                    val nw = sz.width * scale
                    val nh = sz.height * scale

                    val props = MiniMapNodeProps(
                        id = node.id,
                        x = node.x,
                        y = node.y,
                        width = sz.width,
                        height = sz.height,
                        selected = node.selected,
                        type = node.type
                    )

                    val color = if (node.selected) {
                        selectedNodeColor
                    } else {
                        nodeColorMapper?.invoke(props) ?: nodeColor
                    }

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(nx, ny),
                        size = Size(nw, nh),
                        cornerRadius = CornerRadius(nodeBorderRadius, nodeBorderRadius)
                    )

                    if (nodeStrokeWidth > 0f && nodeStrokeColor != Color.Transparent) {
                        drawRoundRect(
                            color = nodeStrokeColor,
                            topLeft = Offset(nx, ny),
                            size = Size(nw, nh),
                            cornerRadius = CornerRadius(nodeBorderRadius, nodeBorderRadius),
                            style = Stroke(width = nodeStrokeWidth)
                        )
                    }
                }

                // 2. Draw viewport rectangle
                val rectX = toMinimapX(vpLeft)
                val rectY = toMinimapY(vpTop)
                val rectW = (vpRight - vpLeft) * scale
                val rectH = (vpBottom - vpTop) * scale

                drawRect(
                    color = maskColor,
                    topLeft = Offset(rectX, rectY),
                    size = Size(rectW, rectH)
                )
                drawRect(
                    color = maskStrokeColor,
                    topLeft = Offset(rectX, rectY),
                    size = Size(rectW, rectH),
                    style = Stroke(width = 1.5f)
                )
            }
        }
    }
}
