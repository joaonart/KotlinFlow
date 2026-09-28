package io.github.kotlinflow.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.types.DefaultEdgeOptions
import io.github.kotlinflow.types.EdgePathResult
import io.github.kotlinflow.types.Position
import java.util.UUID
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

// MARK: - Edge Creation

/**
 * Creates an edge from a [Connection] and appends it, preventing duplicates.
 *
 * Duplicate detection mirrors React Flow / SwiftFlow: an edge is considered a duplicate
 * only when source, target, sourceHandle, and targetHandle all match.
 */
fun <EdgeData> addEdge(
    connection: Connection,
    edges: List<Edge<EdgeData>>,
    defaults: DefaultEdgeOptions? = null
): List<Edge<EdgeData>> {
    val isDuplicate = edges.any { edge ->
        edge.source == connection.source &&
                edge.target == connection.target &&
                edge.sourceHandle == connection.sourceHandle &&
                edge.targetHandle == connection.targetHandle
    }
    if (isDuplicate) return edges

    val suffix = UUID.randomUUID().toString().take(8)
    val newEdge = Edge<EdgeData>(
        id = "e-${connection.source}-${connection.target}-$suffix",
        source = connection.source,
        target = connection.target,
        sourceHandle = connection.sourceHandle,
        targetHandle = connection.targetHandle,
        type = defaults?.type ?: EdgeType.DEFAULT,
        animated = defaults?.animated ?: false,
        markerStart = defaults?.markerStart,
        markerEnd = defaults?.markerEnd
    )
    return edges + newEdge
}

/**
 * Reconnects an existing edge with a new connection, preserving edge properties.
 */
fun <EdgeData> reconnectEdge(
    oldEdge: Edge<EdgeData>,
    newConnection: Connection,
    edges: List<Edge<EdgeData>>
): List<Edge<EdgeData>> {
    val result = edges.toMutableList()
    val index = result.indexOfFirst { it.id == oldEdge.id }
    if (index == -1) return edges
    result[index] = result[index].copy(
        source = newConnection.source,
        target = newConnection.target,
        sourceHandle = newConnection.sourceHandle,
        targetHandle = newConnection.targetHandle
    )
    return result
}

// MARK: - Edge Path Generation

/**
 * Returns a smooth cubic bezier Path between two points.
 */
fun getBezierPath(sourceX: Float, sourceY: Float, targetX: Float, targetY: Float): Path {
    val path = Path()
    path.moveTo(sourceX, sourceY)
    val controlOffset = max(abs(targetX - sourceX) / 2f, 50f)
    path.cubicTo(
        sourceX + controlOffset, sourceY,
        targetX - controlOffset, targetY,
        targetX, targetY
    )
    return path
}

/**
 * Returns a simple quadratic bezier Path with a single control point.
 */
fun getSimpleBezierPath(sourceX: Float, sourceY: Float, targetX: Float, targetY: Float): Path {
    val path = Path()
    path.moveTo(sourceX, sourceY)
    val controlX = (sourceX + targetX) / 2f
    val controlY = (sourceY + targetY) / 2f
    path.quadraticTo(controlX, controlY, targetX, targetY)
    return path
}

/**
 * Returns a straight line Path between two points.
 */
fun getStraightPath(sourceX: Float, sourceY: Float, targetX: Float, targetY: Float): Path {
    val path = Path()
    path.moveTo(sourceX, sourceY)
    path.lineTo(targetX, targetY)
    return path
}

/**
 * Returns a right-angle step Path with sharp corners.
 */
fun getStepPath(sourceX: Float, sourceY: Float, targetX: Float, targetY: Float): Path {
    val path = Path()
    path.moveTo(sourceX, sourceY)
    val midX = (sourceX + targetX) / 2f
    path.lineTo(midX, sourceY)
    path.lineTo(midX, targetY)
    path.lineTo(targetX, targetY)
    return path
}

/**
 * Returns a right-angle step Path with rounded corners.
 */
