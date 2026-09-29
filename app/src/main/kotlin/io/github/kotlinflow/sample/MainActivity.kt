package io.github.kotlinflow.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.github.kotlinflow.components.DiamondShape
import io.github.kotlinflow.types.EdgeStyle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kotlinflow.components.Background
import io.github.kotlinflow.components.Controls
import io.github.kotlinflow.components.Handle
import io.github.kotlinflow.components.KotlinFlow
import io.github.kotlinflow.components.MiniMap
import io.github.kotlinflow.components.NodeToolbar
import io.github.kotlinflow.components.Panel
import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EdgeMarker
import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.KotlinFlowTheme
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.types.BackgroundVariant
import io.github.kotlinflow.types.ColorMode
import io.github.kotlinflow.types.EdgeChange
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.NodeChange
import io.github.kotlinflow.types.PanelPosition
import io.github.kotlinflow.types.Position
import io.github.kotlinflow.utils.KotlinFlowInstance
import io.github.kotlinflow.utils.LayoutAlgorithm
import io.github.kotlinflow.utils.LayoutDirection
import io.github.kotlinflow.utils.addEdge
import io.github.kotlinflow.utils.applyEdgeChanges
import io.github.kotlinflow.utils.applyNodeChanges
import io.github.kotlinflow.utils.computeAutoLayout
import io.github.kotlinflow.utils.fromJSONString
import io.github.kotlinflow.utils.toJSONString
import kotlinx.coroutines.delay

data class SampleNodeData(
    val title: String,
    val description: String,
    val iconType: String = "process"
)

enum class N8nNodeKind {
    AGENT,
    ACTION_SQUARE,
    ADD_BUTTON,
    SUB_NODE_CIRCLE
}

data class N8nNodeData(
    val title: String,
    val subtitle: String? = null,
    val kind: N8nNodeKind = N8nNodeKind.ACTION_SQUARE,
    val topBadge: String? = null,
    val iconType: String = "edit",
    val accentColor: Color = Color(0xFF5E5CE6)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            MaterialTheme(
                colorScheme = if (isDarkTheme) {
                    darkColorScheme(
                        primary = Color(0xFF00E5FF),
                        background = Color(0xFF121212),
                        surface = Color(0xFF1E1E1E),
                        onBackground = Color.White,
                        onSurface = Color.White
                    )
                } else {
                    lightColorScheme(
                        primary = Color(0xFF1E88E5),
                        background = Color(0xFFF8F9FA),
                        surface = Color.White,
                        onBackground = Color.Black,
                        onSurface = Color.Black
                    )
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KotlinFlowSampleApp(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KotlinFlowSampleApp(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var isConfigPanelOpen by rememberSaveable { mutableStateOf(false) }
    val tabs = listOf("Interactive Canvas", "Auto Layout", "JSON Serialization")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "KotlinFlow",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF1E88E5)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "for Android",
                            fontSize = 14.sp,
                            color = if (isDarkTheme) Color(0xFF90A4AE) else Color.Gray
                        )
                    }
                },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = { isConfigPanelOpen = !isConfigPanelOpen }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = if (isConfigPanelOpen) "Close Settings Panel" else "Open Settings Panel",
                                tint = if (isConfigPanelOpen) {
                                    if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF1E88E5)
                                } else {
                                    if (isDarkTheme) Color(0xFF90A4AE) else Color(0xFF546E7A)
                                }
                            )
                        }
                    }
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFF5C6BC0)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) {
                                    if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF1E88E5)
                                } else {
                                    if (isDarkTheme) Color(0xFF888888) else Color.Gray
                                }
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> InteractiveCanvasDemo(
                    isDarkTheme = isDarkTheme,
                    isConfigPanelOpen = isConfigPanelOpen,
                    onCloseConfigPanel = { isConfigPanelOpen = false }
                )
                1 -> AutoLayoutDemo(isDarkTheme = isDarkTheme)
                2 -> SerializationDemo()
            }
        }
    }
}

