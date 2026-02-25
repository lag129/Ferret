package net.lag129.ferret.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

interface ITranslationHelper : AutoCloseable {
    suspend fun translate(text: String): Result<String>
}

class TranslationHelper : ITranslationHelper {

    private val translator by lazy {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.JAPANESE)
                .build()
        )
    }

    private val mutex = Mutex()

    override suspend fun translate(text: String): Result<String> = runCatching {
        mutex.withLock {
            translator.downloadModelIfNeeded().await()
        }
        translator.translate(text).await()
    }

    override fun close() {
        translator.close()
    }
}
