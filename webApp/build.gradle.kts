import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.api.tasks.Sync
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val elementCallAar by configurations.creating {
    isTransitive = false
}

dependencies {
    elementCallAar(libs.element.call.embedded)
}

val sharedWasmBridgeDir = rootProject.layout.projectDirectory.dir(
    "shared/src/wasmJsMain/resources/wasm"
)
val rustDir = rootProject.layout.projectDirectory.dir("rust")
val webAppGeneratedWasmResources = layout.buildDirectory.dir("generated/wasmJsApp/wasm")
val webAppGeneratedRootResources = layout.buildDirectory.dir("generated/wasmJsApp/root")
val webAppGeneratedElementCallResources = layout.buildDirectory.dir("generated/wasmJsApp/element-call")
@DisableCachingByDefault(because = "Invokes external Rust tooling")
abstract class GenerateRustWasmBindingsTask @Inject constructor(
    private val execOps: ExecOperations,
) : DefaultTask() {
    @get:InputDirectory
    abstract val rustProjectDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        execOps.exec {
            workingDir = rustProjectDir.get().asFile
            commandLine("cargo", "build", "--target", "wasm32-unknown-unknown", "--release")
        }

        outputDir.get().asFile.mkdirs()

        execOps.exec {
            workingDir = rustProjectDir.get().asFile
            commandLine(
                "wasm-bindgen",
                "target/wasm32-unknown-unknown/release/mages_ffi.wasm",
                "--target", "bundler",
                "--out-dir", outputDir.get().asFile.absolutePath,
                "--out-name", "mages_ffi",
                "--typescript",
            )
        }
    }
}

abstract class GenerateWasmExternsTask : DefaultTask() {
    @get:InputFile
    abstract val dtsFile: RegularFileProperty

    @get:OutputFile
    abstract val outputKt: RegularFileProperty

    @TaskAction
    fun generate() {
        val content = dtsFile.get().asFile.readText()
        
        // Extract only the WasmClient class body
        val classMatch = Regex("""export class WasmClient\s*\{([^}]+(?:\{[^}]*\}[^}]*)*)\}""", RegexOption.DOT_MATCHES_ALL).find(content)
        if (classMatch == null) {
            outputKt.get().asFile.writeText("// Could not find WasmClient class")
            return
        }
        
        val classBody = classMatch.groupValues[1]
        
        val sb = StringBuilder()
        sb.appendLine("// AUTO-GENERATED from ${dtsFile.get().asFile.name} — do not edit")
        sb.appendLine("@file:JsModule(\"./wasm/mages_ffi.js\")")
        sb.appendLine()
        sb.appendLine("package org.mlm.mages.matrix")
        sb.appendLine()
        sb.appendLine("import kotlin.js.JsAny")
        sb.appendLine("import kotlin.js.JsName")
        sb.appendLine("import kotlin.js.Promise")
        sb.appendLine()
        
        // Parse static methods
        val staticMethods = Regex("""static\s+(\w+)\s*\(([^)]*)\)\s*:\s*([^;]+);""").findAll(classBody).toList()
        
        // Parse instance methods
        val instanceMethods = Regex("""(\w+)\s*\(([^)]*)\)\s*:\s*([^;]+);""").findAll(classBody)
            .filter { it.groupValues[1] !in setOf("free", "cancel", "pull", "start", "write", "abort", "close") }
            .filter { !it.value.contains("static ") }
            .toList()
        
        sb.appendLine("external class WasmClient {")
        
        // Static methods in companion
        if (staticMethods.isNotEmpty()) {
            sb.appendLine("    companion object {")
            for (m in staticMethods) {
                val name = m.groupValues[1]
                if (name == "createAsync") {
                    sb.appendLine("        fun createAsync(")
                    sb.appendLine("            homeserverUrl: String,")
                    sb.appendLine("            baseStoreDir: String,")
                    sb.appendLine("            accountId: String? = kotlin.js.definedExternally")
                    sb.appendLine("        ): Promise<JsAny?>")
                } else {
                    // Handle other static methods
                    val params = m.groupValues[2]
                    val returnType = m.groupValues[3]
                    val ktParams = convertParams(params)
                    val ktReturn = convertReturnType(returnType)
                    sb.appendLine("        fun $name($ktParams): $ktReturn")
                }
            }
            sb.appendLine("    }")
        }
        
        sb.appendLine()
        sb.appendLine("    fun free()")
        
        // Instance methods
        for (m in instanceMethods) {
            val name = m.groupValues[1]
            if (name == "free" || name == "createAsync") continue
            
            val params = m.groupValues[2]
            val returnType = m.groupValues[3]
            
            val ktName = snakeToCamel(name)
            val ktParams = convertParams(params)
            val ktReturn = convertReturnType(returnType)
            
            if (name != ktName) {
                sb.appendLine("    @JsName(\"$name\")")
            }
            
            sb.appendLine("    fun $ktName($ktParams): $ktReturn")
        }
        
        sb.appendLine("}")
        
        sb.appendLine()
        sb.appendLine("external fun asWasmClient(value: JsAny?): WasmClient")
        
        outputKt.get().asFile.parentFile?.mkdirs()
        outputKt.get().asFile.writeText(sb.toString())
    }
    
