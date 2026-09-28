package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kotlinflow.types.ResizeDirection

/**
 * A single resize handle for nodes.
 */
@Composable
fun NodeResizeControl(
    nodeId: String,
    modifier: Modifier = Modifier,
    position: ResizeDirection = ResizeDirection.BOTTOM_RIGHT,
    minWidth: Float = 50f,
    maxWidth: Float = 1000f,
    minHeight: Float = 30f,
    maxHeight: Float = 1000f,
    color: Color = Color(0xFF1E88E5),
    handleSize: Dp = 10.dp,
    isVisible: Boolean = true,
    currentWidth: Float = 150f,
    currentHeight: Float = 50f,
    onResize: ((Float, Float) -> Unit)? = null,
    onResizeStart: (() -> Unit)? = null,
    onResizeEnd: (() -> Unit)? = null,
    shouldResize: ((Float, Float) -> Boolean)? = null
) {
    if (!isVisible) return

    var startWidth by remember { mutableStateOf(currentWidth) }
    var startHeight by remember { mutableStateOf(currentHeight) }
    var totalDrag by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(handleSize)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.5.dp, color, CircleShape)
            .pointerInput(nodeId, position) {
                detectDragGestures(
                    onDragStart = {
                        startWidth = currentWidth
                        startHeight = currentHeight
                        totalDrag = Offset.Zero
                        onResizeStart?.invoke()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount

                        val deltaW = when (position) {
                            ResizeDirection.TOP_LEFT, ResizeDirection.LEFT, ResizeDirection.BOTTOM_LEFT -> -totalDrag.x
                            ResizeDirection.TOP_RIGHT, ResizeDirection.RIGHT, ResizeDirection.BOTTOM_RIGHT -> totalDrag.x
                            ResizeDirection.TOP, ResizeDirection.BOTTOM -> 0f
                        }

                        val deltaH = when (position) {
                            ResizeDirection.TOP_LEFT, ResizeDirection.TOP, ResizeDirection.TOP_RIGHT -> -totalDrag.y
                            ResizeDirection.BOTTOM_LEFT, ResizeDirection.BOTTOM, ResizeDirection.BOTTOM_RIGHT -> totalDrag.y
                            ResizeDirection.LEFT, ResizeDirection.RIGHT -> 0f
                        }

                        val w = (startWidth + deltaW).coerceIn(minWidth, maxWidth)
                        val h = (startHeight + deltaH).coerceIn(minHeight, maxHeight)

                        if (shouldResize?.invoke(w, h) != false) {
                            onResize?.invoke(w, h)
                        }
                    },
                    onDragEnd = {
                        onResizeEnd?.invoke()
                    },
                    onDragCancel = {
                        onResizeEnd?.invoke()
                    }
                )
            }
    )
}

/**
 * A resize handle that users embed in their node views to enable resizing.
 */
@Composable
fun NodeResizer(
    nodeId: String,
    modifier: Modifier = Modifier,
    direction: ResizeDirection = ResizeDirection.BOTTOM_RIGHT,
    minWidth: Float = 50f,
    maxWidth: Float = 1000f,
    minHeight: Float = 30f,
    maxHeight: Float = 1000f,
    color: Color = Color(0xFF1E88E5),
    currentWidth: Float = 150f,
    currentHeight: Float = 50f,
    onResize: ((Float, Float) -> Unit)? = null,
    onResizeStart: (() -> Unit)? = null,
    onResizeEnd: (() -> Unit)? = null,
    shouldResize: ((Float, Float) -> Boolean)? = null
) {
    NodeResizeControl(
        nodeId = nodeId,
        modifier = modifier,
        position = direction,
        minWidth = minWidth,
        maxWidth = maxWidth,
        minHeight = minHeight,
        maxHeight = maxHeight,
        color = color,
        currentWidth = currentWidth,
        currentHeight = currentHeight,
        onResize = onResize,
        onResizeStart = onResizeStart,
        onResizeEnd = onResizeEnd,
        shouldResize = shouldResize
    )
}
