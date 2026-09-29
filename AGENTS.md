# KotlinFlow — AI Agent & Developer Implementation Guide (`AGENTS.md`)

This guide is designed for AI coding agents (**Claude Code**, **Codex**, **Cursor**, **Gemini**, **Copilot**, etc.) and software engineers implementing or extending **KotlinFlow** in Kotlin / Android / Jetpack Compose projects.

KotlinFlow is a declarative, high-performance node-based graph and workflow UI library for Jetpack Compose, designed with 1:1 API parity to **SwiftFlow** and inspired by **React Flow**.

---

## 1. Quick Start & Dependencies

### Gradle Setup (`build.gradle.kts`)

```kotlin
dependencies {
    // KotlinFlow Core Library
    implementation("io.github.kotlinflow:kotlinflow:1.0.0")

    // Required Jetpack Compose & Foundation
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Required for JSON persistence & graph serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
```

Ensure the Compose compiler plugin is applied:
```kotlin
plugins {
    alias(libs.plugins.kotlin.compose) // or id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.kotlin.serialization)
}
```

---

## 2. Mental Model & Core Concepts

KotlinFlow operates as an **unidirectional data flow** canvas:

```
State (nodes, edges) ──> KotlinFlow Canvas ──> User Interaction (Drag / Connect / Pan)
         ▲                                                │
         └──────── applyNodeChanges / applyEdgeChanges ◄──┘
```

### Core Data Models

| Class | Purpose | Key Properties |
| :--- | :--- | :--- |
| `Node<T>` | Represents a canvas node | `id`, `position: XYPosition`, `data: T`, `type: String?`, `selected: Boolean`, `dragging: Boolean`, `hidden: Boolean` |
| `Edge<E>` | Represents a connection | `id`, `source: String`, `target: String`, `sourceHandle: String?`, `targetHandle: String?`, `type: EdgeType`, `animated: Boolean`, `label: String?`, `markerEnd: EdgeMarker?` |
| `XYPosition` | 2D coordinate on canvas | `x: Float`, `y: Float` (in canvas coordinate space) |
| `Handle` | Anchor point for edges | `nodeId`, `id`, `type: HandleType`, `position: Position`, `size: Dp`, `color: Color` |
| `KotlinFlowInstance` | Viewport controller | `fitView()`, `zoomIn()`, `zoomOut()`, `zoomTo(zoom)`, `setViewport(vp)` |

---

## 3. Standard Implementation Template

When an AI agent is asked to create a diagram screen, use this canonical pattern:

```kotlin
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.kotlinflow.components.*
import io.github.kotlinflow.models.*
import io.github.kotlinflow.types.*
import io.github.kotlinflow.utils.*

@Composable
fun FlowPipelineScreen() {
    // 1. Maintain observable state for nodes and edges
    var nodes by remember {
        mutableStateOf(
            listOf(
                Node(id = "1", position = XYPosition(60f, 360f), data = "Source Data", type = "input"),
                Node(id = "2", position = XYPosition(700f, 140f), data = "Processor A", type = "process"),
                Node(id = "3", position = XYPosition(700f, 580f), data = "Processor B", type = "process"),
                Node(id = "4", position = XYPosition(1340f, 360f), data = "Final Sink", type = "output")
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
                    label = "events",
                    markerEnd = EdgeMarker.ArrowClosed
                ),
                Edge<EmptyEdgeData>(
                    id = "e1-3",
                    source = "1",
                    target = "3",
                    animated = true,
                    label = "metrics",
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

    val flowInstance = remember { KotlinFlowInstance() }

    // 2. Smoothly fit view on initial mount
    LaunchedEffect(Unit) {
        flowInstance.fitView(nodes)
    }

    // 3. Render Canvas with Overlays
    Box(modifier = Modifier.fillMaxSize()) {
        KotlinFlow(
            nodes = nodes,
            edges = edges,
            onNodesChange = { changes -> nodes = applyNodeChanges(changes, nodes) },
            onEdgesChange = { changes -> edges = applyEdgeChanges(changes, edges) },
            onConnect = { connection -> edges = addEdge(connection, edges) },
            backgroundVariant = BackgroundVariant.DOTS,
            colorMode = ColorMode.SYSTEM,
            kotlinFlowInstance = flowInstance,
            overlay = {
                Controls(position = PanelPosition.BOTTOM_LEFT)
                MiniMap(position = PanelPosition.BOTTOM_RIGHT)
            }
        ) { node ->
            // Custom node renderer (see Section 4)
            PipelineNodeCard(node = node)
        }
    }
}
```

