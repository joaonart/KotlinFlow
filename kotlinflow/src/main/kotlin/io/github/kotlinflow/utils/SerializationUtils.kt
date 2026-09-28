package io.github.kotlinflow.utils

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

typealias SwiftFlowDocument<NodeData, EdgeData> = KotlinFlowDocument<NodeData, EdgeData>

/**
 * A complete graph state for serialization.
 * Contains all nodes, edges, and an optional viewport snapshot.
 */
@Serializable
data class KotlinFlowDocument<NodeData, EdgeData>(
    val nodes: List<Node<NodeData>>,
    val edges: List<Edge<EdgeData>>,
    val viewport: Viewport? = null
)

val KotlinFlowJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

/**
 * Encodes nodes, edges, and viewport to a JSON string.
 */
inline fun <reified NodeData, reified EdgeData> toJSONString(
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>,
    viewport: Viewport? = null,
    json: Json = KotlinFlowJson
): String {
    val serializer = serializer<KotlinFlowDocument<NodeData, EdgeData>>()
    val doc = KotlinFlowDocument(nodes, edges, viewport)
    return json.encodeToString(serializer, doc)
}

/**
 * Encodes nodes, edges, and viewport to JSON ByteArray.
 */
inline fun <reified NodeData, reified EdgeData> toJSON(
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>,
    viewport: Viewport? = null,
    json: Json = KotlinFlowJson
): ByteArray {
    return toJSONString(nodes, edges, viewport, json).encodeToByteArray()
}

/**
 * Decodes a [KotlinFlowDocument] from a JSON string.
 */
inline fun <reified NodeData, reified EdgeData> fromJSONString(
    string: String,
    json: Json = KotlinFlowJson
): KotlinFlowDocument<NodeData, EdgeData> {
    val serializer = serializer<KotlinFlowDocument<NodeData, EdgeData>>()
    return json.decodeFromString(serializer, string)
}

/**
 * Decodes a [KotlinFlowDocument] from a JSON ByteArray.
 */
inline fun <reified NodeData, reified EdgeData> fromJSON(
    data: ByteArray,
    json: Json = KotlinFlowJson
): KotlinFlowDocument<NodeData, EdgeData> {
    return fromJSONString(data.decodeToString(), json)
}
