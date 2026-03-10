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

val sharedWasmBridgeDir = rootProject.layout.projectDirectory.dir(
    "shared/src/wasmJsMain/resources/wasm"
)
val rustDir = rootProject.layout.projectDirectory.dir("rust")
val webAppGeneratedWasmResources = layout.buildDirectory.dir("generated/wasmJsApp/wasm")
val webMatrixRuntimeGeneratedDir = layout.buildDirectory.dir("generated/webMatrixRuntime")

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

val generateRustWasmBindings = tasks.register<GenerateRustWasmBindingsTask>("generateRustWasmBindings") {
    rustProjectDir.set(rustDir)
    outputDir.set(layout.buildDirectory.dir("generated/rustWasmBindings"))
}

val generateWebMatrixRuntimeEntrypoint = tasks.register("generateWebMatrixRuntimeEntrypoint") {
    val outputFile = webMatrixRuntimeGeneratedDir.map { it.file("runtime/generated_runtime_entry.js") }
    outputs.file(outputFile)

    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            import { WasmClient } from "../mages_ffi.js";

            export async function createRuntimeClient({ homeserverUrl, baseStoreDir, accountId }) {
              if (typeof WasmClient.createAsync === "function") {
                return await WasmClient.createAsync(
                  homeserverUrl,
                  baseStoreDir,
                  accountId ?? undefined,
                );
              }

              return new WasmClient(homeserverUrl, baseStoreDir, accountId ?? undefined);
            }

            export async function restoreRuntimeClient({ homeserverUrl, baseStoreDir, accountId, sessionRecord }) {
              void sessionRecord;
              return await createRuntimeClient({ homeserverUrl, baseStoreDir, accountId });
            }

            export async function logoutRuntimeClient(client, sessionRecord) {
              void client;
              void sessionRecord;
              return false;
            }
            """.trimIndent()
        )
    }
}

val syncWasmAppResources = tasks.register<Sync>("syncWasmAppResources") {
    dependsOn(generateRustWasmBindings, generateWebMatrixRuntimeEntrypoint)
    from(generateRustWasmBindings.flatMap { it.outputDir })
    from(webMatrixRuntimeGeneratedDir)
    from(sharedWasmBridgeDir) {
        include(
            "mages_ffi_init.js",
            "mages_ffi_client.js",
            "web_matrix_facade.js",
            "session_store.js",
            "runtime/*.js",
        )
    }
    into(webAppGeneratedWasmResources)
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
    from(webAppGeneratedWasmResources) {
        into("wasm")
    }
}
