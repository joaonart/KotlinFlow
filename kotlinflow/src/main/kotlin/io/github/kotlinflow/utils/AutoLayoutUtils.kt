package io.github.kotlinflow.utils

import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.types.NodeChange
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * Direction for hierarchical tree layouts.
 */
enum class LayoutDirection {
    TOP_TO_BOTTOM,
    LEFT_TO_RIGHT,
    BOTTOM_TO_TOP,
    RIGHT_TO_LEFT
}

/**
 * Algorithm selection for [computeAutoLayout].
 */
sealed interface LayoutAlgorithm {
    data class Tree(
        val direction: LayoutDirection = LayoutDirection.TOP_TO_BOTTOM,
        val nodeSpacing: Float = 50f,
        val levelSpacing: Float = 150f
    ) : LayoutAlgorithm

    data class ForceDirected(
        val iterations: Int = 100,
        val idealLength: Float = 200f,
        val repulsion: Float = 5000f
    ) : LayoutAlgorithm

    data class Grid(
        val columns: Int = 3,
        val nodeSpacing: Float = 50f
    ) : LayoutAlgorithm
}

/**
 * Computes layout changes for nodes using the specified algorithm.
 *
 * Returns a list of [NodeChange.Position] values that can be applied
 * via [applyNodeChanges].
 */
fun <T, E> computeAutoLayout(
    nodes: List<Node<T>>,
    edges: List<Edge<E>>,
    algorithm: LayoutAlgorithm,
    nodeSizes: Map<String, Size> = emptyMap()
): List<NodeChange<T>> {
    val mutableNodes = nodes.toMutableList()
    when (algorithm) {
        is LayoutAlgorithm.Tree -> {
            applyTreeLayout(
                nodes = mutableNodes,
                edges = edges,
                direction = algorithm.direction,
                nodeSpacing = algorithm.nodeSpacing,
                levelSpacing = algorithm.levelSpacing,
                nodeSizes = nodeSizes
            )
        }
        is LayoutAlgorithm.ForceDirected -> {
            applyForceDirectedLayout(
                nodes = mutableNodes,
                edges = edges,
                iterations = algorithm.iterations,
                idealLength = algorithm.idealLength,
                repulsion = algorithm.repulsion
            )
        }
        is LayoutAlgorithm.Grid -> {
            applyGridLayout(
                nodes = mutableNodes,
                columns = algorithm.columns,
                nodeSpacing = algorithm.nodeSpacing,
                nodeSizes = nodeSizes
            )
        }
    }
    return mutableNodes.map { node ->
        NodeChange.Position(id = node.id, position = node.position)
    }
}

// MARK: - Tree Layout

