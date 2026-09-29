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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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

enum class N8nNodeType(
    val badgeLabel: String,
    val accentColor: Color,
    val icon: ImageVector
) {
    TRIGGER("TRIGGER", Color(0xFFFF5D44), Icons.Default.Bolt),
    AI("AI AGENT", Color(0xFF8B5CF6), Icons.Default.AutoAwesome),
    CONDITION("ROUTER", Color(0xFFF59E0B), Icons.AutoMirrored.Filled.CallSplit),
    DATABASE("DATABASE", Color(0xFF0284C7), Icons.Default.Storage),
    ACTION("ACTION", Color(0xFF10B981), Icons.AutoMirrored.Filled.Send)
}

data class N8nNodeData(
    val title: String,
    val subtitle: String,
    val nodeType: N8nNodeType,
    val status: String = "Success",
    val itemsCount: Int = 1,
    val executionTime: String? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

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
    var selectedTab by remember { mutableStateOf(0) }
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
                0 -> InteractiveCanvasDemo(isDarkTheme = isDarkTheme)
                1 -> AutoLayoutDemo(isDarkTheme = isDarkTheme)
                2 -> SerializationDemo()
            }
        }
    }
}

@Composable
fun InteractiveCanvasDemo(isDarkTheme: Boolean) {
    var isN8nView by remember { mutableStateOf(false) }

    // Standard DAG pipeline nodes
    var standardNodes by remember {
        mutableStateOf(
            listOf(
                Node(
                    id = "1",
                    position = XYPosition(60f, 400f),
                    data = SampleNodeData("Input Data", "Initial event stream", "input"),
                    type = "input"
                ),
                Node(
                    id = "2",
                    position = XYPosition(620f, 180f),
                    data = SampleNodeData("Data Processing", "Transforms & validates payload", "process"),
                    type = "process"
                ),
                Node(
                    id = "3",
                    position = XYPosition(620f, 620f),
                    data = SampleNodeData("Filter", "Discards invalid records", "filter"),
                    type = "filter"
                ),
                Node(
                    id = "4",
                    position = XYPosition(1180f, 400f),
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
                    type = EdgeType.SMOOTHSTEP,
                    label = "raw stream",
                    markerEnd = EdgeMarker.Arrow
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
                    type = EdgeType.SMOOTHSTEP,
                    markerEnd = EdgeMarker.ArrowClosed
                )
            )
        )
    }

    // n8n workflow nodes and edges
    var n8nNodes by remember {
        mutableStateOf(
            listOf(
                Node(
                    id = "n8n-1",
                    position = XYPosition(60f, 440f),
                    data = N8nNodeData(
                        title = "Webhook Trigger",
                        subtitle = "POST /v1/incoming-lead",
                        nodeType = N8nNodeType.TRIGGER,
                        status = "Active",
                        itemsCount = 1
                    ),
                    type = "n8n"
                ),
                Node(
                    id = "n8n-2",
                    position = XYPosition(850f, 200f),
                    data = N8nNodeData(
                        title = "Gemini AI Agent",
                        subtitle = "Extract & score intent",
                        nodeType = N8nNodeType.AI,
                        status = "Success",
                        itemsCount = 1,
                        executionTime = "180ms"
                    ),
                    type = "n8n"
                ),
                Node(
                    id = "n8n-3",
                    position = XYPosition(850f, 680f),
                    data = N8nNodeData(
                        title = "Router (IF)",
                        subtitle = "lead_score >= 80",
                        nodeType = N8nNodeType.CONDITION,
                        status = "Evaluated",
                        itemsCount = 1
                    ),
                    type = "n8n"
                ),
                Node(
                    id = "n8n-4",
                    position = XYPosition(1640f, 200f),
                    data = N8nNodeData(
                        title = "PostgreSQL DB",
                        subtitle = "Insert into crm_leads",
                        nodeType = N8nNodeType.DATABASE,
                        status = "Success",
                        itemsCount = 1,
                        executionTime = "34ms"
                    ),
                    type = "n8n"
                ),
                Node(
                    id = "n8n-5",
                    position = XYPosition(1640f, 680f),
                    data = N8nNodeData(
                        title = "Slack Alert",
                        subtitle = "Notify #vip-deals",
                        nodeType = N8nNodeType.ACTION,
                        status = "Sent",
                        itemsCount = 1,
                        executionTime = "92ms"
                    ),
                    type = "n8n"
                )
            )
        )
    }

    var n8nEdges by remember {
        mutableStateOf(
            listOf(
                Edge<EmptyEdgeData>(
                    id = "n-e1-2",
                    source = "n8n-1",
                    target = "n8n-2",
                    animated = true,
                    label = "1 item",
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "n-e1-3",
                    source = "n8n-1",
                    target = "n8n-3",
                    type = EdgeType.SMOOTHSTEP,
                    animated = true,
                    label = "1 item",
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "n-e2-4",
                    source = "n8n-2",
                    target = "n8n-4",
                    animated = true,
                    label = "score: 95",
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "n-e3-5",
                    source = "n8n-3",
                    target = "n8n-5",
                    type = EdgeType.SMOOTHSTEP,
                    animated = true,
                    label = "true (qualified)",
                    markerEnd = EdgeMarker.ArrowClosed
                )
            )
        )
    }

    var backgroundVariant by remember { mutableStateOf(BackgroundVariant.DOTS) }
    val flowInstance = remember { KotlinFlowInstance() }

    // Re-frame view smoothly whenever switching between Standard and n8n views
    LaunchedEffect(isN8nView) {
        delay(120)
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

        // Top Panel with n8n View switch & variant toggle
        Panel(position = PanelPosition.TOP_RIGHT) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF262626).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // n8n Workflow Switch Row
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

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0))
                    )

                    // Canvas Background Variants
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = backgroundVariant == BackgroundVariant.DOTS,
                            onClick = { backgroundVariant = BackgroundVariant.DOTS },
                            label = { Text("Dots", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = backgroundVariant == BackgroundVariant.LINES,
                            onClick = { backgroundVariant = BackgroundVariant.LINES },
                            label = { Text("Lines", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = backgroundVariant == BackgroundVariant.CROSS,
                            onClick = { backgroundVariant = BackgroundVariant.CROSS },
                            label = { Text("Cross", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = backgroundVariant == BackgroundVariant.NONE,
                            onClick = { backgroundVariant = BackgroundVariant.NONE },
                            label = { Text("None", fontSize = 11.sp) }
                        )
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
                N8nNodeCard(node = node, isDarkTheme = isDarkTheme)
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
                CustomNodeCard(node = node, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@Composable
fun N8nNodeCard(node: Node<N8nNodeData>, isDarkTheme: Boolean = false) {
    val data = node.data
    val cardBg = if (isDarkTheme) Color(0xFF1E222D) else Color.White
    val cardBorder = if (isDarkTheme) Color(0xFF333B4F) else Color(0xFFE2E8F0)
    val titleColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(
        modifier = Modifier.width(230.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(12.dp))
                .border(1.dp, cardBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Top section: Icon + Labels
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Node Type Icon Box (n8n signature colored square with rounded corners)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(data.nodeType.accentColor.copy(alpha = if (isDarkTheme) 0.25f else 0.15f))
                            .border(1.dp, data.nodeType.accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = data.nodeType.icon,
                            contentDescription = null,
                            tint = data.nodeType.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        // Category Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    data.nodeType.accentColor.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = data.nodeType.badgeLabel,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = data.nodeType.accentColor
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = titleColor,
                            maxLines = 1
                        )
                        Text(
                            text = data.subtitle,
                            fontSize = 11.sp,
                            color = subtitleColor,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(if (isDarkTheme) Color(0xFF2A2E3D) else Color(0xFFF1F5F9))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Status Bar: Success checkmark + Items pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (data.executionTime != null) "${data.status} • ${data.executionTime}" else data.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF10B981)
                        )
                    }

                    // Output items count badge (e.g., "1 item", "5 items")
                    Box(
                        modifier = Modifier
                            .background(
                                if (isDarkTheme) Color(0xFF064E3B).copy(alpha = 0.6f) else Color(0xFFD1FAE5),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${data.itemsCount} item",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) Color(0xFF6EE7B7) else Color(0xFF047857)
                        )
                    }
                }
            }
        }

        // Target handle (in) on the left border - only for non-triggers
        if (data.nodeType != N8nNodeType.TRIGGER) {
            Handle(
                nodeId = node.id,
                id = "in",
                type = HandleType.TARGET,
                position = Position.LEFT,
                color = data.nodeType.accentColor,
                size = 12.dp,
                borderColor = cardBg,
                borderWidth = 2.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-6).dp)
            )
        }

        // Source handle (out) on the right border
        Handle(
            nodeId = node.id,
            id = "out",
            type = HandleType.SOURCE,
            position = Position.RIGHT,
            color = data.nodeType.accentColor,
            size = 12.dp,
            borderColor = cardBg,
            borderWidth = 2.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 6.dp)
        )
    }
}

