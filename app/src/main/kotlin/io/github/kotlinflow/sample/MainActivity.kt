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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

data class SampleNodeData(
    val title: String,
    val description: String,
    val iconType: String = "process"
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
    var nodes by remember {
        mutableStateOf(
            listOf(
                Node(
                    id = "1",
                    position = XYPosition(50f, 100f),
                    data = SampleNodeData("Input Data", "Initial event stream", "input"),
                    type = "input"
                ),
                Node(
                    id = "2",
                    position = XYPosition(350f, 50f),
                    data = SampleNodeData("Data Processing", "Transforms & validates payload", "process"),
                    type = "process"
                ),
                Node(
                    id = "3",
                    position = XYPosition(350f, 220f),
                    data = SampleNodeData("Filter", "Discards invalid records", "filter"),
                    type = "filter"
                ),
                Node(
                    id = "4",
                    position = XYPosition(700f, 140f),
                    data = SampleNodeData("Output Target", "Sinks to storage or API", "output"),
                    type = "output"
                )
            )
        )
    }

    var edges by remember {
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

    var backgroundVariant by remember { mutableStateOf(BackgroundVariant.DOTS) }
    val flowInstance = remember { KotlinFlowInstance() }

    Box(modifier = Modifier.fillMaxSize()) {
        KotlinFlow(
            nodes = nodes,
            edges = edges,
            onNodesChange = { nodes = applyNodeChanges(it, nodes) },
            onEdgesChange = { edges = applyEdgeChanges(it, edges) },
            onConnect = { edges = addEdge(it, edges) },
            backgroundVariant = backgroundVariant,
            snapToGrid = false,
            colorMode = if (isDarkTheme) ColorMode.DARK else ColorMode.LIGHT,
            kotlinFlowInstance = flowInstance,
            overlay = {
                // Controls panel (+, -, 100%, fit, lock)
                Controls(position = PanelPosition.BOTTOM_LEFT)

                // MiniMap overview
                MiniMap(position = PanelPosition.BOTTOM_RIGHT)

                // Top Panel with variant toggle
                Panel(position = PanelPosition.TOP_RIGHT) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF262626).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = backgroundVariant == BackgroundVariant.DOTS,
                                onClick = { backgroundVariant = BackgroundVariant.DOTS },
                                label = { Text("Dots", fontSize = 12.sp) }
                            )
                            FilterChip(
                                selected = backgroundVariant == BackgroundVariant.LINES,
                                onClick = { backgroundVariant = BackgroundVariant.LINES },
                                label = { Text("Lines", fontSize = 12.sp) }
                            )
                            FilterChip(
                                selected = backgroundVariant == BackgroundVariant.CROSS,
                                onClick = { backgroundVariant = BackgroundVariant.CROSS },
                                label = { Text("Cross", fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        ) { node ->
            CustomNodeCard(node = node, isDarkTheme = isDarkTheme)
        }
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

    Card(
        modifier = Modifier
            .width(180.dp)
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
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = node.data.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Body
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Target handle on left
                if (node.type != "input") {
                    Handle(
                        nodeId = node.id,
                        id = "in",
                        type = HandleType.TARGET,
                        position = Position.LEFT,
                        color = headerColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = node.data.description,
                    fontSize = 11.sp,
                    color = if (isDarkTheme) Color(0xFFB0BEC5) else Color.DarkGray,
                    modifier = Modifier.weight(1f)
                )

                // Source handle on right
                if (node.type != "output") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Handle(
                        nodeId = node.id,
                        id = "out",
                        type = HandleType.SOURCE,
                        position = Position.RIGHT,
                        color = headerColor
                    )
                }
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