---

## 4. Custom Node & Handle Guidelines

### Golden Rules for Handles
1. **Never omit `Handle`**: Without handles, edge paths cannot calculate anchor offsets and will fall back to node center points.
2. **Handle Geometry & Pixel-Perfect Alignment**:
   - Set `size` explicitly (e.g., `12.dp` or `10.dp`).
   - Offset the handle outward by half its width: `offset(x = (-6).dp)` for `Position.LEFT` and `offset(x = 6.dp)` for `Position.RIGHT`. This ensures the center of the handle is situated exactly on `x = 0` and `x = width`.
   - Matching handle `borderColor` to the card background provides a professional cutout aesthetic.
3. **Handle Types**:
   - `HandleType.TARGET`: Incoming connections (placed on `Position.LEFT` or `Position.TOP`).
   - `HandleType.SOURCE`: Outgoing connections (placed on `Position.RIGHT` or `Position.BOTTOM`).

### Example: High-Quality Custom Node Card with Detailed vs Compact Switch

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kotlinflow.components.Handle
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.Position

@Composable
fun <T> CustomNodeCard(
    node: Node<T>,
    isCompact: Boolean = false,
    accentColor: Color = Color(0xFF1E88E5),
    isDarkTheme: Boolean = false
) {
    val cardBg = if (isDarkTheme) Color(0xFF262626) else Color.White
    val cardBorder = if (isDarkTheme) Color(0xFF3E3E3E) else Color(0xFFE2E8F0)

    if (isCompact) {
        // High-Density (Compact) View: 140dp x 36dp
        Box(
            modifier = Modifier.width(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
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
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = node.data.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Handles with 10dp size and 5dp half-offset
            if (node.type != "input") {
                Handle(
                    nodeId = node.id,
                    id = "in",
                    type = HandleType.TARGET,
                    position = Position.LEFT,
                    color = accentColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-5).dp)
                )
            }
            if (node.type != "output") {
                Handle(
                    nodeId = node.id,
                    id = "out",
                    type = HandleType.SOURCE,
                    position = Position.RIGHT,
                    color = accentColor,
                    size = 10.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 5.dp)
                )
            }
        }
    } else {
        // Detailed View: 180dp card with header & body
        Box(
            modifier = Modifier.width(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorder, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(accentColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = node.data.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Type: ${node.type ?: "standard"}",
                            fontSize = 11.sp,
                            color = if (isDarkTheme) Color(0xFFB0BEC5) else Color.DarkGray
                        )
                    }
                }
            }

            // Handles with 12dp size and 6dp half-offset
            if (node.type != "input") {
                Handle(
                    nodeId = node.id,
                    id = "in",
                    type = HandleType.TARGET,
                    position = Position.LEFT,
                    color = accentColor,
                    size = 12.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier.align(Alignment.CenterStart).offset(x = (-6).dp)
                )
            }
            if (node.type != "output") {
                Handle(
                    nodeId = node.id,
                    id = "out",
                    type = HandleType.SOURCE,
                    position = Position.RIGHT,
                    color = accentColor,
                    size = 12.dp,
                    borderColor = cardBg,
                    borderWidth = 2.dp,
                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 6.dp)
                )
            }
        }
    }
}
```

---

## 5. Mathematical Node Alignment & Coordinate Systems

Canvas coordinates `XYPosition(x, y)` represent canvas units, while node card widths are defined in Compose `dp`.
On standard mobile displays (density `~2.75`), `180.dp ≈ 495px`.

### Alignment Best Practices
- **Column Pitch**: When placing nodes horizontally into columns, leave at least `140px-200px` clearance between columns for animated edge curves and text labels.
  - Column 1: `x = 60f`
  - Column 2: `x = 700f` (`dx = 640f`)
  - Column 3: `x = 1340f` (`dx = 640f`)
- **Row Centering (Diamond Patterns)**:
  - Midline: `y = 360f` (Nodes 1 and 4 on the midline)
  - Top Branch: `y = 140f` (`360 - 220 = 140f`)
  - Bottom Branch: `y = 580f` (`360 + 220 = 580f`)
  - Equal vertical delta guarantees mirror-smooth Bezier curve symmetry.

---

## 6. Built-In Layout Algorithms

KotlinFlow includes automated graph layout algorithms:

```kotlin
import io.github.kotlinflow.utils.computeAutoLayout
import io.github.kotlinflow.utils.LayoutAlgorithm
import io.github.kotlinflow.utils.LayoutDirection

