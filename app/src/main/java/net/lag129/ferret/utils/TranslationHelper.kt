package net.lag129.ferret.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationHelper {

    private val translator by lazy {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.JAPANESE)
                .build()
        )
    }

    suspend fun translate(text: String): Result<String> = runCatching {
        translator.downloadModelIfNeeded().await()
        translator.translate(text).await()
    }
}
