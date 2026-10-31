package org.structured_barebone.cmp_project

import org.structured_barebone.shared.model.AppLanguage
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * User-visible text lives in localized resources only. These tests scan the sources and resource files of
 * every module; see the localization section of `docs/architecture-decisions.md`.
 */
class LocalizationRulesTest {
    private val root = File("..").canonicalFile

    private val allFiles: List<File> = root.walkTopDown()
        .onEnter { it.name !in IGNORED_DIRECTORIES }
        .filter { it.isFile }
        .toList()

    private fun File.code(): String = readText()
        .replace(Regex("""/\*.*?\*/""", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("""(?m)//.*$"""), "")

    private fun relative(file: File) = file.relativeTo(root).invariantSeparatorsPath

    @Test
    fun ui_sources_have_no_hard_coded_user_visible_text() {
        val uiSources = allFiles
            .filter { it.extension == "kt" && Regex("""/src/[a-zA-Z]*Main/""").containsMatchIn(it.invariantSeparatorsPath) }
            .filter { it.readText().contains("@Composable") }
        assertTrue(uiSources.isNotEmpty(), "No composable sources found; the scan is broken")

        val violations = uiSources.flatMap { file ->
            val code = file.code()
            LITERAL_TEXT_PATTERNS.flatMap { pattern -> pattern.findAll(code).map { "${relative(file)}: ${it.value.trim()}" } }
        }
        assertTrue(
            violations.isEmpty(),
            "Hard-coded UI text found; move it to composeResources/values*/strings.xml and use stringResource:\n" +
                violations.joinToString("\n"),
        )
    }

    @Test
    fun every_language_has_a_resource_file_next_to_the_default_one() {
        val defaults = stringFiles().filter { it.parentFile.name == "values" }
        assertTrue(defaults.isNotEmpty(), "No string resources found; the scan is broken")

        val missing = defaults.flatMap { default ->
            AppLanguage.entries.mapNotNull { it.tag }.filter { it != DEFAULT_LANGUAGE }
                .filter { !File(default.parentFile.parentFile, "values-$it/strings.xml").isFile }
                .map { "${relative(default)} has no values-$it" }
        }
        assertTrue(missing.isEmpty(), "Missing translations:\n" + missing.joinToString("\n"))
    }

    @Test
    fun translations_have_the_same_keys_and_placeholders_as_the_default() {
        val defaults = stringFiles().filter { it.parentFile.name == "values" }
        val problems = defaults.flatMap { default ->
            val reference = parse(default)
            default.parentFile.parentFile.listFiles().orEmpty()
                .filter { it.isDirectory && it.name.startsWith("values-") }
                .map { File(it, "strings.xml") }
                .filter { it.isFile }
                .flatMap { translation ->
                    val entries = parse(translation)
                    val missing = reference.keys - entries.keys
                    val extra = entries.keys - reference.keys
                    val differing = reference.keys.intersect(entries.keys)
                        .filter { placeholders(reference.getValue(it)) != placeholders(entries.getValue(it)) }
                    buildList {
                        if (missing.isNotEmpty()) add("${relative(translation)} lacks keys: $missing")
                        if (extra.isNotEmpty()) add("${relative(translation)} has unknown keys: $extra")
                        if (differing.isNotEmpty()) add("${relative(translation)} changes placeholders of: $differing")
                    }
                }
        }
        assertTrue(problems.isEmpty(), problems.joinToString("\n"))
    }

    private fun stringFiles(): List<File> = allFiles.filter {
        it.name == "strings.xml" && it.invariantSeparatorsPath.contains("/composeResources/")
    }

    private fun parse(file: File): Map<String, String> =
        Regex("""<string name="([^"]+)">(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
            .findAll(file.readText()).associate { it.groupValues[1] to it.groupValues[2] }

    private fun placeholders(text: String): List<String> =
        Regex("""%\d+\$[sd]""").findAll(text).map { it.value }.sorted().toList()

    private companion object {
        const val DEFAULT_LANGUAGE = "en"
        val IGNORED_DIRECTORIES = setOf("build", ".gradle", ".git", ".kotlin", "node_modules")

        /** Text passed to composables or accessibility properties as a string literal. */
        val LITERAL_TEXT_PATTERNS = listOf(
            Regex("""\bText\(\s*"[^"]*""""),
            Regex("""\b(?:text|label|title|placeholder|contentDescription|supportingText)\s*=\s*"[^"]*""""),
        )
    }
}
