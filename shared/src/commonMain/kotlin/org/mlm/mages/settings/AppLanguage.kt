package org.mlm.mages.settings

import kotlinx.serialization.Serializable

@Serializable
enum class AppLanguage(val languageTag: String?) {
    System(null),
    English("en"),
    Spanish("es")
}

fun AppSettings.appLanguage(): AppLanguage =
    AppLanguage.entries.getOrElse(language) { AppLanguage.System }

fun AppSettings.appLanguageTagOrNull(): String? = appLanguage().languageTag

fun appLanguageTagOrDefault(languageIndex: Int?, defaultTag: String): String =
    languageIndex
        ?.let { AppLanguage.entries.getOrElse(it) { AppLanguage.System }.languageTag }
        ?: defaultTag