@Composable
fun CustomNodeCard(node: Node<SampleNodeData>, isDarkTheme: Boolean = false) {
    val headerColor = when (node.type) {
        "input" -> Color(0xFF4CAF50)
        "process" -> Color(0xFF1E88E5)
        "filter" -> Color(0xFFFB8C00)
        "output" -> Color(0xFF8E24AA)
        else -> Color.Gray
    }

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
                containerColor = if (isDarkTheme) Color(0xFF262626) else Color.White
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
                borderColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
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
                borderColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 6.dp)
            )
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                val changes = computeAutoLayout(nodes, edges, LayoutAlgorithm.Tree(LayoutDirection.TOP_TO_BOTTOM))
                nodes = applyNodeChanges(changes, nodes)
                instance.fitView(nodes)
            }) {
                Text("Tree (Vertical)", fontSize = 12.sp)
            }
            Button(onClick = {
                val changes = computeAutoLayout(nodes, edges, LayoutAlgorithm.Tree(LayoutDirection.LEFT_TO_RIGHT))
                nodes = applyNodeChanges(changes, nodes)
                instance.fitView(nodes)
            }) {
                Text("Tree (Horizontal)", fontSize = 12.sp)
            }
            Button(onClick = {
                val changes = computeAutoLayout(nodes, edges, LayoutAlgorithm.Grid(columns = 3))
                nodes = applyNodeChanges(changes, nodes)
                instance.fitView(nodes)
            }) {
                Text("Grid", fontSize = 12.sp)
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            KotlinFlow(
                nodes = nodes,
                edges = edges,
                onNodesChange = { nodes = applyNodeChanges(it, nodes) },
                backgroundVariant = BackgroundVariant.LINES,
                colorMode = if (isDarkTheme) ColorMode.DARK else ColorMode.LIGHT,
                kotlinFlowInstance = instance
            ) { node ->
                Card(
                    modifier = Modifier.width(100.dp).padding(4.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF262626) else Color(0xFFF0F4F8)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = node.data,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color.White else Color.Black
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
