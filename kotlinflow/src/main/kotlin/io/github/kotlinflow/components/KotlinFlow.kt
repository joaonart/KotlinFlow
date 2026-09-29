package io.github.kotlinflow.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.models.KotlinFlowTheme
import io.github.kotlinflow.models.MarkerType
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.state.KotlinFlowState
import io.github.kotlinflow.state.LocalKotlinFlowInstance
import io.github.kotlinflow.state.LocalKotlinFlowState
import io.github.kotlinflow.state.LocalNodesInitialized
import io.github.kotlinflow.types.AccessibilityConfig
import io.github.kotlinflow.types.AnyEdgeSnapshot
import io.github.kotlinflow.types.AnyNodeSnapshot
import io.github.kotlinflow.types.BackgroundVariant
import io.github.kotlinflow.types.BeforeDeleteResult
import io.github.kotlinflow.types.ColorMode
import io.github.kotlinflow.types.ConnectionMode
import io.github.kotlinflow.types.ConnectionState
import io.github.kotlinflow.types.CoordinateExtent
import io.github.kotlinflow.types.DefaultEdgeOptions
import io.github.kotlinflow.types.EdgeChange
import io.github.kotlinflow.types.EdgePathResult
import io.github.kotlinflow.types.FitViewOptions
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.KeyboardShortcuts
import io.github.kotlinflow.types.NodeChange
import io.github.kotlinflow.types.NodeHandle
import io.github.kotlinflow.types.NodeOrigin
import io.github.kotlinflow.types.OnConnectStartParams
import io.github.kotlinflow.types.PanOnScrollMode
import io.github.kotlinflow.types.Position
import io.github.kotlinflow.types.SelectionMode
import io.github.kotlinflow.types.ZIndexMode
import io.github.kotlinflow.utils.KotlinFlowInstance
import io.github.kotlinflow.utils.addEdge
import io.github.kotlinflow.utils.applyEdgeChanges
import io.github.kotlinflow.utils.applyNodeChanges
import io.github.kotlinflow.utils.elementsToDelete
import io.github.kotlinflow.utils.getEdgeAngleAtEnd
import io.github.kotlinflow.utils.getEdgeMidpoint
import io.github.kotlinflow.utils.getEdgePath
import io.github.kotlinflow.utils.getEdgePathResult
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Typealias for 100% API naming parity with SwiftFlow.
 */
