package net.lag129.ferret

import android.content.Context
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.lag129.ferret.db.CachedStatusDao
import net.lag129.ferret.db.RoomDatabase
import net.lag129.ferret.utils.DateUtils
import net.lag129.ferret.utils.DateUtilsImpl
import net.lag129.ferret.utils.ITranslationHelper
import net.lag129.ferret.utils.TranslationHelper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {

    single<HttpClient> {
        val preferencesRepository = get<PreferencesRepository>()
        val serverName = runBlocking { preferencesRepository.serverName.first() }
        HttpClient(CIO) {
            defaultRequest { url("https://$serverName/") }
            install(Auth) {
                bearer {
                    loadTokens {
                        val bearerToken = preferencesRepository.bearerToken.first()
                        BearerTokens(bearerToken, bearerToken)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpCache) {
                val context = get<Context>()
                publicStorage(FileStorage(File(context.cacheDir, "ktor_cache")))
                privateStorage(FileStorage(File(context.cacheDir, "ktor_private_cache")))
            }
        }
    }

    single<RoomDatabase> {
        Room.databaseBuilder(get(), RoomDatabase::class.java, "ferret_db").build()
    }

    single<CachedStatusDao> { get<RoomDatabase>().cachedStatusDao() }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    single<MastodonRepository> { MastodonRepositoryImpl(get()) }

    single<ITranslationHelper> { TranslationHelper() }

    viewModel { TimelineViewModel(get(), get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { AuthViewModel(get()) }

    single<DateUtils> { DateUtilsImpl(get()) }
}
