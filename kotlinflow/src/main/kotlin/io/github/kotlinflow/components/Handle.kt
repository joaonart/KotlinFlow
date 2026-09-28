package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kotlinflow.state.LocalKotlinFlowState
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.Position

/**
 * A connection handle placed inside custom node views.
 *
 * Handles define the connection points where edges attach to nodes.
 * Each handle has a [type] (SOURCE or TARGET) and a [position] that
 * determines its visual placement within the node.
 */
@Composable
fun Handle(
    nodeId: String,
    id: String,
    modifier: Modifier = Modifier,
    type: HandleType = HandleType.SOURCE,
    position: Position = Position.RIGHT,
    color: Color = Color.Gray,
    size: Dp = 12.dp,
    isConnectable: Boolean = true,
    onDragStart: ((Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null
) {
    val flowState = LocalKotlinFlowState.current
    val key = "${nodeId}__${id}__${type.name.lowercase()}"

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "${if (type == HandleType.SOURCE) "Output" else "Input"} handle $id"
            }
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                val center = Offset(bounds.left + bounds.width / 2f, bounds.top + bounds.height / 2f)
                val current = flowState.handlePositions.toMutableMap()
                current[key] = center
                flowState.handlePositions = current

                val currentTypes = flowState.handleTypes.toMutableMap()
                currentTypes[key] = type
                flowState.handleTypes = currentTypes
            }
            .then(
                if (isConnectable && flowState.isInteractive && (onDragStart != null || onDrag != null || onDragEnd != null)) {
                    Modifier.pointerInput(key) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                onDragStart?.invoke(offset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag?.invoke(dragAmount)
                            },
                            onDragEnd = {
                                onDragEnd?.invoke()
                            },
                            onDragCancel = {
                                onDragEnd?.invoke()
                            }
                        )
                    }
                } else Modifier
            )
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White, CircleShape)
    )
}