fun getSmoothStepPath(
    sourceX: Float,
    sourceY: Float,
    targetX: Float,
    targetY: Float,
    borderRadius: Float = 5f
): Path {
    val path = Path()
    path.moveTo(sourceX, sourceY)

    val midX = (sourceX + targetX) / 2f
    val r = min(borderRadius, min(abs(midX - sourceX), abs(targetY - sourceY) / 2f))

    if (abs(targetY - sourceY) < 1f) {
        path.lineTo(targetX, targetY)
        return path
    }

    val goingDown = targetY > sourceY
    val goingRight = midX > sourceX
    val targetGoingRight = targetX > midX

    path.lineTo(midX - (if (goingRight) r else -r), sourceY)
    path.quadraticTo(
        midX, sourceY,
        midX, sourceY + (if (goingDown) r else -r)
    )
    path.lineTo(midX, targetY - (if (goingDown) r else -r))
    path.quadraticTo(
        midX, targetY,
        midX + (if (targetGoingRight) r else -r), targetY
    )
    path.lineTo(targetX, targetY)
    return path
}

// MARK: - Position-aware Edge Paths

data class PositionAwarePathResult(
    val path: Path,
    val labelX: Float,
    val labelY: Float,
    val offsetX: Float,
    val offsetY: Float
)

/**
 * Position-aware smooth cubic bezier path.
 */
fun getBezierPath(
    sourceX: Float, sourceY: Float, sourcePosition: Position,
    targetX: Float, targetY: Float, targetPosition: Position,
    curvature: Float = 0.25f
): PositionAwarePathResult {
    val path = Path()
    path.moveTo(sourceX, sourceY)

    val isSourceHorizontal = sourcePosition == Position.LEFT || sourcePosition == Position.RIGHT
    val isTargetHorizontal = targetPosition == Position.LEFT || targetPosition == Position.RIGHT

    val dist = max(abs(targetX - sourceX), abs(targetY - sourceY))
    val offset = max(dist * curvature, 25f)

    val c1x = when (sourcePosition) {
        Position.LEFT -> sourceX - offset
        Position.RIGHT -> sourceX + offset
        else -> sourceX
    }
    val c1y = when (sourcePosition) {
        Position.TOP -> sourceY - offset
        Position.BOTTOM -> sourceY + offset
        else -> sourceY
    }

    val c2x = when (targetPosition) {
        Position.LEFT -> targetX - offset
        Position.RIGHT -> targetX + offset
        else -> targetX
    }
    val c2y = when (targetPosition) {
        Position.TOP -> targetY - offset
        Position.BOTTOM -> targetY + offset
        else -> targetY
    }

    path.cubicTo(c1x, c1y, c2x, c2y, targetX, targetY)

    // Cubic bezier midpoint at t = 0.5
    val t = 0.5f
    val mt = 0.5f
    val lx = mt * mt * mt * sourceX + 3f * mt * mt * t * c1x + 3f * mt * t * t * c2x + t * t * t * targetX
    val ly = mt * mt * mt * sourceY + 3f * mt * mt * t * c1y + 3f * mt * t * t * c2y + t * t * t * targetY

    return PositionAwarePathResult(path, lx, ly, 0f, 0f)
}

/**
 * Position-aware simple quadratic bezier path.
 */
fun getSimpleBezierPath(
    sourceX: Float, sourceY: Float, sourcePosition: Position,
    targetX: Float, targetY: Float, targetPosition: Position
): PositionAwarePathResult {
    val path = Path()
    path.moveTo(sourceX, sourceY)
    val midX = (sourceX + targetX) / 2f
    val midY = (sourceY + targetY) / 2f

    val controlX: Float
    val controlY: Float
    if (sourcePosition == Position.LEFT || sourcePosition == Position.RIGHT) {
        controlX = midX
        controlY = sourceY
    } else {
        controlX = sourceX
        controlY = midY
    }

    path.quadraticTo(controlX, controlY, targetX, targetY)
    val lx = 0.25f * sourceX + 0.5f * controlX + 0.25f * targetX
    val ly = 0.25f * sourceY + 0.5f * controlY + 0.25f * targetY

    return PositionAwarePathResult(path, lx, ly, lx - midX, ly - midY)
}

/**
 * Position-aware step path with control based on source/target handle positions.
 */