private fun <T, E> applyTreeLayout(
    nodes: MutableList<Node<T>>,
    edges: List<Edge<E>>,
    direction: LayoutDirection,
    nodeSpacing: Float,
    levelSpacing: Float,
    nodeSizes: Map<String, Size>
) {
    if (nodes.isEmpty()) return

    val children = mutableMapOf<String, MutableList<String>>()
    val inDegree = mutableMapOf<String, Int>()
    for (node in nodes) {
        children[node.id] = mutableListOf()
        inDegree[node.id] = 0
    }
    for (edge in edges) {
        if (children.containsKey(edge.source) && inDegree.containsKey(edge.target)) {
            children[edge.source]?.add(edge.target)
            inDegree[edge.target] = (inDegree[edge.target] ?: 0) + 1
        }
    }

    val roots = nodes.filter { inDegree[it.id] == 0 }.map { it.id }
    val startNodes = if (roots.isEmpty()) listOf(nodes.first().id) else roots

    val levels = mutableMapOf<String, Int>()
    val visited = mutableSetOf<String>()
    val queue = ArrayDeque<Pair<String, Int>>()

    for (root in startNodes) {
        queue.add(root to 0)
        visited.add(root)
    }

    while (queue.isNotEmpty()) {
        val (nodeId, level) = queue.removeFirst()
        levels[nodeId] = level
        for (child in children[nodeId] ?: emptyList()) {
            if (visited.add(child)) {
                queue.add(child to (level + 1))
            }
        }
    }

    // Disconnected nodes go to level 0
    for (node in nodes) {
        if (node.id !in visited) {
            levels[node.id] = 0
        }
    }

    val levelNodes = mutableMapOf<Int, MutableList<String>>()
    for (node in nodes) {
        val lvl = levels[node.id] ?: 0
        levelNodes.getOrPut(lvl) { mutableListOf() }.add(node.id)
    }
    val maxLevel = levelNodes.keys.maxOrNull() ?: 0

    for (level in 0..maxLevel) {
        val ids = levelNodes[level] ?: continue
        val horizontal = direction == LayoutDirection.LEFT_TO_RIGHT || direction == LayoutDirection.RIGHT_TO_LEFT
        val sizes = ids.map { nodeSizes[it] ?: Size(150f, 50f) }
        val lengths = sizes.map { if (horizontal) it.height else it.width }
        var offset = -(lengths.sum() + (ids.size - 1) * nodeSpacing) / 2f

        for (i in ids.indices) {
            val nodeId = ids[i]
            val index = nodes.indexOfFirst { it.id == nodeId }
            if (index == -1) continue

            val reverse = direction == LayoutDirection.BOTTOM_TO_TOP || direction == LayoutDirection.RIGHT_TO_LEFT
            val levelOffset = (if (reverse) maxLevel - level else level) * levelSpacing
            val pos = if (horizontal) {
                XYPosition(x = levelOffset, y = offset)
            } else {
                XYPosition(x = offset, y = levelOffset)
            }
            nodes[index] = nodes[index].copy(position = pos)
            offset += lengths[i] + nodeSpacing
        }
    }
}

// MARK: - Force Directed Layout