@Composable
fun <NodeData, EdgeData> SwiftFlow(
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>,
    modifier: Modifier = Modifier,
    onNodesChange: ((List<NodeChange<NodeData>>) -> Unit)? = null,
    onEdgesChange: ((List<EdgeChange<EdgeData>>) -> Unit)? = null,
    onConnect: ((Connection) -> Unit)? = null,
    nodesDraggable: Boolean = true,
    nodesConnectable: Boolean = true,
    elementsSelectable: Boolean = true,
    panOnDrag: Boolean = true,
    zoomOnPinch: Boolean = true,
    zoomOnDoubleClick: Boolean = true,
    selectionOnDrag: Boolean = false,
    selectNodesOnDrag: Boolean = true,
    selectionMode: SelectionMode = SelectionMode.PARTIAL,
    connectionMode: ConnectionMode = ConnectionMode.STRICT,
    connectionLineType: EdgeType = EdgeType.DEFAULT,
    backgroundVariant: BackgroundVariant? = null,
    snapToGrid: Boolean = false,
    snapGrid: Pair<Float, Float> = 20f to 20f,
    theme: KotlinFlowTheme = KotlinFlowTheme.Default,
    colorMode: ColorMode = ColorMode.SYSTEM,
    zIndexMode: ZIndexMode = ZIndexMode.AUTO,
    nodeOrigin: NodeOrigin = NodeOrigin.TopLeft,
    coordinateExtent: CoordinateExtent = CoordinateExtent.Infinite,
    fitView: Boolean = false,
    fitViewOptions: FitViewOptions? = null,
    defaultEdgeOptions: DefaultEdgeOptions? = null,
    keyboardShortcuts: KeyboardShortcuts = KeyboardShortcuts.Default,
    accessibilityConfig: AccessibilityConfig = AccessibilityConfig.Default,
    onPaneClick: (() -> Unit)? = null,
    onViewportChange: ((Viewport) -> Unit)? = null,
    onSelectionChange: ((List<Node<NodeData>>, List<Edge<EdgeData>>) -> Unit)? = null,
    isValidConnection: ((Connection) -> Boolean)? = null,
    onNodeDragStart: ((Node<NodeData>) -> Unit)? = null,
    onNodeDrag: ((Node<NodeData>) -> Unit)? = null,
    onNodeDragStop: ((Node<NodeData>) -> Unit)? = null,
    onConnectStart: ((OnConnectStartParams) -> Unit)? = null,
    onConnectEnd: (() -> Unit)? = null,
    onBeforeDelete: (suspend (List<Node<NodeData>>, List<Edge<EdgeData>>) -> BeforeDeleteResult<NodeData, EdgeData>?)? = null,
    onNodesDelete: ((List<Node<NodeData>>) -> Unit)? = null,
    onEdgesDelete: ((List<Edge<EdgeData>>) -> Unit)? = null,
    onNodeClick: ((Node<NodeData>) -> Unit)? = null,
    onNodeDoubleClick: ((Node<NodeData>) -> Unit)? = null,
    onEdgeClick: ((Edge<EdgeData>) -> Unit)? = null,
    onEdgeDoubleClick: ((Edge<EdgeData>) -> Unit)? = null,
    onReconnect: ((Edge<EdgeData>, Connection) -> Unit)? = null,
    onMoveStart: ((Viewport) -> Unit)? = null,
    onMove: ((Viewport) -> Unit)? = null,
    onMoveEnd: ((Viewport) -> Unit)? = null,
    onInit: (() -> Unit)? = null,
    connectionLineContent: ((Connection, Offset, Offset) -> Unit)? = null,
    kotlinFlowInstance: KotlinFlowInstance? = null,
    edgeContent: ((Edge<EdgeData>, EdgePathResult) -> Unit)? = null,
    overlay: @Composable () -> Unit = {},
    nodeContent: @Composable (Node<NodeData>) -> Unit
) {
    KotlinFlow(
        nodes = nodes,
        edges = edges,
        modifier = modifier,
        onNodesChange = onNodesChange,
        onEdgesChange = onEdgesChange,
        onConnect = onConnect,
        nodesDraggable = nodesDraggable,
        nodesConnectable = nodesConnectable,
        elementsSelectable = elementsSelectable,
        panOnDrag = panOnDrag,
        zoomOnPinch = zoomOnPinch,
        zoomOnDoubleClick = zoomOnDoubleClick,
        selectionOnDrag = selectionOnDrag,
        selectNodesOnDrag = selectNodesOnDrag,
        selectionMode = selectionMode,
        connectionMode = connectionMode,
        connectionLineType = connectionLineType,
        backgroundVariant = backgroundVariant,
        snapToGrid = snapToGrid,
        snapGrid = snapGrid,
        theme = theme,
        colorMode = colorMode,
        zIndexMode = zIndexMode,
        nodeOrigin = nodeOrigin,
        coordinateExtent = coordinateExtent,
        fitView = fitView,
        fitViewOptions = fitViewOptions,
        defaultEdgeOptions = defaultEdgeOptions,
        keyboardShortcuts = keyboardShortcuts,
        accessibilityConfig = accessibilityConfig,
        onPaneClick = onPaneClick,
        onViewportChange = onViewportChange,
        onSelectionChange = onSelectionChange,
        isValidConnection = isValidConnection,
        onNodeDragStart = onNodeDragStart,
        onNodeDrag = onNodeDrag,
        onNodeDragStop = onNodeDragStop,
        onConnectStart = onConnectStart,
        onConnectEnd = onConnectEnd,
        onBeforeDelete = onBeforeDelete,
        onNodesDelete = onNodesDelete,
        onEdgesDelete = onEdgesDelete,
        onNodeClick = onNodeClick,
        onNodeDoubleClick = onNodeDoubleClick,
        onEdgeClick = onEdgeClick,
        onEdgeDoubleClick = onEdgeDoubleClick,
        onReconnect = onReconnect,
        onMoveStart = onMoveStart,
        onMove = onMove,
        onMoveEnd = onMoveEnd,
        onInit = onInit,
        connectionLineContent = connectionLineContent,
        kotlinFlowInstance = kotlinFlowInstance,
        edgeContent = edgeContent,
        overlay = overlay,
        nodeContent = nodeContent
    )
}