@Composable
fun InteractiveCanvasDemo(
    isDarkTheme: Boolean,
    isConfigPanelOpen: Boolean = false,
    onCloseConfigPanel: () -> Unit = {}
) {
    var isN8nView by rememberSaveable { mutableStateOf(true) }
    var isCompactNodes by rememberSaveable { mutableStateOf(false) }

    // Standard DAG pipeline nodes (symmetrically balanced diamond layout)
    var standardNodes by remember {
        mutableStateOf(
            listOf(
                Node(
                    id = "1",
                    position = XYPosition(60f, 360f),
                    data = SampleNodeData("Input Data", "Initial event stream", "input"),
                    type = "input"
                ),
                Node(
                    id = "2",
                    position = XYPosition(700f, 140f),
                    data = SampleNodeData("Data Processing", "Transform & validate payload", "process"),
                    type = "process"
                ),
                Node(
                    id = "3",
                    position = XYPosition(700f, 580f),
                    data = SampleNodeData("Filter", "Discard invalid records", "filter"),
                    type = "filter"
                ),
                Node(
                    id = "4",
                    position = XYPosition(1340f, 360f),
                    data = SampleNodeData("Output Target", "Sinks to storage or API", "output"),
                    type = "output"
                )
            )
        )
    }

    var standardEdges by remember {
        mutableStateOf(
            listOf(
                Edge<EmptyEdgeData>(
                    id = "e1-2",
                    source = "1",
                    target = "2",
                    animated = true,
                    label = "valid events",
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "e1-3",
                    source = "1",
                    target = "3",
                    animated = true,
                    label = "raw stream",
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "e2-4",
                    source = "2",
                    target = "4",
                    animated = true,
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "e3-4",
                    source = "3",
                    target = "4",
                    animated = true,
                    markerEnd = EdgeMarker.ArrowClosed
                )
            )
        )
    }

    // n8n workflow nodes and edges (authentic n8n AI agent canvas layout)
    var n8nNodes by remember {
        mutableStateOf(
            listOf(
                // 1. Meeting Availability Agent (Main Agent wide card)
                Node(
                    id = "agent",
                    position = XYPosition(100f, 200f),
                    data = N8nNodeData(
                        title = "Meeting\nAvailability Agent",
                        subtitle = "Tools Agent",
                        kind = N8nNodeKind.AGENT,
                        iconType = "robot"
                    ),
                    type = "n8n"
                ),
                // 2. Generate Message (Action square)
                Node(
                    id = "generate-msg",
                    position = XYPosition(1150f, 215f),
                    data = N8nNodeData(
                        title = "Generate Message",
                        subtitle = "manual",
                        kind = N8nNodeKind.ACTION_SQUARE,
                        iconType = "edit",
                        accentColor = Color(0xFF5E5CE6)
                    ),
                    type = "n8n"
                ),
                // 3. Send for Human Approval (Gmail action square)
                Node(
                    id = "human-approval",
                    position = XYPosition(1650f, 215f),
                    data = N8nNodeData(
                        title = "Send for Human Approval",
                        subtitle = "sendAndWait: message",
                        kind = N8nNodeKind.ACTION_SQUARE,
                        iconType = "gmail"
                    ),
                    type = "n8n"
                ),
                // 4. [+] Add Next Step button
                Node(
                    id = "add-step",
                    position = XYPosition(2120f, 265f),
                    data = N8nNodeData(
                        title = "",
                        kind = N8nNodeKind.ADD_BUTTON,
                        iconType = "add"
                    ),
                    type = "n8n"
                ),
                // 5. Model (Sub-node circle, OpenAI)
                Node(
                    id = "sub-model",
                    position = XYPosition(50f, 850f),
                    data = N8nNodeData(
                        title = "Model",
                        kind = N8nNodeKind.SUB_NODE_CIRCLE,
                        topBadge = "Model",
                        iconType = "openai"
                    ),
                    type = "n8n"
                ),
                // 6. Availability (Sub-node circle, Google Calendar 31)
                Node(
                    id = "sub-calendar",
                    position = XYPosition(450f, 850f),
                    data = N8nNodeData(
                        title = "Availability",
                        subtitle = "availability: calendar",
                        kind = N8nNodeKind.SUB_NODE_CIRCLE,
                        iconType = "calendar"
                    ),
                    type = "n8n"
                ),
                // 7. Output (Sub-node circle, Code brackets)
                Node(
                    id = "sub-output",
                    position = XYPosition(850f, 850f),
                    data = N8nNodeData(
                        title = "Output",
                        kind = N8nNodeKind.SUB_NODE_CIRCLE,
                        topBadge = "Output Parser",
                        iconType = "code"
                    ),
                    type = "n8n"
                )
            )
        )
    }

    var n8nEdges by remember(isDarkTheme) {
        mutableStateOf(
            listOf(
                // Main Horizontal Pipeline (Solid connections with Arrow markers)
                Edge<EmptyEdgeData>(
                    id = "e-agent-generate",
                    source = "agent",
                    sourceHandle = "out",
                    target = "generate-msg",
                    targetHandle = "in",
                    type = EdgeType.BEZIER,
                    markerEnd = EdgeMarker.ArrowClosed,
                    style = EdgeStyle(
                        strokeColor = if (isDarkTheme) Color(0xFFD4D4D8) else Color(0xFF475569),
                        strokeWidth = 2.2f
                    )
                ),
                Edge<EmptyEdgeData>(
                    id = "e-generate-approval",
                    source = "generate-msg",
                    sourceHandle = "out",
                    target = "human-approval",
                    targetHandle = "in",
                    type = EdgeType.BEZIER,
                    markerEnd = EdgeMarker.ArrowClosed,
                    style = EdgeStyle(
                        strokeColor = if (isDarkTheme) Color(0xFFD4D4D8) else Color(0xFF475569),
                        strokeWidth = 2.2f
                    )
                ),
                Edge<EmptyEdgeData>(
                    id = "e-approval-add",
                    source = "human-approval",
                    sourceHandle = "out",
                    target = "add-step",
                    targetHandle = "in",
                    type = EdgeType.BEZIER,
                    markerEnd = EdgeMarker.ArrowClosed,
                    style = EdgeStyle(
                        strokeColor = if (isDarkTheme) Color(0xFFD4D4D8) else Color(0xFF475569),
                        strokeWidth = 2.2f
                    )
                ),
                // Sub-Nodes Connections (Dashed bezier curves from Agent bottom ports)
                Edge<EmptyEdgeData>(
                    id = "e-agent-model",
                    source = "agent",
                    sourceHandle = "agent-chat-model",
                    target = "sub-model",
                    targetHandle = "top",
                    type = EdgeType.BEZIER,
                    style = EdgeStyle(
                        strokeColor = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF64748B),
                        strokeWidth = 1.8f,
                        dashed = true
                    )
                ),
                Edge<EmptyEdgeData>(
                    id = "e-agent-calendar",
                    source = "agent",
                    sourceHandle = "agent-tool",
                    target = "sub-calendar",
                    targetHandle = "top",
                    type = EdgeType.BEZIER,
                    style = EdgeStyle(
                        strokeColor = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF64748B),
                        strokeWidth = 1.8f,
                        dashed = true
                    )
                ),
                Edge<EmptyEdgeData>(
                    id = "e-agent-output",
                    source = "agent",
                    sourceHandle = "agent-output-parser",
                    target = "sub-output",
                    targetHandle = "top",
                    type = EdgeType.BEZIER,
                    style = EdgeStyle(
                        strokeColor = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF64748B),
                        strokeWidth = 1.8f,
                        dashed = true
                    )
                )
            )
        )
    }

    var backgroundVariant by rememberSaveable { mutableStateOf(BackgroundVariant.DOTS) }
    val flowInstance = remember { KotlinFlowInstance() }

    // Re-frame view smoothly whenever switching between Standard and n8n views or compact mode
    LaunchedEffect(isN8nView, isCompactNodes) {
        delay(300)
        if (isN8nView) {
            flowInstance.fitView(n8nNodes)
        } else {
            flowInstance.fitView(standardNodes)
        }
    }

    val overlayContent: @Composable () -> Unit = {
        // Controls panel (+, -, 100%, fit, lock)
        Controls(position = PanelPosition.BOTTOM_LEFT)

        // MiniMap overview
        MiniMap(position = PanelPosition.BOTTOM_RIGHT)

        // Top Panel with n8n View switch, compact switch & variant toggle (shown when activated via top bar config button)
        if (isConfigPanelOpen) {
            Panel(position = PanelPosition.TOP_RIGHT) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF262626).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header with Title and Close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF1E88E5),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Flow Settings",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                )
                            }
                            IconButton(
                                onClick = onCloseConfigPanel,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close settings",
                                    tint = if (isDarkTheme) Color(0xFF90A4AE) else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0),
                            thickness = 0.5.dp
                        )

                        // Row 1: n8n Workflow Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(
                                            color = Color(0xFFFF5D44),
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "n8n Mode",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "n8n Mode",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = if (isN8nView) "Automation Nodes" else "Standard Flow",
                                        fontSize = 10.sp,
                                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Switch(
                                checked = isN8nView,
                                onCheckedChange = { checked ->
                                    isN8nView = checked
                                },
                                modifier = Modifier.scale(0.85f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFFF5D44),
                                    uncheckedTrackColor = if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0)
                                )
                            )
                        }

                        // Row 2: Compact Nodes Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(
                                            color = if (isDarkTheme) Color(0xFF3B82F6).copy(alpha = 0.25f) else Color(0xFFDBEAFE),
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewCompact,
                                        contentDescription = "Compact Nodes",
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Compact Nodes",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = if (isCompactNodes) "High-density view" else "Detailed view",
                                        fontSize = 10.sp,
                                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Switch(
                                checked = isCompactNodes,
                                onCheckedChange = { checked ->
                                    isCompactNodes = checked
                                },
                                modifier = Modifier.scale(0.85f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF3B82F6),
                                    uncheckedTrackColor = if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0)
                                )
                            )
                        }

                        // Divider
                        HorizontalDivider(
                            color = if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0),
                            thickness = 0.5.dp
                        )

                        // Canvas Background Variants
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BackgroundVariant.values().forEach { variant ->
                                val label = when (variant) {
                                    BackgroundVariant.DOTS -> "Dots"
                                    BackgroundVariant.LINES -> "Lines"
                                    BackgroundVariant.CROSS -> "Cross"
                                    BackgroundVariant.NONE -> "None"
                                }
                                FilterChip(
                                    selected = backgroundVariant == variant,
                                    onClick = { backgroundVariant = variant },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isN8nView) {
            KotlinFlow(
                nodes = n8nNodes,
                edges = n8nEdges,
                onNodesChange = { n8nNodes = applyNodeChanges(it, n8nNodes) },
                onEdgesChange = { n8nEdges = applyEdgeChanges(it, n8nEdges) },
                onConnect = { n8nEdges = addEdge(it, n8nEdges) },
                backgroundVariant = backgroundVariant,
                snapToGrid = false,
                fitView = true,
                colorMode = if (isDarkTheme) ColorMode.DARK else ColorMode.LIGHT,
                kotlinFlowInstance = flowInstance,
                overlay = overlayContent
            ) { node ->
                N8nNodeCard(node = node, isDarkTheme = isDarkTheme, isCompact = isCompactNodes)
            }
        } else {
            KotlinFlow(
                nodes = standardNodes,
                edges = standardEdges,
                onNodesChange = { standardNodes = applyNodeChanges(it, standardNodes) },
                onEdgesChange = { standardEdges = applyEdgeChanges(it, standardEdges) },
                onConnect = { standardEdges = addEdge(it, standardEdges) },
                backgroundVariant = backgroundVariant,
                snapToGrid = false,
                fitView = true,
                colorMode = if (isDarkTheme) ColorMode.DARK else ColorMode.LIGHT,
                kotlinFlowInstance = flowInstance,
                overlay = overlayContent
            ) { node ->
                CustomNodeCard(node = node, isDarkTheme = isDarkTheme, isCompact = isCompactNodes)
            }
        }
    }
}