    companion object {
        fun snakeToCamel(name: String): String {
            if (!name.contains('_')) return name
            return buildString {
                var capitalizeNext = false
                for (ch in name) {
                    if (ch == '_') {
                        capitalizeNext = true
                    } else {
                        append(if (capitalizeNext) ch.uppercaseChar() else ch)
                        capitalizeNext = false
                    }
                }
            }
        }

        fun convertParamType(ts: String): String {
            val t = ts.trim()
            return when {
                t == "boolean" || t == "bool" -> "Boolean"
                t == "string" -> "String"
                t == "number" -> "Double"
                t == "boolean | null" || t == "boolean | undefined" -> "Boolean?"
                t == "number | null" || t == "number | undefined" -> "Double?"
                t == "string | null" || t == "string | undefined" -> "String?"
                else -> "JsAny?"
            }
        }

        fun convertReturnType(ts: String): String {
            val t = ts.trim()
            return when {
                t == "void" -> "Unit"
                t == "boolean" -> "Boolean"
                t == "string" -> "String"
                t == "number" -> "Double"
                t == "string | null" || t == "string | undefined" -> "String?"
                t == "boolean | null" -> "Boolean?"
                t == "number | null" -> "Double?"
                t.startsWith("Promise<") -> "Promise<JsAny?>"
                else -> "JsAny?"
            }
        }

        fun convertParams(ts: String): String {
            if (ts.isBlank()) return ""
            return ts.split(",").joinToString(", ") { param ->
                val parts = param.trim().split(":", limit = 2)
                if (parts.size < 2) return@joinToString "param: JsAny?"
                val rawName = parts[0].trim()
                val optional = rawName.endsWith("?")
                val tsName = rawName.removeSuffix("?")
                val ktName = snakeToCamel(tsName)
                val tsType = parts[1].trim()
                val ktType = convertParamType(tsType)
                if (optional) "$ktName: $ktType = kotlin.js.definedExternally"
                else "$ktName: $ktType"
            }
        }
    }
}

val generateRustWasmBindings = tasks.register<GenerateRustWasmBindingsTask>("generateRustWasmBindings") {
    rustProjectDir.set(rustDir)
    outputDir.set(layout.buildDirectory.dir("generated/rustWasmBindings"))
}

val generateWasmExterns = tasks.register<GenerateWasmExternsTask>("generateWasmExterns") {
    dtsFile.set(generateRustWasmBindings.flatMap { it.outputDir.file("mages_ffi.d.ts") })
    outputKt.set(rootProject.layout.projectDirectory.file("shared/src/wasmJsMain/kotlin/org/mlm/mages/matrix/WasmClientExterns.generated.kt"))
}

tasks.matching { it.name.startsWith("compileKotlin") }.configureEach {
    dependsOn(generateWasmExterns)
}

val syncWasmAppResources = tasks.register<Sync>("syncWasmAppResources") {
    dependsOn(generateRustWasmBindings)
    from(generateRustWasmBindings.flatMap { it.outputDir })
    into(webAppGeneratedWasmResources)
}

val extractElementCall = tasks.register<Copy>("extractElementCall") {
    from({ elementCallAar.files.map { zipTree(it) } }) {
        include("assets/element-call/**")
        eachFile { path = path.removePrefix("assets/") }
    }
    into(webAppGeneratedElementCallResources)
    includeEmptyDirs = false
}

val syncRootAppResources = tasks.register<Sync>("syncRootAppResources") {
    dependsOn(extractElementCall)
    from(webAppGeneratedElementCallResources)
    into(webAppGeneratedRootResources)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "mages"
        browser {
            commonWebpackConfig {
                outputFileName = "mages.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.kmp.settings.core)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

tasks.named<ProcessResources>("wasmJsProcessResources") {
    dependsOn(syncWasmAppResources)
    dependsOn(syncRootAppResources)
    from(webAppGeneratedWasmResources) {
        into("wasm")
    }
    from(webAppGeneratedRootResources)
}