/**
 * The main interactive canvas for rendering and editing node graphs.
 *
 * [KotlinFlow] renders nodes and edges, manages panning, zooming, selection,
 * connection drawing, and custom overlays.
 */
@Composable
fun <NodeData, EdgeData> KotlinFlow(
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>,
    modifier: Modifier = Modifier,
    onNodesChange: ((List<NodeChange<NodeData>>) -> Unit)? = null,
    onEdgesChange: ((List<EdgeChange<EdgeData>>) -> Unit)? = null,
    onConnect: ((Connection) -> Unit)? = null,
    nodesDraggable: Boolean = true,
    nodesConnectable: Boolean = true,
    elementsSelectable: Boolean = true,
    panOnDrag: Boolean = true,
    zoomOnPinch: Boolean = true,
    zoomOnDoubleClick: Boolean = true,
    selectionOnDrag: Boolean = false,
    selectNodesOnDrag: Boolean = true,
    selectionMode: SelectionMode = SelectionMode.PARTIAL,
    connectionMode: ConnectionMode = ConnectionMode.STRICT,
    connectionLineType: EdgeType = EdgeType.DEFAULT,
    backgroundVariant: BackgroundVariant? = null,
    snapToGrid: Boolean = false,
    snapGrid: Pair<Float, Float> = 20f to 20f,
    theme: KotlinFlowTheme = KotlinFlowTheme.Default,
    colorMode: ColorMode = ColorMode.SYSTEM,
    zIndexMode: ZIndexMode = ZIndexMode.AUTO,
    nodeOrigin: NodeOrigin = NodeOrigin.TopLeft,
    coordinateExtent: CoordinateExtent = CoordinateExtent.Infinite,
    fitView: Boolean = false,
    fitViewOptions: FitViewOptions? = null,
    defaultEdgeOptions: DefaultEdgeOptions? = null,
    keyboardShortcuts: KeyboardShortcuts = KeyboardShortcuts.Default,
    accessibilityConfig: AccessibilityConfig = AccessibilityConfig.Default,
    onPaneClick: (() -> Unit)? = null,
    onViewportChange: ((Viewport) -> Unit)? = null,
    onSelectionChange: ((List<Node<NodeData>>, List<Edge<EdgeData>>) -> Unit)? = null,
    isValidConnection: ((Connection) -> Boolean)? = null,
    onNodeDragStart: ((Node<NodeData>) -> Unit)? = null,
    onNodeDrag: ((Node<NodeData>) -> Unit)? = null,
    onNodeDragStop: ((Node<NodeData>) -> Unit)? = null,
    onConnectStart: ((OnConnectStartParams) -> Unit)? = null,
    onConnectEnd: (() -> Unit)? = null,
    onBeforeDelete: (suspend (List<Node<NodeData>>, List<Edge<EdgeData>>) -> BeforeDeleteResult<NodeData, EdgeData>?)? = null,
    onNodesDelete: ((List<Node<NodeData>>) -> Unit)? = null,
    onEdgesDelete: ((List<Edge<EdgeData>>) -> Unit)? = null,
    onNodeClick: ((Node<NodeData>) -> Unit)? = null,
    onNodeDoubleClick: ((Node<NodeData>) -> Unit)? = null,
    onEdgeClick: ((Edge<EdgeData>) -> Unit)? = null,
    onEdgeDoubleClick: ((Edge<EdgeData>) -> Unit)? = null,
    onReconnect: ((Edge<EdgeData>, Connection) -> Unit)? = null,
    onMoveStart: ((Viewport) -> Unit)? = null,
    onMove: ((Viewport) -> Unit)? = null,
    onMoveEnd: ((Viewport) -> Unit)? = null,
    onInit: (() -> Unit)? = null,
    connectionLineContent: ((Connection, Offset, Offset) -> Unit)? = null,
    kotlinFlowInstance: KotlinFlowInstance? = null,
    edgeContent: ((Edge<EdgeData>, EdgePathResult) -> Unit)? = null,
    overlay: @Composable () -> Unit = {},
    nodeContent: @Composable (Node<NodeData>) -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val effectiveTheme = remember(theme, colorMode, isSystemDark) {
        if (theme != KotlinFlowTheme.Default) {
            theme
        } else {
            when (colorMode) {
                ColorMode.LIGHT -> KotlinFlowTheme.Light
                ColorMode.DARK -> KotlinFlowTheme.Dark
                ColorMode.SYSTEM -> if (isSystemDark) KotlinFlowTheme.Dark else KotlinFlowTheme.Light
            }
        }
    }

    val flowState = remember { KotlinFlowState() }
    flowState.theme = effectiveTheme
    val scope = rememberCoroutineScope()

    // Viewport state
    var viewport by remember { mutableStateOf(Viewport.Identity) }
    var viewSize by remember { mutableStateOf(Size.Zero) }

    // Measured sizes of nodes
    val nodeSizes = remember { mutableStateMapOf<String, Size>() }

    // Selection marquee
    var selectionBoxStart by remember { mutableStateOf<Offset?>(null) }
    var selectionBoxCurrent by remember { mutableStateOf<Offset?>(null) }

    // Drafting connection state
    var draftingStart by remember { mutableStateOf<Offset?>(null) }
    var draftingCurrent by remember { mutableStateOf<Offset?>(null) }
    var draftingSourceHandle by remember { mutableStateOf<NodeHandle?>(null) }
    var draftingSourceNodeId by remember { mutableStateOf<String?>(null) }

    // Sync flowState viewSize
    LaunchedEffect(viewSize) {
        flowState.viewSize = viewSize
        kotlinFlowInstance?.viewSize = viewSize
    }

    LaunchedEffect(nodes) {
        flowState.nodes = nodes.map { AnyNodeSnapshot.from(it) }
    }

    LaunchedEffect(edges) {
        flowState.edges = edges.map { AnyEdgeSnapshot.from(it) }

        // Build connections map only when edges change
        val map = mutableMapOf<String, MutableList<Connection>>()
        for (edge in edges) {
            val conn = Connection(edge.source, edge.target, edge.sourceHandle, edge.targetHandle)
            map.getOrPut(edge.source) { mutableListOf() }.add(conn)
            map.getOrPut(edge.target) { mutableListOf() }.add(conn)
        }
        flowState.connectionsMap = map
    }

    LaunchedEffect(Unit) {
        onInit?.invoke()
        if (fitView) {
            flowState.fitView(
                padding = fitViewOptions?.padding ?: 80f,
                maxZoom = fitViewOptions?.maxZoom ?: 1.5f,
                animated = (fitViewOptions?.durationMillis ?: 0L) > 0L
            )
        }
    }

    // Connect viewport mutations
    flowState.internalApplyViewport = { newVp, _ ->
        val clamped = newVp.copy(zoom = newVp.clampedZoom)
        viewport = clamped
        flowState.viewport = clamped
        kotlinFlowInstance?.viewport = clamped
        onViewportChange?.invoke(clamped)
    }

    kotlinFlowInstance?.internalApplyViewport = { newVp, _ ->
        val clamped = newVp.copy(zoom = newVp.clampedZoom)
        viewport = clamped
        flowState.viewport = clamped
        kotlinFlowInstance?.viewport = clamped
        onViewportChange?.invoke(clamped)
    }

    kotlinFlowInstance?.internalDeleteElements = { nodeIds, edgeIds ->
        val res = elementsToDelete(nodes, edges, nodeIds, edgeIds)
        onNodesChange?.invoke(res.nodes.map { NodeChange.Remove(it.id) })
        onEdgesChange?.invoke(res.edges.map { EdgeChange.Remove(it.id) })
        onNodesDelete?.invoke(res.nodes)
        onEdgesDelete?.invoke(res.edges)
    }

    CompositionLocalProvider(
        LocalKotlinFlowState provides flowState,
        LocalKotlinFlowInstance provides kotlinFlowInstance,
        LocalNodesInitialized provides (nodes.isNotEmpty() && nodes.all { it.width != null || nodeSizes.containsKey(it.id) })
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .clipToBounds()
                .background(effectiveTheme.canvasBackgroundColor)
                .onGloballyPositioned { coordinates ->
                    viewSize = Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())
                }
                .pointerInput(panOnDrag, zoomOnPinch, flowState.isInteractive) {
                    if (!flowState.isInteractive) return@pointerInput
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        if (panOnDrag || zoomOnPinch) {
                            val newZoom = if (zoomOnPinch) (viewport.zoom * zoom).coerceIn(Viewport.MinZoom, Viewport.MaxZoom) else viewport.zoom
                            val newX = if (panOnDrag) viewport.x + pan.x else viewport.x
                            val newY = if (panOnDrag) viewport.y + pan.y else viewport.y
                            val newVp = Viewport(newX, newY, newZoom)
                            viewport = newVp
                            flowState.viewport = newVp
                            kotlinFlowInstance?.viewport = newVp
                            onViewportChange?.invoke(newVp)
                        }
                    }
                }
                .pointerInput(zoomOnDoubleClick, flowState.isInteractive) {
                    if (!flowState.isInteractive) return@pointerInput
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            if (zoomOnDoubleClick) {
                                flowState.zoomTo(if (viewport.zoom >= 1.5f) 1f else viewport.zoom * 1.5f)
                            }
                        },
                        onTap = {
                            onPaneClick?.invoke()
                            // Deselect elements on empty pane click
                            val nodeChanges = nodes.filter { it.selected }.map { NodeChange.Selection(it.id, false) }
                            val edgeChanges = edges.filter { it.selected }.map { EdgeChange.Selection(it.id, false) }
                            if (nodeChanges.isNotEmpty()) onNodesChange?.invoke(nodeChanges)
                            if (edgeChanges.isNotEmpty()) onEdgesChange?.invoke(edgeChanges)
                        }
                    )
                }
        ) {
            // Optional Background pattern
            if (backgroundVariant != null && backgroundVariant != io.github.kotlinflow.types.BackgroundVariant.NONE) {
                Background(
                    variant = backgroundVariant,
                    color = effectiveTheme.gridColor,
                    gap = effectiveTheme.gridSpacing
                )
            }

            // Canvas content layer: transformed by viewport zoom and pan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = viewport.zoom
                        scaleY = viewport.zoom
                        translationX = viewport.x
                        translationY = viewport.y
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
                    }
            ) {
                // 1. Render Edges Layer
                EdgesLayer(
                    edges = edges,
                    nodes = nodes,
                    nodeSizes = nodeSizes,
                    theme = effectiveTheme,
                    onEdgeClick = onEdgeClick,
                    onEdgeDoubleClick = onEdgeDoubleClick,
                    onEdgesChange = onEdgesChange,
                    edgeContent = edgeContent
                )

                // 2. Render Drafting Connection Line (if in progress)
                if (draftingStart != null && draftingCurrent != null && draftingSourceNodeId != null) {
                    DraftingConnectionLine(
                        start = draftingStart!!,
                        current = draftingCurrent!!,
                        type = connectionLineType,
                        theme = effectiveTheme
                    )
                }

                // 3. Render Nodes Layer
                for (node in nodes) {
                    if (node.hidden) continue

                    val nodeSize = nodeSizes[node.id] ?: Size(node.width ?: 150f, node.height ?: 50f)
                    val isNodeDraggable = nodesDraggable && node.draggable && flowState.isInteractive

                    key(node.id) {
                        FlowNodeItem(
                            node = node,
                            nodeOrigin = nodeOrigin,
                            nodeSize = nodeSize,
                            zIndexMode = zIndexMode,
                            isNodeDraggable = isNodeDraggable,
                            elementsSelectable = elementsSelectable,
                            zoom = viewport.zoom,
                            snapToGrid = snapToGrid,
                            snapGrid = snapGrid,
                            coordinateExtent = coordinateExtent,
                            theme = effectiveTheme,
                            onNodeDragStart = onNodeDragStart,
                            onNodeDrag = onNodeDrag,
                            onNodeDragStop = onNodeDragStop,
                            onNodeClick = onNodeClick,
                            onNodeDoubleClick = onNodeDoubleClick,
                            onNodesChange = onNodesChange,
                            onSizeMeasured = { sz ->
                                if (nodeSizes[node.id] != sz) {
                                    nodeSizes[node.id] = sz
                                    flowState.nodeSizes = nodeSizes.toMap()
                                }
                            },
                            content = nodeContent
                        )
                    }
                }
            }

            // 4. Selection box overlay (if marquee drag is active)
            if (selectionBoxStart != null && selectionBoxCurrent != null) {
                val p1 = selectionBoxStart!!
                val p2 = selectionBoxCurrent!!
                val left = min(p1.x, p2.x)
                val top = min(p1.y, p2.y)
                val right = max(p1.x, p2.x)
                val bottom = max(p1.y, p2.y)

                val density = LocalDensity.current
                val boxWidth = with(density) { (right - left).toDp() }
                val boxHeight = with(density) { (bottom - top).toDp() }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                        .size(boxWidth, boxHeight)
                        .background(effectiveTheme.selectionBoxColor)
                        .border(1.dp, effectiveTheme.selectionBoxBorderColor)
                )
            }

            // 5. User overlay layer (Controls, MiniMap, Panel, etc.)
            overlay()
        }
    }
}