fun getStepPath(
    sourceX: Float, sourceY: Float, sourcePosition: Position,
    targetX: Float, targetY: Float, targetPosition: Position
): PositionAwarePathResult {
    val isSourceHorizontal = sourcePosition == Position.LEFT || sourcePosition == Position.RIGHT
    val path = Path()
    path.moveTo(sourceX, sourceY)

    if (isSourceHorizontal) {
        val midX = (sourceX + targetX) / 2f
        path.lineTo(midX, sourceY)
        path.lineTo(midX, targetY)
        path.lineTo(targetX, targetY)
    } else {
        val midY = (sourceY + targetY) / 2f
        path.lineTo(sourceX, midY)
        path.lineTo(targetX, midY)
        path.lineTo(targetX, targetY)
    }

    val lx = (sourceX + targetX) / 2f
    val ly = (sourceY + targetY) / 2f
    return PositionAwarePathResult(path, lx, ly, 0f, 0f)
}

/**
 * Position-aware smooth step path with rounded corners.
 */
fun getSmoothStepPath(
    sourceX: Float, sourceY: Float, sourcePosition: Position,
    targetX: Float, targetY: Float, targetPosition: Position,
    borderRadius: Float = 5f
): PositionAwarePathResult {
    val isSourceHorizontal = sourcePosition == Position.LEFT || sourcePosition == Position.RIGHT
    val path = Path()
    path.moveTo(sourceX, sourceY)

    if (isSourceHorizontal) {
        val midX = (sourceX + targetX) / 2f
        val r = min(borderRadius, min(abs(midX - sourceX), abs(targetY - sourceY) / 2f))

        if (abs(targetY - sourceY) < 1f) {
            path.lineTo(targetX, targetY)
            return PositionAwarePathResult(path, (sourceX + targetX) / 2f, sourceY, 0f, 0f)
        }

        val goingDown = targetY > sourceY
        val goingRight = midX > sourceX
        val targetGoingRight = targetX > midX

        path.lineTo(midX - (if (goingRight) r else -r), sourceY)
        path.quadraticTo(midX, sourceY, midX, sourceY + (if (goingDown) r else -r))
        path.lineTo(midX, targetY - (if (goingDown) r else -r))
        path.quadraticTo(midX, targetY, midX + (if (targetGoingRight) r else -r), targetY)
        path.lineTo(targetX, targetY)
    } else {
        val midY = (sourceY + targetY) / 2f
        val r = min(borderRadius, min(abs(midY - sourceY), abs(targetX - sourceX) / 2f))

        if (abs(targetX - sourceX) < 1f) {
            path.lineTo(targetX, targetY)
            return PositionAwarePathResult(path, sourceX, (sourceY + targetY) / 2f, 0f, 0f)
        }

        val goingRight = targetX > sourceX
        val goingDown = midY > sourceY
        val targetGoingDown = targetY > midY

        path.lineTo(sourceX, midY - (if (goingDown) r else -r))
        path.quadraticTo(sourceX, midY, sourceX + (if (goingRight) r else -r), midY)
        path.lineTo(targetX - (if (goingRight) r else -r), midY)
        path.quadraticTo(targetX, midY, targetX, midY + (if (targetGoingDown) r else -r))
        path.lineTo(targetX, targetY)
    }

    val lx = (sourceX + targetX) / 2f
    val ly = (sourceY + targetY) / 2f
    return PositionAwarePathResult(path, lx, ly, 0f, 0f)
}

/**
 * Returns the appropriate path with full position awareness for the given edge type.
 */
fun getEdgePath(
    type: EdgeType,
    sourceX: Float, sourceY: Float, sourcePosition: Position,
    targetX: Float, targetY: Float, targetPosition: Position
): PositionAwarePathResult {
    return when (type) {
        EdgeType.STRAIGHT -> {
            val p = getStraightPath(sourceX, sourceY, targetX, targetY)
            PositionAwarePathResult(p, (sourceX + targetX) / 2f, (sourceY + targetY) / 2f, 0f, 0f)
        }
        EdgeType.STEP -> getStepPath(sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition)
        EdgeType.SMOOTHSTEP -> getSmoothStepPath(sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition)
        EdgeType.SIMPLEBEZIER -> getSimpleBezierPath(sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition)
        EdgeType.DEFAULT, EdgeType.BEZIER -> getBezierPath(sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition)
    }
}