// 1. Hierarchical Tree Layout (Vertical)
val treeChanges = computeAutoLayout(
    nodes = nodes,
    edges = edges,
    algorithm = LayoutAlgorithm.Tree(LayoutDirection.TOP_TO_BOTTOM)
)
nodes = applyNodeChanges(treeChanges, nodes)
flowInstance.fitView(nodes)

// 2. Hierarchical Tree Layout (Horizontal)
val hTreeChanges = computeAutoLayout(
    nodes = nodes,
    edges = edges,
    algorithm = LayoutAlgorithm.Tree(LayoutDirection.LEFT_TO_RIGHT)
)
nodes = applyNodeChanges(hTreeChanges, nodes)
flowInstance.fitView(nodes)

// 3. Grid Layout
val gridChanges = computeAutoLayout(
    nodes = nodes,
    edges = edges,
    algorithm = LayoutAlgorithm.Grid(columns = 3)
)
nodes = applyNodeChanges(gridChanges, nodes)
flowInstance.fitView(nodes)

// 4. Force-Directed Layout
val forceChanges = computeAutoLayout(
    nodes = nodes,
    edges = edges,
    algorithm = LayoutAlgorithm.ForceDirected(iterations = 100)
)
nodes = applyNodeChanges(forceChanges, nodes)
flowInstance.fitView(nodes)
```

---

## 7. JSON Persistence & Serialization

Nodes and edges support full serialization with `kotlinx.serialization`:

```kotlin
import io.github.kotlinflow.utils.toJSONString
import io.github.kotlinflow.utils.fromJSONString

// Export workflow to JSON
val jsonString: String = toJSONString(nodes, edges)

// Import workflow from JSON
val (importedNodes, importedEdges) = fromJSONString<String, EmptyEdgeData>(jsonString)
nodes = importedNodes
edges = importedEdges
flowInstance.fitView(nodes)
```

---

## 8. Common Traps & Anti-Patterns to Avoid

| Anti-Pattern | Correct Pattern | Why |
| :--- | :--- | :--- |
| Mutating list in place (`nodes.add(...)`) | `nodes = applyNodeChanges(...)` or `nodes + newNode` | Compose requires new list references to trigger smart recomposition. |
| Duplicating node or edge `id`s | Generate unique IDs (`UUID.randomUUID()` or sequential) | Identifiers serve as Compose stable keys and internal handle lookup indices. |
| Placing `Handle` inside gesture detectors | Position handles directly inside the root card `Box` | Click/drag modifiers can intercept handle touch events and break connection gestures. |
| Hardcoding non-matching handle centers | Always offset by `-size/2` on left, `+size/2` on right | Avoids visible gaps between the edge line terminus and handle dot. |
| Overcrowding overlay panels | Use a TopAppBar settings toggle button (`Icons.Default.Tune`) to show/hide config panels | Keeps canvas unobstructed and viewports clean for diagram inspection. |

---

## 9. Architectural Parity Matrix (SwiftFlow vs KotlinFlow)

| SwiftFlow (SwiftUI / iOS) | KotlinFlow (Compose / Android) |
| :--- | :--- |
| `SwiftFlow(nodes:edges:...)` | `KotlinFlow(nodes:edges:...)` |
| `SwiftFlowState` | `KotlinFlowState` / `SwiftFlowState` |
| `SwiftFlowInstance` | `KotlinFlowInstance` / `SwiftFlowInstance` |
| `Handle(id:type:position:)` | `Handle(id:type:position:)` |
| `EdgeMarker.arrowClosed` | `EdgeMarker.ArrowClosed` |
| `LayoutAlgorithm.tree` | `LayoutAlgorithm.Tree` |
| `BackgroundVariant.dots` | `BackgroundVariant.DOTS` |

---

*Authored for AI Agents and Maintainers of KotlinFlow.*
