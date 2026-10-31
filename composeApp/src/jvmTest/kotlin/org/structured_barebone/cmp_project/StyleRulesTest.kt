package org.structured_barebone.cmp_project

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Sizes, colors and type styles come from the design tokens of the `theme` module. Production sources of every
 * other module must not repeat them as raw numbers; there is no tolerance list, so any new violation fails.
 */
class StyleRulesTest {
    private val root = File("..").canonicalFile

    private val sources: List<File> = root.walkTopDown()
        .onEnter { it.name !in IGNORED_DIRECTORIES }
        .filter { it.isFile && it.extension == "kt" }
        .filter { Regex("""/src/[a-zA-Z]*Main/""").containsMatchIn(it.invariantSeparatorsPath) }
        .toList()

    private val outsideTheme = sources.filter { !it.invariantSeparatorsPath.contains("/theme/src/") }

    private fun File.code(): String = readText()
        .replace(Regex("""/\*.*?\*/""", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("""(?m)//.*$"""), "")

    private fun violations(pattern: Regex): List<String> = outsideTheme.flatMap { file ->
        pattern.findAll(file.code()).map { "${file.relativeTo(root).invariantSeparatorsPath}: ${it.value}" }.toList()
    }

    @Test
    fun the_scan_sees_the_theme_and_the_other_modules() {
        assertTrue(sources.any { it.invariantSeparatorsPath.contains("/theme/src/") }, "theme sources not found")
        assertTrue(outsideTheme.size > 5, "Too few sources outside the theme; the scan is broken")
    }

    @Test
    fun no_raw_dp_or_sp_numbers_outside_the_theme() {
        val found = violations(Regex("""\b\d+(?:\.\d+)?f?\.(?:dp|sp)\b"""))
        assertTrue(found.isEmpty(), "Use Spacing, Radius, IconSize, LineWidth, Elevation or AppLayout tokens:\n" + found.joinToString("\n"))
    }

    @Test
    fun no_raw_colors_or_type_styles_outside_the_theme() {
        val found = violations(Regex("""\bColor\(\s*0x|\bfontSize\s*=|\bFontWeight\.|\bRoundedCornerShape\("""))
        assertTrue(found.isEmpty(), "Use MaterialTheme colors, typography and shapes:\n" + found.joinToString("\n"))
    }

    private companion object {
        val IGNORED_DIRECTORIES = setOf("build", ".gradle", ".git", ".kotlin", "node_modules")
    }
}
