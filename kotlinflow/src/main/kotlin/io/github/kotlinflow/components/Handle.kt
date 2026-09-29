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

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates

val LocalNodeCoordinates = compositionLocalOf<LayoutCoordinates?> { null }

val DiamondShape: Shape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
    close()
}

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
    borderColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
    shape: Shape = CircleShape,
    isConnectable: Boolean = true,
    onDragStart: ((Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null
) {
    val flowState = LocalKotlinFlowState.current
    val nodeCoord = LocalNodeCoordinates.current
    val key = "${nodeId}__${id}__${type.name.lowercase()}"
    val shortKey = "${nodeId}__${id}"

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "${if (type == HandleType.SOURCE) "Output" else "Input"} handle $id"
            }
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                val center = Offset(bounds.left + bounds.width / 2f, bounds.top + bounds.height / 2f)
                if (flowState.handlePositions[key] != center) {
                    val current = flowState.handlePositions.toMutableMap()
                    current[key] = center
                    flowState.handlePositions = current
                }

                if (flowState.handleTypes[key] != type) {
                    val currentTypes = flowState.handleTypes.toMutableMap()
                    currentTypes[key] = type
                    flowState.handleTypes = currentTypes
                }

                if (flowState.handlePlacements[shortKey] != position) {
                    val currentPlacements = flowState.handlePlacements.toMutableMap()
                    currentPlacements[shortKey] = position
                    flowState.handlePlacements = currentPlacements
                }

                if (nodeCoord != null && nodeCoord.isAttached && coordinates.isAttached) {
                    val localCenter = nodeCoord.localPositionOf(coordinates, Offset(coordinates.size.width / 2f, coordinates.size.height / 2f))
                    if (flowState.handleOffsets[shortKey] != localCenter) {
                        val currentOffsets = flowState.handleOffsets.toMutableMap()
                        currentOffsets[shortKey] = localCenter
                        flowState.handleOffsets = currentOffsets
                    }
                }
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
            .shadow(2.dp, shape)
            .clip(shape)
            .background(color)
            .border(borderWidth, borderColor, shape)
    )
}
