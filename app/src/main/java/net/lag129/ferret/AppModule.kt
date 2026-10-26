package net.lag129.ferret

import android.content.Context
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.lag129.ferret.db.CachedStatusDao
import net.lag129.ferret.db.RoomDatabase
import net.lag129.ferret.repository.MastodonRepository
import net.lag129.ferret.repository.MastodonRepositoryImpl
import net.lag129.ferret.repository.MastodonTimelineRepositoryImpl
import net.lag129.ferret.repository.MisskeyRepository
import net.lag129.ferret.repository.MisskeyRepositoryImpl
import net.lag129.ferret.repository.MisskeyTimelineRepositoryImpl
import net.lag129.ferret.repository.Platform
import net.lag129.ferret.repository.PreferencesRepository
import net.lag129.ferret.repository.PreferencesRepositoryImpl
import net.lag129.ferret.repository.TimelineRepository
import net.lag129.ferret.ui.screen.login.LoginViewModel
import net.lag129.ferret.ui.screen.profile.MyProfileViewModel
import net.lag129.ferret.ui.screen.profile.ProfileViewModel
import net.lag129.ferret.ui.screen.search.SearchViewModel
import net.lag129.ferret.ui.screen.timeline.TimelineViewModel
import net.lag129.ferret.utils.DateUtils
import net.lag129.ferret.utils.DateUtilsImpl
import net.lag129.ferret.viewmodel.PreferencesViewModel
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
        Room.databaseBuilder(get(), RoomDatabase::class.java, "ferret_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single<CachedStatusDao> { get<RoomDatabase>().cachedStatusDao() }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    single<MastodonRepository> { MastodonRepositoryImpl(get()) }

    single<MisskeyRepository> { MisskeyRepositoryImpl(get()) }

    factory<TimelineRepository> {
        when (runBlocking { get<PreferencesRepository>().platform.first() }) {
            Platform.MASTODON -> MastodonTimelineRepositoryImpl(get())
            Platform.MISSKEY -> MisskeyTimelineRepositoryImpl(get())
        }
    }

    viewModel { TimelineViewModel(get(), get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { MyProfileViewModel(get()) }

    viewModel { LoginViewModel(get()) }

    viewModel { PreferencesViewModel(get()) }

    viewModel { SearchViewModel(get()) }

    single<DateUtils> { DateUtilsImpl(get()) }
}