/**
 * Returns the appropriate Path for the given edge type (simple 2-point variant).
 */
fun getEdgePath(
    type: EdgeType,
    sourceX: Float, sourceY: Float,
    targetX: Float, targetY: Float
): Path {
    return when (type) {
        EdgeType.STRAIGHT -> getStraightPath(sourceX, sourceY, targetX, targetY)
        EdgeType.STEP -> getStepPath(sourceX, sourceY, targetX, targetY)
        EdgeType.SMOOTHSTEP -> getSmoothStepPath(sourceX, sourceY, targetX, targetY)
        EdgeType.SIMPLEBEZIER -> getSimpleBezierPath(sourceX, sourceY, targetX, targetY)
        EdgeType.DEFAULT, EdgeType.BEZIER -> getBezierPath(sourceX, sourceY, targetX, targetY)
    }
}

/**
 * Returns the appropriate [EdgePathResult] for the given edge type, including label position.
 */
fun getEdgePathResult(
    type: EdgeType,
    sourceX: Float, sourceY: Float,
    targetX: Float, targetY: Float
): EdgePathResult {
    val path = getEdgePath(type, sourceX, sourceY, targetX, targetY)
    val mid = getEdgeMidpoint(type, sourceX, sourceY, targetX, targetY)
    return EdgePathResult(
        path = path,
        labelX = mid.x,
        labelY = mid.y,
        sourceX = sourceX,
        sourceY = sourceY,
        targetX = targetX,
        targetY = targetY
    )
}

/**
 * Returns the midpoint of an edge path, used for positioning labels.
 */
fun getEdgeMidpoint(
    type: EdgeType,
    sourceX: Float, sourceY: Float,
    targetX: Float, targetY: Float
): Offset {
    return when (type) {
        EdgeType.STRAIGHT, EdgeType.STEP, EdgeType.SMOOTHSTEP, EdgeType.SIMPLEBEZIER -> {
            Offset((sourceX + targetX) / 2f, (sourceY + targetY) / 2f)
        }
        EdgeType.DEFAULT, EdgeType.BEZIER -> {
            val controlOffset = max(abs(targetX - sourceX) / 2f, 50f)
            val t = 0.5f
            val mt = 0.5f
            val x = mt * mt * mt * sourceX +
                    3f * mt * mt * t * (sourceX + controlOffset) +
                    3f * mt * t * t * (targetX - controlOffset) +
                    t * t * t * targetX
            val y = mt * mt * mt * sourceY +
                    3f * mt * mt * t * sourceY +
                    3f * mt * t * t * targetY +
                    t * t * t * targetY
            Offset(x, y)
        }
    }
}

/**
 * Returns the angle of the edge path at the target endpoint (for marker rotation).
 */
fun getEdgeAngleAtEnd(
    type: EdgeType,
    sourceX: Float, sourceY: Float,
    targetX: Float, targetY: Float
): Float {
    return when (type) {
        EdgeType.STRAIGHT, EdgeType.SIMPLEBEZIER -> {
            atan2(targetY - sourceY, targetX - sourceX)
        }
        EdgeType.STEP, EdgeType.SMOOTHSTEP -> {
            val midX = (sourceX + targetX) / 2f
            atan2(0f, targetX - midX)
        }
        EdgeType.DEFAULT, EdgeType.BEZIER -> {
            val controlOffset = max(abs(targetX - sourceX) / 2f, 50f)
            atan2(0f, controlOffset * 3f)
        }
    }
}

/**
 * Returns the angle of the edge path at the source endpoint (for marker rotation).
 */
fun getEdgeAngleAtStart(
    type: EdgeType,
    sourceX: Float, sourceY: Float,
    targetX: Float, targetY: Float
): Float {
    return when (type) {
        EdgeType.STRAIGHT, EdgeType.SIMPLEBEZIER -> {
            atan2(targetY - sourceY, targetX - sourceX)
        }
        EdgeType.STEP, EdgeType.SMOOTHSTEP -> 0f
        EdgeType.DEFAULT, EdgeType.BEZIER -> {
            val controlOffset = max(abs(targetX - sourceX) / 2f, 50f)
            atan2(0f, controlOffset * 3f)
        }
    }
}