@Composable
fun GmailIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(34.dp)) {
        val w = size.width
        val h = size.height
        val s = w * 0.16f

        // Blue left column
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(s / 2, h * 0.22f),
            end = Offset(s / 2, h - s / 2),
            strokeWidth = s,
            cap = StrokeCap.Round
        )
        // Green right column
        drawLine(
            color = Color(0xFF34A853),
            start = Offset(w - s / 2, h * 0.22f),
            end = Offset(w - s / 2, h - s / 2),
            strokeWidth = s,
            cap = StrokeCap.Round
        )
        // Red left diagonal & top corner
        val redPath = Path().apply {
            moveTo(s / 2, h * 0.22f)
            lineTo(w * 0.5f, h * 0.60f)
        }
        drawPath(
            path = redPath,
            color = Color(0xFFEA4335),
            style = Stroke(width = s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Yellow right diagonal
        val yellowPath = Path().apply {
            moveTo(w * 0.5f, h * 0.60f)
            lineTo(w - s / 2, h * 0.22f)
        }
        drawPath(
            path = yellowPath,
            color = Color(0xFFFBBC05),
            style = Stroke(width = s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun GoogleCalendarIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.5.dp, Color(0xFF4285F4), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .background(Color(0xFFEA4335))
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "31",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
fun OpenAIIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(30.dp)) {
        val r = size.minDimension / 2f
        val c = center
        for (i in 0 until 6) {
            val angle = (i * 60f) * (Math.PI / 180f).toFloat()
            val ox = c.x + (r * 0.42f) * kotlin.math.cos(angle)
            val oy = c.y + (r * 0.42f) * kotlin.math.sin(angle)
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = r * 0.40f,
                center = Offset(ox, oy),
                style = Stroke(width = 1.8.dp.toPx())
            )
        }
    }
}

@Composable
fun N8nNodeCard(
    node: Node<N8nNodeData>,
    isDarkTheme: Boolean = false,
    isCompact: Boolean = false
) {
    val data = node.data
    val cardBg = if (isDarkTheme) Color(0xFF26262B) else Color.White
    val cardBorder = if (isDarkTheme) Color(0xFF4B4B55) else Color(0xFFCBD5E1)
    val handleColor = if (isDarkTheme) Color(0xFFD4D4D8) else Color(0xFF475569)
    val titleColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF64748B)

    if (isCompact) {
        // High-density compact n8n pill node
        Box(
            modifier = Modifier.width(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .shadow(3.dp, RoundedCornerShape(8.dp))
                    .border(1.dp, cardBorder, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (data.iconType) {
                        "robot" -> Icon(Icons.Default.SmartToy, null, tint = titleColor, modifier = Modifier.size(16.dp))
                        "edit" -> Icon(Icons.Default.Edit, null, tint = Color(0xFF5E5CE6), modifier = Modifier.size(16.dp))
                        "gmail" -> GmailIcon(modifier = Modifier.size(16.dp))
                        "openai" -> Icon(Icons.Default.AutoAwesome, null, tint = titleColor, modifier = Modifier.size(16.dp))
                        "calendar" -> GoogleCalendarIcon(modifier = Modifier.size(16.dp))
                        "code" -> Text("</>", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = titleColor)
                        "add" -> Icon(Icons.Default.Add, null, tint = titleColor, modifier = Modifier.size(16.dp))
                        else -> Icon(Icons.Default.Bolt, null, tint = titleColor, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = data.title.replace("\n", " "),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Input handle if applicable
            if (data.kind != N8nNodeKind.AGENT && data.kind != N8nNodeKind.ADD_BUTTON) {
                Handle(
                    nodeId = node.id,
                    id = if (data.kind == N8nNodeKind.SUB_NODE_CIRCLE) "top" else "in",
                    type = HandleType.TARGET,
                    position = if (data.kind == N8nNodeKind.SUB_NODE_CIRCLE) Position.TOP else Position.LEFT,
                    color = handleColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-5).dp)
                )
            } else if (data.kind == N8nNodeKind.ADD_BUTTON) {
                Handle(
                    nodeId = node.id,
                    id = "in",
                    type = HandleType.TARGET,
                    position = Position.LEFT,
                    color = handleColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-5).dp)
                )
            }

            // Output handle
            if (data.kind != N8nNodeKind.ADD_BUTTON && data.kind != N8nNodeKind.SUB_NODE_CIRCLE) {
                Handle(
                    nodeId = node.id,
                    id = "out",
                    type = HandleType.SOURCE,
                    position = Position.RIGHT,
                    color = handleColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 5.dp)
                )
            }
        }
    } else {
        // Detailed authentic n8n replica view
        when (data.kind) {
            N8nNodeKind.AGENT -> {
                // Wide Agent card with 4 bottom diamond ports
                Box(
                    modifier = Modifier.width(280.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .border(1.dp, cardBorder, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = titleColor,
                                    modifier = Modifier.size(34.dp)
                                )
                                Column {
                                    Text(
                                        text = "Meeting\nAvailability Agent",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        color = titleColor
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Tools Agent",
                                        fontSize = 11.sp,
                                        color = subtitleColor
                                    )
                                }
                            }
                        }

                        // Bottom diamond ports bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-5.5).dp)
                                .padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // 1. Chat Model
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Handle(
                                    nodeId = node.id,
                                    id = "agent-chat-model",
                                    type = HandleType.SOURCE,
                                    position = Position.BOTTOM,
                                    shape = DiamondShape,
                                    color = handleColor,
                                    size = 11.dp,
                                    borderColor = cardBg,
                                    borderWidth = 1.5.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(if (isDarkTheme) Color(0xFF1E1E24) else Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Chat Model",
                                        fontSize = 8.sp,
                                        color = subtitleColor
                                    )
                                }
                            }

                            // 2. Memory
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Handle(
                                    nodeId = node.id,
                                    id = "agent-memory",
                                    type = HandleType.SOURCE,
                                    position = Position.BOTTOM,
                                    shape = DiamondShape,
                                    color = handleColor,
                                    size = 11.dp,
                                    borderColor = cardBg,
                                    borderWidth = 1.5.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(if (isDarkTheme) Color(0xFF1E1E24) else Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Memory",
                                        fontSize = 8.sp,
                                        color = subtitleColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(if (isDarkTheme) Color(0xFF26262B) else Color.White, RoundedCornerShape(3.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = titleColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }

                            // 3. Tool
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Handle(
                                    nodeId = node.id,
                                    id = "agent-tool",
                                    type = HandleType.SOURCE,
                                    position = Position.BOTTOM,
                                    shape = DiamondShape,
                                    color = handleColor,
                                    size = 11.dp,
                                    borderColor = cardBg,
                                    borderWidth = 1.5.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(if (isDarkTheme) Color(0xFF1E1E24) else Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Tool",
                                        fontSize = 8.sp,
                                        color = subtitleColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(if (isDarkTheme) Color(0xFF26262B) else Color.White, RoundedCornerShape(3.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = titleColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }

                            // 4. Output Parser
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Handle(
                                    nodeId = node.id,
                                    id = "agent-output-parser",
                                    type = HandleType.SOURCE,
                                    position = Position.BOTTOM,
                                    shape = DiamondShape,
                                    color = handleColor,
                                    size = 11.dp,
                                    borderColor = cardBg,
                                    borderWidth = 1.5.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(if (isDarkTheme) Color(0xFF1E1E24) else Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Output Parser",
                                        fontSize = 8.sp,
                                        color = subtitleColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(if (isDarkTheme) Color(0xFF26262B) else Color.White, RoundedCornerShape(3.dp))
                                        .border(0.5.dp, cardBorder, RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = titleColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Left Circle Handle
                    Handle(
                        nodeId = node.id,
                        id = "in",
                        type = HandleType.TARGET,
                        position = Position.LEFT,
                        color = handleColor,
                        size = 12.dp,
                        borderColor = cardBg,
                        borderWidth = 2.dp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-6).dp, y = 35.dp)
                    )

                    // Right Circle Handle
                    Handle(
                        nodeId = node.id,
                        id = "out",
                        type = HandleType.SOURCE,
                        position = Position.RIGHT,
                        color = handleColor,
                        size = 12.dp,
                        borderColor = cardBg,
                        borderWidth = 2.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = 35.dp)
                    )
                }
            }

            N8nNodeKind.ACTION_SQUARE -> {
                // Square 72x72dp card with icon inside and labels below
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(130.dp)
                ) {
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .border(1.dp, cardBorder, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (data.iconType == "edit") {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF5E5CE6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else if (data.iconType == "gmail") {
                                    GmailIcon(modifier = Modifier.size(34.dp))
                                }
                            }
                        }

                        // Left Circle Handle
                        Handle(
                            nodeId = node.id,
                            id = "in",
                            type = HandleType.TARGET,
                            position = Position.LEFT,
                            color = handleColor,
                            size = 12.dp,
                            borderColor = cardBg,
                            borderWidth = 2.dp,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = (-6).dp)
                        )

                        // Right Circle Handle
                        Handle(
                            nodeId = node.id,
                            id = "out",
                            type = HandleType.SOURCE,
                            position = Position.RIGHT,
                            color = handleColor,
                            size = 12.dp,
                            borderColor = cardBg,
                            borderWidth = 2.dp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = data.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = titleColor,
                        textAlign = TextAlign.Center
                    )
                    if (data.subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data.subtitle,
                            fontSize = 10.sp,
                            color = subtitleColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            N8nNodeKind.ADD_BUTTON -> {
                // Mini 34x34dp [+] button
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(3.dp, RoundedCornerShape(8.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = titleColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Left Circle Handle
                    Handle(
                        nodeId = node.id,
                        id = "in",
                        type = HandleType.TARGET,
                        position = Position.LEFT,
                        color = handleColor,
                        size = 10.dp,
                        borderColor = cardBg,
                        borderWidth = 2.dp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (-5).dp)
                    )
                }
            }

            N8nNodeKind.SUB_NODE_CIRCLE -> {
                // Circular node with top diamond port
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(110.dp)
                ) {
                    if (data.topBadge != null) {
                        Box(
                            modifier = Modifier
                                .background(if (isDarkTheme) Color(0xFF1E1E24) else Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = data.topBadge,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = subtitleColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Box(
                        modifier = Modifier.size(66.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(6.dp, CircleShape)
                                .border(1.5.dp, cardBorder, CircleShape),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                when (data.iconType) {
                                    "openai" -> OpenAIIcon()
                                    "calendar" -> GoogleCalendarIcon()
                                    "code" -> Text("</>", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = titleColor)
                                    else -> Icon(Icons.Default.AutoAwesome, null, tint = titleColor, modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        // Top Diamond Handle
                        Handle(
                            nodeId = node.id,
                            id = "top",
                            type = HandleType.TARGET,
                            position = Position.TOP,
                            shape = DiamondShape,
                            color = handleColor,
                            size = 11.dp,
                            borderColor = cardBg,
                            borderWidth = 1.5.dp,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-5.5).dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = data.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = titleColor,
                        textAlign = TextAlign.Center
                    )
                    if (data.subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data.subtitle,
                            fontSize = 10.sp,
                            color = subtitleColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomNodeCard(
    node: Node<SampleNodeData>,
    isDarkTheme: Boolean = false,
    isCompact: Boolean = false
) {
    val headerColor = when (node.type) {
        "input" -> Color(0xFF4CAF50)
        "process" -> Color(0xFF1E88E5)
        "filter" -> Color(0xFFFB8C00)
        "output" -> Color(0xFF8E24AA)
        else -> Color.Gray
    }
    val cardBg = if (isDarkTheme) Color(0xFF262626) else Color.White
    val cardBorder = if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0)

    if (isCompact) {
        Box(
            modifier = Modifier.width(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .shadow(3.dp, RoundedCornerShape(8.dp))
                    .border(1.dp, cardBorder, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .background(headerColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = node.data.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Target handle (in) on the left border, perfectly centered vertically
            if (node.type != "input") {
                Handle(
                    nodeId = node.id,
                    id = "in",
                    type = HandleType.TARGET,
                    position = Position.LEFT,
                    color = headerColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-5).dp)
                )
            }

            // Source handle (out) on the right border, perfectly centered vertically
            if (node.type != "output") {
                Handle(
                    nodeId = node.id,
                    id = "out",
                    type = HandleType.SOURCE,
                    position = Position.RIGHT,
                    color = headerColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 5.dp)
                )
            }
        }
    } else {
        // Detailed card
        Box(
            modifier = Modifier.width(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                )
            ) {
                Column {
                    // Header bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(headerColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = node.data.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = node.data.description,
                            fontSize = 11.sp,
                            color = if (isDarkTheme) Color(0xFFB0BEC5) else Color.DarkGray
                        )
                    }
                }
            }

            // Target handle (in) on the left border, perfectly centered vertically
            if (node.type != "input") {
                Handle(
                    nodeId = node.id,
                    id = "in",
                    type = HandleType.TARGET,
                    position = Position.LEFT,
                    color = headerColor,
                    size = 12.dp,
                    borderColor = cardBg,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-6).dp)
                )
            }

            // Source handle (out) on the right border, perfectly centered vertically
            if (node.type != "output") {
                Handle(
                    nodeId = node.id,
                    id = "out",
                    type = HandleType.SOURCE,
                    position = Position.RIGHT,
                    color = headerColor,
                    size = 12.dp,
                    borderColor = cardBg,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AutoLayoutDemo(isDarkTheme: Boolean = false) {
    var nodes by remember {
        mutableStateOf(
            listOf(
                Node(id = "root", position = XYPosition.Zero, data = "Root", type = "input"),
                Node(id = "child1", position = XYPosition.Zero, data = "Child 1", type = "process"),
                Node(id = "child2", position = XYPosition.Zero, data = "Child 2", type = "process"),
                Node(id = "leaf1", position = XYPosition.Zero, data = "Leaf 1", type = "output"),
                Node(id = "leaf2", position = XYPosition.Zero, data = "Leaf 2", type = "output"),
                Node(id = "leaf3", position = XYPosition.Zero, data = "Leaf 3", type = "output")
            )
        )
    }

    val edges = remember {
        listOf(
            Edge<EmptyEdgeData>(id = "e1", source = "root", target = "child1"),
            Edge<EmptyEdgeData>(id = "e2", source = "root", target = "child2"),
            Edge<EmptyEdgeData>(id = "e3", source = "child1", target = "leaf1"),
            Edge<EmptyEdgeData>(id = "e4", source = "child1", target = "leaf2"),
            Edge<EmptyEdgeData>(id = "e5", source = "child2", target = "leaf3")
        )
    }

    val instance = remember { KotlinFlowInstance() }

    LaunchedEffect(Unit) {
        val changes = computeAutoLayout(
            nodes,
            edges,
            LayoutAlgorithm.Tree(LayoutDirection.LEFT_TO_RIGHT, nodeSpacing = 140f, levelSpacing = 360f)
        )
        nodes = applyNodeChanges(changes, nodes)
        delay(300)
        instance.fitView(nodes)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                val changes = computeAutoLayout(
                    nodes,
                    edges,
                    LayoutAlgorithm.Tree(LayoutDirection.TOP_TO_BOTTOM, nodeSpacing = 160f, levelSpacing = 240f)
                )
                nodes = applyNodeChanges(changes, nodes)
                instance.fitView(nodes)
            }) {
                Text("Tree (Vertical)", fontSize = 12.sp)
            }
            Button(onClick = {
                val changes = computeAutoLayout(
                    nodes,
                    edges,
                    LayoutAlgorithm.Tree(LayoutDirection.LEFT_TO_RIGHT, nodeSpacing = 140f, levelSpacing = 360f)
                )
                nodes = applyNodeChanges(changes, nodes)
                instance.fitView(nodes)
            }) {
                Text("Tree (Horizontal)", fontSize = 12.sp)
            }
            Button(onClick = {
                val changes = computeAutoLayout(
                    nodes,
                    edges,
                    LayoutAlgorithm.Grid(columns = 3, nodeSpacing = 180f)
                )
                nodes = applyNodeChanges(changes, nodes)
                instance.fitView(nodes)
            }) {
                Text("Grid", fontSize = 12.sp)
            }
        }

        val overlayContent: @Composable () -> Unit = {
            Controls(position = PanelPosition.BOTTOM_LEFT)
            MiniMap(position = PanelPosition.BOTTOM_RIGHT)
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            KotlinFlow(
                nodes = nodes,
                edges = edges,
                onNodesChange = { nodes = applyNodeChanges(it, nodes) },
                backgroundVariant = BackgroundVariant.LINES,
                colorMode = if (isDarkTheme) ColorMode.DARK else ColorMode.LIGHT,
                kotlinFlowInstance = instance,
                overlay = overlayContent
            ) { node ->
                Box(contentAlignment = Alignment.Center) {
                    if (node.id != "root") {
                        Handle(
                            nodeId = node.id,
                            id = "in",
                            type = HandleType.TARGET,
                            position = Position.LEFT,
                            color = Color(0xFF00BCD4),
                            size = 10.dp,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = (-5).dp)
                        )
                    }
                    Card(
                        modifier = Modifier.width(110.dp).padding(horizontal = 6.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = node.data,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                            )
                        }
                    }
                    if (node.id != "leaf1" && node.id != "leaf2" && node.id != "leaf3") {
                        Handle(
                            nodeId = node.id,
                            id = "out",
                            type = HandleType.SOURCE,
                            position = Position.RIGHT,
                            color = Color(0xFF10B981),
                            size = 10.dp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SerializationDemo() {
    val sampleNodes = remember {
        listOf(
            Node(id = "1", position = XYPosition(100f, 100f), data = "Source Node"),
            Node(id = "2", position = XYPosition(350f, 200f), data = "Target Node")
        )
    }
    val sampleEdges = remember {
        listOf(Edge<EmptyEdgeData>(id = "e1-2", source = "1", target = "2", label = "HTTP Request"))
    }

    var jsonOutput by remember { mutableStateOf("") }
    var loadedInfo by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                jsonOutput = toJSONString(sampleNodes, sampleEdges)
            }) {
                Text("Export Graph to JSON")
            }
            Button(onClick = {
                if (jsonOutput.isNotEmpty()) {
                    val doc = fromJSONString<String, EmptyEdgeData>(jsonOutput)
                    loadedInfo = "Loaded ${doc.nodes.size} nodes and ${doc.edges.size} edges successfully!"
                }
            }) {
                Text("Import from JSON")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (loadedInfo.isNotEmpty()) {
            Text(loadedInfo, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Card(
            modifier = Modifier.fillMaxSize().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
        ) {
            Text(
                text = if (jsonOutput.isEmpty()) "// Click 'Export Graph to JSON' to see serialized graph" else jsonOutput,
                color = Color(0xFF80CBC4),
                modifier = Modifier.padding(12.dp),
                fontSize = 12.sp
            )
        }
    }
}
