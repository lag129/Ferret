package net.lag129.ferret

import android.content.Context
import androidx.room.Room
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.cache.storage.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import net.lag129.ferret.db.CachedStatusDao
import net.lag129.ferret.db.RoomDatabase
import net.lag129.ferret.repository.MastodonRepository
import net.lag129.ferret.repository.MastodonRepositoryImpl
import net.lag129.ferret.repository.PreferencesRepository
import net.lag129.ferret.repository.PreferencesRepositoryImpl
import net.lag129.ferret.utils.DateUtils
import net.lag129.ferret.utils.DateUtilsImpl
import net.lag129.ferret.viewmodel.AuthViewModel
import net.lag129.ferret.viewmodel.PreferencesViewModel
import net.lag129.ferret.viewmodel.ProfileViewModel
import net.lag129.ferret.viewmodel.TimelineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {

    single<HttpClient> {
        val preferencesRepository = get<PreferencesRepository>()
        HttpClient(CIO) {
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
            install(createClientPlugin("BaseUrl") {
                onRequest { request, _ ->
                    val serverName = preferencesRepository.serverName.first()
                    request.url {
                        protocol = URLProtocol.HTTPS
                        host = serverName
                    }
                }
            })
        }
    }

    single<RoomDatabase> {
        Room.databaseBuilder(get(), RoomDatabase::class.java, "ferret_db").build()
    }

    single<CachedStatusDao> { get<RoomDatabase>().cachedStatusDao() }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    single<MastodonRepository> { MastodonRepositoryImpl(get()) }

    viewModel { TimelineViewModel(get(), get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { AuthViewModel(get()) }

    viewModel { PreferencesViewModel(get()) }

    single<DateUtils> { DateUtilsImpl(get()) }
}