private fun <T, E> applyForceDirectedLayout(
    nodes: MutableList<Node<T>>,
    edges: List<Edge<E>>,
    iterations: Int = 100,
    idealLength: Float = 200f,
    repulsion: Float = 5000f
) {
    if (nodes.size <= 1 || iterations <= 0) return

    // Break symmetry when nodes overlap exactly
    for (i in 0 until nodes.size) {
        for (j in (i + 1) until nodes.size) {
            if (nodes[i].position.x == nodes[j].position.x && nodes[i].position.y == nodes[j].position.y) {
                nodes[j] = nodes[j].copy(
                    position = XYPosition(
                        x = nodes[j].position.x + j * 0.1f,
                        y = nodes[j].position.y + j * 0.1f
                    )
                )
            }
        }
    }

    val nodeIds = nodes.map { it.id }.toSet()
    val edgeNodeIds = edges.flatMap { listOf(it.source, it.target) }.filter { it in nodeIds }.toSet()

    val velocitiesX = mutableMapOf<String, Float>()
    val velocitiesY = mutableMapOf<String, Float>()
    for (node in nodes) {
        velocitiesX[node.id] = 0f
        velocitiesY[node.id] = 0f
    }

    val damping = 0.9f
    val dt = 0.1f
    val centerAttraction = 0.01f

    var cx = 0f
    var cy = 0f
    for (node in nodes) {
        cx += node.position.x
        cy += node.position.y
    }
    cx /= nodes.size.toFloat()
    cy /= nodes.size.toFloat()

    for (iter in 0 until iterations) {
        // Repulsive forces (all pairs)
        for (i in 0 until nodes.size) {
            for (j in (i + 1) until nodes.size) {
                val dx = nodes[j].position.x - nodes[i].position.x
                val dy = nodes[j].position.y - nodes[i].position.y
                val dist = max(hypot(dx, dy), 1f)
                val f = repulsion / (dist * dist)
                val fx = (dx / dist) * f * dt
                val fy = (dy / dist) * f * dt

                velocitiesX[nodes[i].id] = (velocitiesX[nodes[i].id] ?: 0f) - fx
                velocitiesY[nodes[i].id] = (velocitiesY[nodes[i].id] ?: 0f) - fy
                velocitiesX[nodes[j].id] = (velocitiesX[nodes[j].id] ?: 0f) + fx
                velocitiesY[nodes[j].id] = (velocitiesY[nodes[j].id] ?: 0f) + fy
            }
        }

        // Attractive forces (edges)
        for (edge in edges) {
            val si = nodes.indexOfFirst { it.id == edge.source }
            val ti = nodes.indexOfFirst { it.id == edge.target }
            if (si == -1 || ti == -1) continue

            val dx = nodes[ti].position.x - nodes[si].position.x
            val dy = nodes[ti].position.y - nodes[si].position.y
            val dist = max(hypot(dx, dy), 1f)
            val f = (dist - idealLength) * 0.1f
            val fx = (dx / dist) * f * dt
            val fy = (dy / dist) * f * dt

            velocitiesX[nodes[si].id] = (velocitiesX[nodes[si].id] ?: 0f) + fx
            velocitiesY[nodes[si].id] = (velocitiesY[nodes[si].id] ?: 0f) + fy
            velocitiesX[nodes[ti].id] = (velocitiesX[nodes[ti].id] ?: 0f) - fx
            velocitiesY[nodes[ti].id] = (velocitiesY[nodes[ti].id] ?: 0f) - fy
        }

        // Central attraction for disconnected nodes
        for (i in 0 until nodes.size) {
            if (nodes[i].id !in edgeNodeIds) {
                val dx = cx - nodes[i].position.x
                val dy = cy - nodes[i].position.y
                velocitiesX[nodes[i].id] = (velocitiesX[nodes[i].id] ?: 0f) + dx * centerAttraction * dt
                velocitiesY[nodes[i].id] = (velocitiesY[nodes[i].id] ?: 0f) + dy * centerAttraction * dt
            }
        }

        // Apply velocities with damping
        for (i in 0 until nodes.size) {
            if (nodes[i].draggable) {
                val vx = (velocitiesX[nodes[i].id] ?: 0f) * damping
                val vy = (velocitiesY[nodes[i].id] ?: 0f) * damping
                velocitiesX[nodes[i].id] = vx
                velocitiesY[nodes[i].id] = vy
                nodes[i] = nodes[i].copy(
                    position = XYPosition(
                        x = nodes[i].position.x + vx,
                        y = nodes[i].position.y + vy
                    )
                )
            }
        }
    }
}

// MARK: - Grid Layout

private fun <T> applyGridLayout(
    nodes: MutableList<Node<T>>,
    columns: Int = 3,
    nodeSpacing: Float = 50f,
    nodeSizes: Map<String, Size> = emptyMap()
) {
    if (nodes.isEmpty()) return
    val cols = min(max(columns, 1), nodes.size)
    val rows = (nodes.size - 1) / cols + 1

    val widths = FloatArray(cols) { 0f }
    val heights = FloatArray(rows) { 0f }

    for (i in nodes.indices) {
        val node = nodes[i]
        val size = nodeSizes[node.id] ?: Size(node.width ?: 150f, node.height ?: 50f)
        val col = i % cols
        val row = i / cols
        widths[col] = max(widths[col], size.width)
        heights[row] = max(heights[row], size.height)
    }

    val xOffsets = FloatArray(cols) { 0f }
    val yOffsets = FloatArray(rows) { 0f }

    for (i in 1 until cols) {
        xOffsets[i] = xOffsets[i - 1] + widths[i - 1] + nodeSpacing
    }
    for (i in 1 until rows) {
        yOffsets[i] = yOffsets[i - 1] + heights[i - 1] + nodeSpacing
    }

    for (i in nodes.indices) {
        val col = i % cols
        val row = i / cols
        nodes[i] = nodes[i].copy(
            position = XYPosition(x = xOffsets[col], y = yOffsets[row])
        )
    }
}
