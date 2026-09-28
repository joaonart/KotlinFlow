package io.github.kotlinflow

import io.github.kotlinflow.models.KotlinFlowTheme
import org.junit.Assert.assertNotNull
import org.junit.Test

class ThemeTests {

    @Test
    fun defaultTheme() {
        val theme = KotlinFlowTheme.Default
        assertNotNull(theme.edgeColor)
        assertNotNull(theme.edgeSelectedColor)
        assertNotNull(theme.nodeBackgroundColor)
    }

    @Test
    fun darkTheme() {
        val theme = KotlinFlowTheme.Dark
        assertNotNull(theme.edgeColor)
        assertNotNull(theme.canvasBackgroundColor)
    }

    @Test
    fun lightTheme() {
        val theme = KotlinFlowTheme.Light
        assertNotNull(theme.edgeColor)
    }
}