// MARK: - Internal FlowNodeItem Component

@Composable
private fun <NodeData> FlowNodeItem(
    node: Node<NodeData>,
    nodeOrigin: NodeOrigin,
    nodeSize: Size,
    zIndexMode: ZIndexMode,
    isNodeDraggable: Boolean,
    elementsSelectable: Boolean,
    zoom: Float,
    snapToGrid: Boolean,
    snapGrid: Pair<Float, Float>,
    coordinateExtent: CoordinateExtent,
    theme: KotlinFlowTheme,
    onNodeDragStart: ((Node<NodeData>) -> Unit)?,
    onNodeDrag: ((Node<NodeData>) -> Unit)?,
    onNodeDragStop: ((Node<NodeData>) -> Unit)?,
    onNodeClick: ((Node<NodeData>) -> Unit)?,
    onNodeDoubleClick: ((Node<NodeData>) -> Unit)?,
    onNodesChange: ((List<NodeChange<NodeData>>) -> Unit)?,
    onSizeMeasured: (Size) -> Unit,
    content: @Composable (Node<NodeData>) -> Unit
) {
    val currentNode by rememberUpdatedState(node)
    val currentZoom by rememberUpdatedState(zoom)
    val currentSnapToGrid by rememberUpdatedState(snapToGrid)
    val currentSnapGrid by rememberUpdatedState(snapGrid)
    val currentExtent by rememberUpdatedState(coordinateExtent)
    val currentOnNodesChange by rememberUpdatedState(onNodesChange)
    val currentOnNodeDrag by rememberUpdatedState(onNodeDrag)
    val currentOnNodeDragStart by rememberUpdatedState(onNodeDragStart)
    val currentOnNodeDragStop by rememberUpdatedState(onNodeDragStop)
    val currentOnNodeClick by rememberUpdatedState(onNodeClick)
    val currentOnNodeDoubleClick by rememberUpdatedState(onNodeDoubleClick)

    var dragPosition by remember { mutableStateOf(node.position) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (node.position.x - nodeOrigin.x * nodeSize.width).roundToInt(),
                    y = (node.position.y - nodeOrigin.y * nodeSize.height).roundToInt()
                )
            }
            .zIndex((if (node.selected && zIndexMode != ZIndexMode.MANUAL) node.zIndex + 1000 else node.zIndex).toFloat())
            .onGloballyPositioned { coordinates ->
                val sz = Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())
                onSizeMeasured(sz)
            }
            .pointerInput(node.id, isNodeDraggable) {
                if (!isNodeDraggable) return@pointerInput
                detectDragGestures(
                    onDragStart = {
                        dragPosition = currentNode.position
                        currentOnNodeDragStart?.invoke(currentNode)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val z = currentZoom.coerceAtLeast(0.01f)
                        val deltaX = dragAmount.x / z
                        val deltaY = dragAmount.y / z

                        val updatedX = dragPosition.x + deltaX
                        val updatedY = dragPosition.y + deltaY
                        var newPos = XYPosition(updatedX, updatedY)
                        if (currentSnapToGrid) {
                            newPos = newPos.snapped(currentSnapGrid.first, currentSnapGrid.second)
                        }
                        newPos = currentExtent.clamp(newPos)
                        dragPosition = newPos

                        currentOnNodesChange?.invoke(listOf(NodeChange.Position(currentNode.id, newPos)))
                        currentOnNodeDrag?.invoke(currentNode.copy(position = newPos))
                    },
                    onDragEnd = {
                        currentOnNodeDragStop?.invoke(currentNode)
                    },
                    onDragCancel = {
                        currentOnNodeDragStop?.invoke(currentNode)
                    }
                )
            }
            .pointerInput(node.id) {
                detectTapGestures(
                    onTap = {
                        currentOnNodeClick?.invoke(currentNode)
                        if (elementsSelectable && currentNode.selectable) {
                            val changes = listOf(NodeChange.Selection(currentNode.id, !currentNode.selected))
                            currentOnNodesChange?.invoke(changes)
                        }
                    },
                    onDoubleTap = {
                        currentOnNodeDoubleClick?.invoke(currentNode)
                    }
                )
            }
            .then(
                if (node.selected) {
                    Modifier.border(
                        width = node.style?.borderWidth?.dp ?: theme.nodeSelectedBorderWidth.dp,
                        color = node.style?.borderColor ?: theme.nodeSelectedBorderColor,
                        shape = RoundedCornerShape(node.style?.borderRadius?.dp ?: 8.dp)
                    )
                } else Modifier
            )
    ) {
        content(node)
    }
}

