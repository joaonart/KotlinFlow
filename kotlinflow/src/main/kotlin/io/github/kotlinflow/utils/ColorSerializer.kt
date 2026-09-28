package io.github.kotlinflow.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for Compose [Color] to/from hex string format (e.g. "#RRGGBBAA" or "#RRGGBB").
 */
object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        val argb = value.toArgb()
        val hex = String.format("#%08X", argb)
        encoder.encodeString(hex)
    }

    override fun deserialize(decoder: Decoder): Color {
        val hex = decoder.decodeString()
        return parseHexColor(hex)
    }

    fun parseHexColor(hex: String): Color {
        val cleanHex = hex.removePrefix("#")
        return when (cleanHex.length) {
            6 -> {
                val rgb = cleanHex.toLong(16)
                Color(0xFF000000 or rgb)
            }
            8 -> {
                val argb = cleanHex.toLong(16)
                Color(argb.toInt())
            }
            else -> Color.Black
        }
    }
}
