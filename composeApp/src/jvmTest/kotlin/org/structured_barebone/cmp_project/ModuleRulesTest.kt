package org.structured_barebone.cmp_project

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Mechanical structure rules that the compiler cannot enforce: a missing registration only fails at run time.
 * The tests scan production sources (the `src/<target>Main` folders) of every module in the repository.
 */
class ModuleRulesTest {
    private val root = File("..").canonicalFile

    private val productionSources: List<File> = root.walkTopDown()
        .onEnter { it.name !in IGNORED_DIRECTORIES }
        .filter { it.isFile && it.extension == "kt" }
        .filter { Regex("""/src/[a-zA-Z]*Main/""").containsMatchIn(it.invariantSeparatorsPath) }
        .toList()

    private fun relative(file: File) = file.relativeTo(root).invariantSeparatorsPath

    private fun sourcesWhere(predicate: (String) -> Boolean): List<File> =
        productionSources.filter { predicate(it.invariantSeparatorsPath) }

    /** Source text without comments, so commented-out examples are not mistaken for declarations. */
    private fun File.code(): String = readText()
        .replace(Regex("""/\*.*?\*/""", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("""(?m)//.*$"""), "")

    @Test
    fun every_screen_route_is_registered_in_the_nav_graph() {
        val screens = sourcesWhere { it.endsWith("/route/Screen.kt") }.joinToString("\n") { it.code() }
        val graph = sourcesWhere { it.contains("/navigation/") }.joinToString("\n") { it.code() }
        val routes = Regex("""(?:data object|data class)\s+(\w+)""").findAll(screens).map { it.groupValues[1] }.toList()

        assertTrue(routes.isNotEmpty(), "No Screen routes found; the scan is broken")
        val unregistered = routes.filter { !graph.contains("composable<Screen.$it>") }
        assertTrue(unregistered.isEmpty(), "Routes without a composable<> registration in the nav graph: $unregistered")
    }

    @Test
    fun every_screen_route_is_serializable() {
        val screens = sourcesWhere { it.endsWith("/route/Screen.kt") }.joinToString("\n") { it.code() }
        val declarations = Regex("""(@Serializable\s+)?(?:data object|data class)\s+(\w+)""").findAll(screens).toList()

        assertTrue(declarations.isNotEmpty(), "No Screen routes found; the scan is broken")
        val missing = declarations.filter { it.groupValues[1].isEmpty() }.map { it.groupValues[2] }
        assertTrue(missing.isEmpty(), "Routes without @Serializable: $missing")
    }

    @Test
    fun every_view_model_extends_view_model_and_is_registered() {
        val definitions = productionSources.flatMap { file ->
            Regex("""(?m)^(?:internal\s+)?class\s+(\w+ViewModel)\b([^{]*)""").findAll(file.code())
                .map { Triple(file, it.groupValues[1], it.groupValues[2]) }
        }
        assertTrue(definitions.isNotEmpty(), "No ViewModel classes found; the scan is broken")

        val notViewModels = definitions.filter { (_, _, header) -> !header.substringAfter(":", "").contains("ViewModel()") }
            .map { "${relative(it.first)}: ${it.second}" }
        assertTrue(
            notViewModels.isEmpty(),
            "A class named *ViewModel must extend androidx ViewModel (rename plain state holders):\n" + notViewModels.joinToString("\n"),
        )

        val registrations = diSources()
        val unregistered = definitions.map { it.second }.distinct().filter { !Regex("""\b$it\(""").containsMatchIn(registrations) }
        assertTrue(unregistered.isEmpty(), "ViewModels missing from the Koin modules (koinViewModel fails at run time): $unregistered")
    }

    @Test
    fun every_repository_interface_is_registered() {
        val interfaces = productionSources.flatMap { file ->
            Regex("""(?m)^(?:internal\s+)?interface\s+(\w+Repository)\b""").findAll(file.code()).map { it.groupValues[1] }
        }.distinct()
        assertTrue(interfaces.isNotEmpty(), "No Repository interfaces found; the scan is broken")

        val registrations = diSources()
        val unregistered = interfaces.filter { !Regex("""(single|factory)\s*<\s*$it\s*>""").containsMatchIn(registrations) }
        assertTrue(unregistered.isEmpty(), "Repositories missing from the Koin modules: $unregistered")
    }

    /** Koin definitions live in the `di` module and in optional packages (`packages/<name>/.../di`). */
    private fun diSources(): String =
        sourcesWhere { it.contains("/di/src/") || it.contains("/packages/") && it.contains("/di/") }
            .joinToString("\n") { it.code() }

    private companion object {
        val IGNORED_DIRECTORIES = setOf("build", ".gradle", ".git", ".kotlin", "node_modules")
    }
}