// MARK: - Internal Edges Layer

@Composable
private fun <NodeData, EdgeData> EdgesLayer(
    edges: List<Edge<EdgeData>>,
    nodes: List<Node<NodeData>>,
    nodeSizes: Map<String, Size>,
    theme: KotlinFlowTheme,
    onEdgeClick: ((Edge<EdgeData>) -> Unit)?,
    onEdgeDoubleClick: ((Edge<EdgeData>) -> Unit)?,
    onEdgesChange: ((List<EdgeChange<EdgeData>>) -> Unit)?,
    edgeContent: ((Edge<EdgeData>, EdgePathResult) -> Unit)?
) {
    val nodeMap = remember(nodes) { nodes.associateBy { it.id } }
    val hasAnimatedEdges = remember(edges) { edges.any { it.animated } }

    val dashPhase = if (hasAnimatedEdges) {
        val infiniteTransition = rememberInfiniteTransition(label = "edgeDash")
        val phase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 60f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "dashPhase"
        )
        phase
    } else 0f

    val markerPath = remember { Path() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (edge in edges) {
            if (edge.hidden) continue

            val sourceNode = nodeMap[edge.source] ?: continue
            val targetNode = nodeMap[edge.target] ?: continue

            val sourceSize = nodeSizes[sourceNode.id] ?: Size(sourceNode.width ?: 150f, sourceNode.height ?: 50f)
            val targetSize = nodeSizes[targetNode.id] ?: Size(targetNode.width ?: 150f, targetNode.height ?: 50f)

            val sourceX = sourceNode.position.x + sourceSize.width
            val sourceY = sourceNode.position.y + sourceSize.height / 2f
            val targetX = targetNode.position.x
            val targetY = targetNode.position.y + targetSize.height / 2f

            val strokeColor = edge.style?.strokeColor
                ?: if (edge.selected) theme.edgeSelectedColor else theme.edgeColor
            val strokeW = edge.style?.strokeWidth
                ?: if (edge.selected) theme.edgeSelectedWidth else theme.edgeWidth

            val path = getEdgePath(edge.type, sourceX, sourceY, targetX, targetY)

            val pathEffect = if (edge.animated) {
                PathEffect.dashPathEffect(floatArrayOf(20f, 10f), dashPhase)
            } else null

            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(
                    width = strokeW,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = pathEffect
                )
            )

            // Draw End Marker if specified
            if (edge.markerEnd != null) {
                val angle = getEdgeAngleAtEnd(edge.type, sourceX, sourceY, targetX, targetY)
                val mSize = edge.markerEnd.width
                val handleOffset = 6.dp.toPx()
                val tipX = targetX - handleOffset * cos(angle)
                val tipY = targetY - handleOffset * sin(angle)
                markerPath.reset()
                markerPath.moveTo(tipX, tipY)
                markerPath.lineTo(
                    tipX - mSize * cos(angle - 0.5f),
                    tipY - mSize * sin(angle - 0.5f)
                )
                markerPath.lineTo(
                    tipX - mSize * cos(angle + 0.5f),
                    tipY - mSize * sin(angle + 0.5f)
                )
                markerPath.close()

                if (edge.markerEnd.type == MarkerType.ARROW_CLOSED) {
                    drawPath(markerPath, color = strokeColor)
                } else {
                    drawPath(markerPath, color = strokeColor, style = Stroke(width = strokeW))
                }
            }
        }
    }

    // Render Edge Labels / Custom Edge Content
    for (edge in edges) {
        if (edge.hidden) continue
        val sourceNode = nodeMap[edge.source] ?: continue
        val targetNode = nodeMap[edge.target] ?: continue

        val sourceSize = nodeSizes[sourceNode.id] ?: Size(sourceNode.width ?: 150f, sourceNode.height ?: 50f)
        val targetSize = nodeSizes[targetNode.id] ?: Size(targetNode.width ?: 150f, targetNode.height ?: 50f)

        val sourceX = sourceNode.position.x + sourceSize.width
        val sourceY = sourceNode.position.y + sourceSize.height / 2f
        val targetX = targetNode.position.x
        val targetY = targetNode.position.y + targetSize.height / 2f

        val pathResult = getEdgePathResult(edge.type, sourceX, sourceY, targetX, targetY)

        if (edgeContent != null) {
            edgeContent(edge, pathResult)
        } else if (edge.label != null) {
            var labelSize by remember(edge.id) { mutableStateOf(IntSize.Zero) }
            EdgeText(
                label = edge.label,
                modifier = Modifier
                    .onSizeChanged { labelSize = it }
                    .offset {
                        IntOffset(
                            (pathResult.labelX - labelSize.width / 2f).roundToInt(),
                            (pathResult.labelY - labelSize.height / 2f).roundToInt()
                        )
                    }
                    .clickable {
                        onEdgeClick?.invoke(edge)
                        onEdgesChange?.invoke(listOf(EdgeChange.Selection(edge.id, !edge.selected)))
                    },
                textStyle = theme.edgeLabelTextStyle,
                textColor = theme.edgeLabelColor,
                backgroundColor = theme.edgeLabelBackgroundColor,
                borderColor = theme.edgeLabelBorderColor
            )
        }
    }
}

// MARK: - Internal Drafting Connection Line

@Composable
private fun DraftingConnectionLine(
    start: Offset,
    current: Offset,
    type: EdgeType,
    theme: KotlinFlowTheme
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = getEdgePath(type, start.x, start.y, current.x, current.y)
        drawPath(
            path = path,
            color = theme.edgeSelectedColor,
            style = Stroke(
                width = 2.5f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        )
    }
}
